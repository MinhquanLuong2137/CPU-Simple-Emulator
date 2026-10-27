# 8-bit RISC-V Inspired CPU Emulator

A most-simple, modular 8-bit CPU simulator written in Java. This project models a simple Von Neumann computer architecture and executes custom machine bytecode using Object-Oriented Programming (OOP) principles and the Command Design Pattern.

---

## Architecture Overview

The system is split into three main packages:

1. **`com.cpu.hardware`**: Hardware components representing physical CPU structures.
* **`Memory`**: 256-byte RAM array (byte-addressable from `0x00` to `0xFF`).
* **`RegisterFile`**: 4 general-purpose 8-bit registers (`R0`–`R3`), a Program Counter (`PC`), and a `ZeroFlag`.
* **`ALU`**: Arithmetic Logic Unit supporting 8-bit operations (`ADD`, `SUB`, `AND`, `OR`, `XOR`) with bitwise wrapping (`& 0xFF`).
* **`CPU`**: Central controller orchestrating the **Fetch-Decode-Execute** cycle.


2. **`com.cpu.instruction`**: Instruction set architecture (ISA) implementation.
* **`Instruction`**: Abstract base class defining the `execute(CPU cpu)` interface (Command Pattern).
* Concrete instruction classes: `LoadConstInstruction`, `AddInstruction`, `SubInstruction`, `JumpInstruction`, and `HaltInstruction`.


3. **`com.cpu.app`**: Entry point (`Main.java`) to load and run sample bytecode programs.

---

## 📑 Instruction Set Architecture (ISA)

The CPU operates on a 3-operand format for arithmetic instructions, similar to RISC-V.

| Opcode | Mnemonic | Operands | Description |
| --- | --- | --- | --- |
| `0x20` | `LOAD_CONST` | `destReg, value` | Loads an 8-bit literal `value` into `destReg`. |
| `0x30` | `ADD` | `rd, rs1, rs2` | Adds `rs1` + `rs2`, stores result in `rd`, updates `ZeroFlag`. |
| `0x40` | `SUB` | `rd, rs1, rs2` | Subtracts `rs1` - `rs2`, stores result in `rd`, updates `ZeroFlag`. |
| `0x10` | `JUMP` | `targetAddress` | Sets the Program Counter (`PC`) to `targetAddress`. |
| `0xFF` | `HALT` | *None* | Stops CPU execution loop. |

---

## 📁 Project Structure

```text
minhquanluong2137-cpu-simple-emulator/
└── src/
    └── com/
        └── cpu/
            ├── app/
            │   └── Main.java
            ├── hardware/
            │   ├── ALU.java
            │   ├── CPU.java
            │   ├── Memory.java
            │   └── RegisterFile.java
            └── instruction/
                ├── Instruction.java
                ├── LoadConstInstruction.java
                ├── AddInstruction.java
                ├── SubInstruction.java
                ├── JumpInstruction.java
                └── HaltInstruction.java

```

---

## How It Works: Fetch-Decode-Execute Cycle

Each call to `CPU.step()` performs one clock cycle:

1. **Fetch**: Reads the bytecode byte at `RAM[PC]` and increments the `PC`.
2. **Decode**: Matches the byte against known opcodes, reads any required operand bytes from memory, and instantiates the corresponding `Instruction` object.
3. **Execute**: Calls `Instruction.execute(this)`, modifying register states, RAM, or control flags.

---

## Getting Started

### Prerequisites

* **JDK 8** or higher.

### Compilation

From the root directory (`minhquanluong2137-cpu-simple-emulator/`), compile all Java source files:

```bash
javac -d bin src/com/cpu/*/*.java

```

### Running the Emulator

Execute the main application class:

```bash
java -cp bin com.cpu.app.Main

```

---

## 💻 Sample Program (`Main.java`)

The default program calculates `10 + 20 = 30` and stores the result in register `R2`:

```java
int[] program = {
    0x20, 0, 10,   // LOAD_CONST R0, 10
    0x20, 1, 20,   // LOAD_CONST R1, 20
    0x30, 2, 0, 1, // ADD R2, R0, R1
    0xFF           // HALT
};

```

**Expected Output:**

```text
10   (R0)
20   (R1)
30   (R2)
0    (R3)

```

## 📜 License

This project is licensed under the **GNU General Public License v3.0** - see the [LICENSE](LICENSE) file for details.
