package com.cpu.instruction;
import com.cpu.hardware.CPU;

public class BeqInstruction extends  Instruction {

    private final int rs1;
    private final int rs2;
    private final int targetAddress;

    public BeqInstruction(int rs1, int rs2, int targetAddress) {
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.targetAddress = targetAddress;
    }

    @Override
    public void execute(CPU cpu) {
        int a = cpu.getReg().getRegister(rs1);
        int b = cpu.getReg().getRegister(rs2);

        if (a - b == 0){
            cpu.getReg().setProgramCounter(targetAddress);
        }

    }

}
