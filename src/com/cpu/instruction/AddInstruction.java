package com.cpu.instruction;

import com.cpu.hardware.CPU;

public class AddInstruction extends Instruction {
    private final int rd;
    private final int rs1;
    private final int rs2;

    public AddInstruction(int rd, int rs1, int rs2) {
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
    }
    @Override
    public void execute(CPU cpu) {
        int a = cpu.getReg().getRegister(rs1);
        int b = cpu.getReg().getRegister(rs2);
        int ans = cpu.getALU().add(a, b);
        cpu.getReg().setRegister(rd, ans);
        cpu.getReg().setZeroFlag(ans == 0);
    }


}
