package com.cpu.instruction;

import com.cpu.hardware.CPU;

public class BltInstruction extends BTypeInstruction {
    public BltInstruction(int rs1, int rs2, int target) {
        super(rs1, rs2, target);
    }
    @Override
    public boolean condition(int a, int b) {
        return a < b;
    }
}
