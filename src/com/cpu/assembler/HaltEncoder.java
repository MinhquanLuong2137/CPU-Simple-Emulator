package com.cpu.assembler;
import java.util.List;
public class HaltEncoder implements InstructionEncoder {
    private final int opcode;

    public HaltEncoder(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public void encode(String[] args, List<Integer> output) {
        output.add(opcode);
    }

}
