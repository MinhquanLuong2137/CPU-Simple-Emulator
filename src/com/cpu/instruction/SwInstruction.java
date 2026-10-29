package com.cpu.instruction;

import com.cpu.hardware.CPU;

public class SwInstruction extends Instruction{
    private final int rd;
    private final int offset;
    private final int rs;

    public SwInstruction(int rd, int offset, int rs) {
        this.rd = rd;
        this.offset = offset;
        this.rs = rs;
    }

    @Override
    public void execute(CPU cpu) {
        int address = (cpu.getReg().getRegister(rs) + offset ) & 0xFF;
        cpu.getMemory().write(address, cpu.getReg().getRegister(rd));
    }
}
