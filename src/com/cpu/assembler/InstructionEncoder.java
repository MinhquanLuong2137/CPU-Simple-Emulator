package com.cpu.assembler;
import java.util.List;
public interface InstructionEncoder {
    void encode(String[] args, List<Integer> output);
}
