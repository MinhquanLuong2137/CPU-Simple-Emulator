package com.cpu.instruction;

import com.cpu.hardware.CPU;

public abstract class Instruction {
    public abstract void execute(CPU cpu);
}
