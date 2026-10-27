package com.cpu.instruction;

import com.cpu.hardware.CPU;

public class JumpInstruction extends Instruction{
    private final int targetAddress;

    public JumpInstruction(int targetAddress) {
        this.targetAddress = targetAddress;
    }

    @Override
    public void execute(CPU cpu) {
        cpu.getReg().setProgramCounter(targetAddress);
    }

}
