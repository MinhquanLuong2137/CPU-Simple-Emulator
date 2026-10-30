# 8-Bit RISC-V Inspired CPU Emulator & Assembler

A lightweight, modular 8-bit CPU emulator and software assembler written in Java. The project simulates a Von Neumann computer architecture and provides an assembly toolchain using Object-Oriented Programming (OOP) principles, the Command Pattern for instruction execution, and the Strategy Pattern for assembly encoding.

---

## Technical Specifications

* **Architecture:** 8-bit Von Neumann model
* **Memory:** 256-byte byte-addressable RAM (`0x00` – `0xFF`)
* **Registers:** 4 General Purpose Registers (`R0`–`R3`), Program Counter (`PC`), Zero Flag (`ZF`)
* **Execution Pipeline:** Fetch-Decode-Execute cycle
* **Assembler:** Multi-format parser with case-insensitive mnemonic matching and flexible whitespace handling

---

## Package Architecture

The source code is organized into four distinct packages:

1. **`com.cpu.hardware`**: Core hardware structures.
* `CPU`: Central controller executing the Fetch-Decode-Execute pipeline.
* `Memory`: 256-byte RAM encapsulation.
* `RegisterFile`: Storage for general-purpose registers, `PC`, and flags.
* `ALU`: Arithmetic and Logic Unit executing 8-bit operations with bitwise wrapping (`& 0xFF`).


2. **`com.cpu.instruction`**: Hardware instruction implementations.
* `Instruction`: Abstract command interface (`execute(CPU cpu)`).
* Abstractions: `RTypeInstruction`, `BTypeInstruction`, etc.
* Concrete implementations for each opcode.


3. **`com.cpu.assembler`**: Software assembly toolchain.
* `SimpleAssembler`: Orchestrates assembly source translation into machine bytecode.
* `OperandParser`: Utility for parsing registers, immediate values, and memory offset formats (`offset(rs)`).
* `InstructionEncoder`: Strategy interface and concrete encoders (`RTypeEncoder`, `BTypeEncoder`, `MemoryEncoder`, `LoadConsEncoder`, `JumpEncoder`, `HaltEncoder`).


4. **`com.cpu.app`**: Application entry point.
* `Main`: Interactive Command Line Interface (CLI) supporting direct bytecode execution or raw Assembly source input.



---

## Instruction Set Architecture (ISA)

The table below lists all supported instructions, their opcodes, operands, and operations:

| Category | Mnemonic | Opcode (Hex) | Operands | Description |
| --- | --- | --- | --- | --- |
| **Control** | `OVER` / `HALT` | `0xFF` | *None* | Halts CPU execution loop. |
|  | `TELE` / `JUMP` | `0x10` | `target` | Sets `PC` to `targetAddress`. |
| **Immediate** | `LC` | `0x20` | `rd, imm` | Loads 8-bit literal `imm` into `rd`. |
| **Arithmetic / Logic** | `ADD` | `0x30` | `rd, rs1, rs2` | `rd = rs1 + rs2` |
|  | `AND` | `0x31` | `rd, rs1, rs2` | `rd = rs1 & rs2` |
|  | `OR` | `0x32` | `rd, rs1, rs2` | `rd = rs1 | rs2` |
|  | `XOR` | `0x33` | `rd, rs1, rs2` | `rd = rs1 ^ rs2` |
|  | `SLL` | `0x34` | `rd, rs1, rs2` | `rd = rs1 << rs2` (Shift Left) |
|  | `SRL` | `0x35` | `rd, rs1, rs2` | `rd = rs1 >> rs2` (Shift Right) |
|  | `SUB` | `0x40` | `rd, rs1, rs2` | `rd = rs1 - rs2` |
| **Branch** | `BEQ` | `0x50` | `rs1, rs2, target` | Branch to `target` if `rs1 == rs2` |
|  | `BLT` | `0x51` | `rs1, rs2, target` | Branch to `target` if `rs1 < rs2` |
|  | `BGE` | `0x52` | `rs1, rs2, target` | Branch to `target` if `rs1 >= rs2` |
| **Memory** | `LW` | `0x60` | `rd, offset(rs1)` | Load from `RAM[rs1 + offset]` into `rd` |
|  | `SW` | `0x70` | `rs2, offset(rs1)` | Store from `rs2` to `RAM[rs1 + offset]` |

---

## Project Structure

```text
.
└── src/
    └── com/
        └── cpu/
            ├── app/
            │   └── Main.java
            ├── assembler/
            │   ├── BTypeEncoder.java
            │   ├── HaltEncoder.java
            │   ├── InstructionEncoder.java
            │   ├── JumpEncoder.java
            │   ├── LoadConsEncoder.java
            │   ├── MemoryEncoder.java
            │   ├── OperandParser.java
            │   ├── RTypeEncoder.java
            │   └── SimpleAssembler.java
            ├── hardware/
            │   ├── ALU.java
            │   ├── CPU.java
            │   ├── Memory.java
            │   └── RegisterFile.java
            └── instruction/
                ├── BTypeInstruction.java
                ├── Instruction.java
                ├── RTypeInstruction.java
                └── ... (concrete instruction implementations)

```

---

## Build and Execution

### Prerequisites

* Java Development Kit (JDK) 17 or higher.

### Compilation

Compile all source files into the `bin` directory:

```bash
javac -d bin src/com/cpu/*/*.java

```

### Running

Execute the application:

```bash
java -cp bin com.cpu.app.Main

```

---

## Usage Example

Upon running `Main`, select input mode `2` to assemble and execute raw assembly code:

```text
Select Input Mode:
1. Machine Code (Bytecode array)
2. Assembly Code (Text instructions)
Enter your choice (1 or 2): 2

Enter Assembly Code line by line (type 'END' or press Enter on an empty line to finish):
lc r0, 10
lc r1, 25
add r2, r0, r1
sw r2, 5(r0)
lw r3, 5(r0)
over

```

### Execution Output

```text
=== EXECUTING PROGRAM ===

=== REGISTERS ===
R0: 10
R1: 25
R2: 35
R3: 35
===================
=== RAM DUMP ===
Ram[0]: 32 | Ram[1]: 0 | Ram[2]: 10 | Ram[3]: 32 | Ram[4]: 1 | Ram[5]: 25 | Ram[6]: 48 | Ram[7]: 2 | Ram[8]: 0 | Ram[9]: 1 | 
Ram[10]: 64 | Ram[11]: 3 | Ram[12]: 1 | Ram[13]: 0 | Ram[14]: 112 | Ram[15]: 35 | ...

```

---

## Live Demo
Mô phỏng CPU 8-bit trực tiếp trên trình duyệt: [Click vào đây để chạy Web Emulator](https://minhquanluong2137.github.io/CPU-Simple-Emulator/)


## License

This project is licensed under the [GNU General Public License v3.0](https://www.google.com/search?q=LICENSE).
