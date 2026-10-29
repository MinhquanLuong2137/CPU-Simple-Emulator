package com.cpu.instruction;
import com.cpu.hardware.CPU;
public abstract class BTypeInstruction extends Instruction {
    public final int rs1, rs2, targetAddress;
    public BTypeInstruction(int rs1, int rs2, int targetAddress) {
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.targetAddress = targetAddress;
    }

    @Override
    public void execute(CPU cpu) {
        int a = cpu.getReg().getRegister(rs1);
        int b = cpu.getReg().getRegister(rs2);
        if(condition(a, b)){
            cpu.getReg().setProgramCounter(targetAddress);
        }
    }

    protected abstract boolean condition(int a, int b);

}
