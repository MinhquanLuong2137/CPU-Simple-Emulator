package com.cpu.instruction;
import com.cpu.hardware.ALU;

public class SrlInstruction extends RTypeInstruction{
    public SrlInstruction(int rd, int rs1, int rs2) {
        super(rd, rs1, rs2);
    }

    @Override
    protected int compute(ALU alu, int a, int b) {
        return alu.srl(a, b);
    }
}
