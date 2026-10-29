package com.cpu.assembler;
import java.util.List;
public class MemoryEncoder implements InstructionEncoder {
    private final int opcode;
    public MemoryEncoder(int opcode) {
        this.opcode = opcode;
    }
    @Override
    public void encode(String[] args, List<Integer> output) {
        int reg =  OperandParser.parseRegister(args[0]);
        int[] mem = OperandParser.parseOffsetRegister(args[1]);

        output.add(opcode);
        output.add(reg);
        output.add(mem[0]);
        output.add(mem[1]);
    }
}
