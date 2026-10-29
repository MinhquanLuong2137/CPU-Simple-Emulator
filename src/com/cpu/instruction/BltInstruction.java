package com.cpu.instruction;

import com.cpu.hardware.CPU;

public class BltInstruction extends Instruction {
    private final int rs1;
    private final int rs2;
    private final int targetAdress;

    public BltInstruction(int rs1, int rs2, int targetAdress) {
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.targetAdress = targetAdress;
    }

    @Override
    public void execute(CPU cpu) {
        int a = cpu.getReg().getRegister(rs1);
        int b = cpu.getReg().getRegister(rs2);

        if(a < b){
            cpu.getReg().setProgramCounter(targetAdress);
        }
    }

}
