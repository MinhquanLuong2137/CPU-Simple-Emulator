package com.cpu.instruction;

import com.cpu.hardware.CPU;

public class LoadConstInstruction extends Instruction {

    private final int destRegister;
    private final int val;
    public LoadConstInstruction(int destRegister, int val) {
        this.val = val;
        this.destRegister = destRegister;
    }

    @Override
    public void execute(CPU cpu) {
        cpu.getReg().setRegister(destRegister, val);
    }

}
