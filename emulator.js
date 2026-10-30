(function () {
  "use strict";

  const OPCODES = Object.freeze({
    OVER: 0xff,
    TELE: 0x10,
    LC: 0x20,
    ADD: 0x30,
    AND: 0x31,
    OR: 0x32,
    XOR: 0x33,
    SLL: 0x34,
    SRL: 0x35,
    SUB: 0x40,
    BEQ: 0x50,
    BLT: 0x51,
    BGE: 0x52,
    LW: 0x60,
    SW: 0x70
  });

  function javaTrim(value) {
    return value.replace(/^[\x00-\x20]+|[\x00-\x20]+$/g, "");
  }

  function parseJavaInt(value, radix) {
    const text = value;
    const pattern = radix === 16 ? /^[+-]?[0-9a-f]+$/i : /^[+-]?[0-9]+$/;
    if (!pattern.test(text)) {
      throw new Error(`Invalid integer: "${value}"`);
    }

    const parsed = Number.parseInt(text, radix);
    if (!Number.isSafeInteger(parsed) || parsed < -2147483648 || parsed > 2147483647) {
      throw new Error(`Invalid integer: "${value}"`);
    }
    return parsed;
  }

  function parseRegister(register) {
    return parseJavaInt(javaTrim(register).toUpperCase().replace(/R/g, ""), 10);
  }

  function parseImmediate(immediate) {
    const cleaned = javaTrim(immediate);
    if (cleaned.startsWith("0x") || cleaned.startsWith("0X")) {
      return parseJavaInt(cleaned.substring(2), 16);
    }
    return parseJavaInt(cleaned, 10);
  }

  function parseOffsetRegister(argument) {
    const cleaned = javaTrim(argument);
    const openParen = cleaned.indexOf("(");
    const closeParen = cleaned.indexOf(")");
    if (openParen < 0 || closeParen < openParen) {
      throw new Error(`Invalid memory operand: "${argument}"`);
    }
    const offset = parseJavaInt(cleaned.substring(0, openParen), 10);
    const register = parseRegister(cleaned.substring(openParen + 1, closeParen));
    return { offset, register };
  }

  function requireArgument(args, index, mnemonic) {
    if (index >= args.length) {
      throw new Error(`Missing operand for instruction: ${mnemonic}`);
    }
    return args[index];
  }

  function assemble(sourceCode) {
    const output = [];
    const lines = String(sourceCode).split(/\r\n|[\n\r\u000B\f\u001C-\u001E\u0085\u2028\u2029]/);

    for (let line of lines) {
      line = javaTrim(line.replace(/\u00a0/g, " "));
      if (line.length === 0 || line.startsWith("#") || line.startsWith(";")) {
        continue;
      }

      const hashIndex = line.indexOf("#");
      if (hashIndex >= 0) {
        line = javaTrim(line.substring(0, hashIndex));
      }
      const semicolonIndex = line.indexOf(";");
      if (semicolonIndex >= 0) {
        line = javaTrim(line.substring(0, semicolonIndex));
      }

      const splitIndex = line.search(/\s/);
      const mnemonic = (splitIndex < 0 ? line : line.substring(0, splitIndex)).toUpperCase();
      const operandText = splitIndex < 0 ? null : javaTrim(line.substring(splitIndex));
      const args = operandText === null ? [] : operandText.split(",").map(javaTrim);
      const opcode = OPCODES[mnemonic];
      if (opcode === undefined) {
        throw new Error(`Unknown instruction mnemonic: ${mnemonic}`);
      }

      switch (mnemonic) {
        case "OVER":
          output.push(opcode);
          break;
        case "TELE":
          output.push(opcode, parseImmediate(requireArgument(args, 0, mnemonic)));
          break;
        case "LC":
          output.push(
            opcode,
            parseRegister(requireArgument(args, 0, mnemonic)),
            parseImmediate(requireArgument(args, 1, mnemonic))
          );
          break;
        case "ADD":
        case "AND":
        case "OR":
        case "XOR":
        case "SLL":
        case "SRL":
        case "SUB":
          output.push(
            opcode,
            parseRegister(requireArgument(args, 0, mnemonic)),
            parseRegister(requireArgument(args, 1, mnemonic)),
            parseRegister(requireArgument(args, 2, mnemonic))
          );
          break;
        case "BEQ":
        case "BLT":
        case "BGE":
          output.push(
            opcode,
            parseRegister(requireArgument(args, 0, mnemonic)),
            parseRegister(requireArgument(args, 1, mnemonic)),
            parseImmediate(requireArgument(args, 2, mnemonic))
          );
          break;
        case "LW":
        case "SW": {
          const register = parseRegister(requireArgument(args, 0, mnemonic));
          const memory = parseOffsetRegister(requireArgument(args, 1, mnemonic));
          output.push(opcode, register, memory.offset, memory.register);
          break;
        }
      }
    }
    return output;
  }

  class RegisterFile {
    constructor() {
      this.registers = [0, 0, 0, 0];
      this.programCounter = 0;
      this.zeroFlag = false;
    }

    getRegister(index) {
      if (index >= 0 && index < this.registers.length) {
        return this.registers[index];
      }
      console.log("Register index out of range");
      return 0;
    }

    setRegister(index, value) {
      if (index >= 0 && index < this.registers.length) {
        this.registers[index] = value & 0xff;
      }
    }
  }

  class Memory {
    constructor() {
      this.ram = new Array(256).fill(0);
    }

    write(address, data) {
      if (address >= 0 && address < 256) {
        this.ram[address] = data;
      } else {
        console.log("Error: address out of range");
      }
    }

    read(address) {
      if (address >= 0 && address < 256) {
        return this.ram[address];
      }
      console.log("Error: address out of range");
      return 0;
    }
  }

  class ALU {
    add(a, b) { return (a + b) & 0xff; }
    sub(a, b) { return (a - b) & 0xff; }
    and(a, b) { return (a & b) & 0xff; }
    or(a, b) { return (a | b) & 0xff; }
    xor(a, b) { return (a ^ b) & 0xff; }
    sll(a, b) { return (a << (b & 0x07)) & 0xff; }
    srl(a, b) { return (a >> (b & 0x07)) & 0xff; }
  }

  class CPU {
    constructor() {
      this.reg = new RegisterFile();
      this.memory = new Memory();
      this.alu = new ALU();
      this.running = true;
    }

    loadProgram(program, startAddress) {
      for (let i = 0; i < program.length; i++) {
        this.memory.write(startAddress + i, program[i]);
      }
      this.reg.programCounter = startAddress;
    }

    decode(rawInstruction) {
      const readOperand = () => {
        const value = this.memory.read(this.reg.programCounter);
        this.reg.programCounter++;
        return value;
      };

      switch (rawInstruction) {
        case 0xff:
          return () => { this.running = false; };
        case 0x10: {
          const target = readOperand();
          return () => { this.reg.programCounter = target; };
        }
        case 0x20: {
          const destination = readOperand();
          const value = readOperand();
          return () => { this.reg.setRegister(destination, value); };
        }
        case 0x30:
        case 0x31:
        case 0x32:
        case 0x33:
        case 0x34:
        case 0x35:
        case 0x40: {
          const destination = readOperand();
          const source1 = readOperand();
          const source2 = readOperand();
          const operations = {
            0x30: this.alu.add,
            0x31: this.alu.and,
            0x32: this.alu.or,
            0x33: this.alu.xor,
            0x34: this.alu.sll,
            0x35: this.alu.srl,
            0x40: this.alu.sub
          };
          return () => {
            const result = operations[rawInstruction].call(
              this.alu,
              this.reg.getRegister(source1),
              this.reg.getRegister(source2)
            );
            this.reg.setRegister(destination, result);
            this.reg.zeroFlag = result === 0;
          };
        }
        case 0x50:
        case 0x51:
        case 0x52: {
          const source1 = readOperand();
          const source2 = readOperand();
          const target = readOperand();
          return () => {
            const a = this.reg.getRegister(source1);
            const b = this.reg.getRegister(source2);
            const taken = rawInstruction === 0x50 ? a === b
              : rawInstruction === 0x51 ? a < b
                : a >= b;
            if (taken) {
              this.reg.programCounter = target;
            }
          };
        }
        case 0x60: {
          const destination = readOperand();
          const offset = readOperand();
          const source = readOperand();
          return () => {
            const address = (this.reg.getRegister(source) + offset) & 0xff;
            this.reg.setRegister(destination, this.memory.read(address));
          };
        }
        case 0x70: {
          const source2 = readOperand();
          const offset = readOperand();
          const source1 = readOperand();
          return () => {
            const address = (this.reg.getRegister(source1) + offset) & 0xff;
            this.memory.write(address, this.reg.getRegister(source2));
          };
        }
        default:
          console.log("Unknown Instruction");
          return null;
      }
    }

    step() {
      if (!this.running) {
        return;
      }
      const rawInstruction = this.memory.read(this.reg.programCounter);
      this.reg.programCounter++;
      const instruction = this.decode(rawInstruction);
      if (instruction !== null) {
        instruction();
      }
    }
  }

  let cpu = new CPU();

  function getState() {
    return {
      registers: cpu.reg.registers.slice(),
      PC: cpu.reg.programCounter,
      ZeroFlag: cpu.reg.zeroFlag,
      running: cpu.running,
      RAM: cpu.memory.ram.slice()
    };
  }

  window.cpuEmulator = Object.freeze({
    assemble(code) {
      const program = assemble(code);
      if (program.length > 256) {
        throw new Error("Program exceeds the 256-byte RAM capacity.");
      }
      cpu = new CPU();
      cpu.loadProgram(program, 0);
      return getState();
    },
    step() {
      cpu.step();
      return getState();
    },
    run(maxSteps) {
      const stepLimit = maxSteps === undefined ? 100000 : maxSteps;
      let steps = 0;
      while (cpu.running && steps < stepLimit) {
        cpu.step();
        steps++;
      }
      if (cpu.running) {
        throw new Error(`Execution stopped after ${stepLimit} steps; the program may not halt.`);
      }
      return getState();
    },
    reset() {
      cpu = new CPU();
      return getState();
    },
    getState
  });
})();
