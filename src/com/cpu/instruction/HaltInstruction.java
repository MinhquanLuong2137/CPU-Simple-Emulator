package com.cpu.instruction;

import com.cpu.hardware.CPU;

public class HaltInstruction extends Instruction {
    @Override
    public void execute(CPU cpu) {
        cpu.stop();
    }
}
