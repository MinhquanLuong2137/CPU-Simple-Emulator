package com.cpu.assembler;
import java.util.List;

public class RTypeEncoder implements InstructionEncoder{
    private final int opcode;

    public RTypeEncoder(int opcode){
        this.opcode = opcode;
    }

    @Override
    public void encode(String[] args, List<Integer> output){
        int rd = OperandParser.parseRegister(args[0]);
        int rs1 = OperandParser.parseRegister(args[1]);
        int rs2 = OperandParser.parseRegister(args[2]);

        output.add(opcode);
        output.add(rd);
        output.add(rs1);
        output.add(rs2);
    }
}
