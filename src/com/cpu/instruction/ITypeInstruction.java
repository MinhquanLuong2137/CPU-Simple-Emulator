package com.cpu.instruction;

import com.cpu.hardware.ALU;
import com.cpu.hardware.CPU;

public abstract class ITypeInstruction extends Instruction {
    protected final int rd;
    protected final int imm;
    protected final int rs;

    public ITypeInstruction(int rd, int imm, int rs) {
        this.rd = rd;
        this.imm = imm;
        this.rs = rs;
    }

    @Override
    public void execute(CPU cpu) {
        int a = cpu.getReg().getRegister(rs);

        int result = compute(cpu.getALU(), a, imm);

        cpu.getReg().setRegister(rd, result);
        cpu.getReg().setZeroFlag(result == 0);
    }

    protected abstract int compute(ALU alu, int a, int imm);
}
