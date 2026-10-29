package com.cpu.instruction;
import com.cpu.hardware.CPU;

public class BeqInstruction extends BTypeInstruction {

    public BeqInstruction(int rs1, int rs2, int target) {
        super(rs1, rs2, target);
    }
    @Override
    protected boolean condition(int a, int b) {
        return a == b;
    }
}
