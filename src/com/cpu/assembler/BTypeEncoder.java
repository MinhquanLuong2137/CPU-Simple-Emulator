package com.cpu.assembler;
import java.util.List;
public class BTypeEncoder implements InstructionEncoder {
    private final int opcode;
    public BTypeEncoder(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public void encode(String[] args, List<Integer> output) {
        int rs1 = OperandParser.parseRegister(args[0]);
        int rs2 = OperandParser.parseRegister(args[1]);
        int target = OperandParser.parseImmediate(args[2]);
        output.add(opcode);
        output.add(rs1);
        output.add(rs2);
        output.add(target);
    }
}
