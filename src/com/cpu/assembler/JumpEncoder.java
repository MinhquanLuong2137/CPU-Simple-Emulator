package com.cpu.assembler;

import java.util.List;

public class JumpEncoder implements InstructionEncoder {
    private final int opcode;

    public JumpEncoder(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public void encode(String[] args, List<Integer> output) {
        output.add(opcode);
        output.add(OperandParser.parseImmediate(args[0]));
    }
}