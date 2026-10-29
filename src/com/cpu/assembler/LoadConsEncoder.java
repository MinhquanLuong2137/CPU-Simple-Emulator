package com.cpu.assembler;
import java.util.List;
public class LoadConsEncoder implements InstructionEncoder {
    private final int opcode;
    public LoadConsEncoder(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public void encode(String[] args, List<Integer> output) {
        int destReg = OperandParser.parseRegister(args[0]);
        int val = OperandParser.parseImmediate(args[1]);

        output.add(opcode);
        output.add(destReg);
        output.add(val);
    }

}
