package com.cpu.instruction;

import com.cpu.hardware.ALU;
import com.cpu.hardware.CPU;

public abstract class RTypeInstruction extends Instruction {
    protected final int rd;
    protected final int rs1;
    protected final int rs2;

    public RTypeInstruction(int rd, int rs1, int rs2) {
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
    }

    @Override
    public void execute(CPU cpu) {
        int a = cpu.getReg().getRegister(rs1);
        int b = cpu.getReg().getRegister(rs2);
        int result = compute(cpu.getALU(), a, b);
        cpu.getReg().setRegister(rd, result);
        cpu.getReg().setZeroFlag(result == 0);
    }

    protected abstract int compute(ALU alu, int a, int b);

}