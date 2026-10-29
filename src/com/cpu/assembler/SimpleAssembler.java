package com.cpu.assembler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleAssembler {
    private static final Map<String, InstructionEncoder> encoders = new HashMap<>();

    static {
        encoders.put("OVER", new HaltEncoder(0xFF));
        encoders.put("TELE", new JumpEncoder(0x10));
        encoders.put("LC",   new LoadConsEncoder(0x20));
        encoders.put("ADD",  new RTypeEncoder(0x30));
        encoders.put("AND",  new RTypeEncoder(0x31));
        encoders.put("OR",   new RTypeEncoder(0x32));
        encoders.put("XOR",  new RTypeEncoder(0x33));
        encoders.put("SLL",  new RTypeEncoder(0x34));
        encoders.put("SRL",  new RTypeEncoder(0x35));
        encoders.put("SUB",  new RTypeEncoder(0x40));
        encoders.put("BEQ",  new BTypeEncoder(0x50));
        encoders.put("BLT",  new BTypeEncoder(0x51));
        encoders.put("BGE",  new BTypeEncoder(0x52));
        encoders.put("LW",   new MemoryEncoder(0x60));
        encoders.put("SW",   new MemoryEncoder(0x70));
    }

    public static int[] assemble(String sourceCode) {
        List<Integer> bytecode = new ArrayList<>();

        String[] lines = sourceCode.split("\\R");

        for (String line : lines) {
            line = line.replace('\u00a0', ' ').trim();

            if (line.isEmpty() || line.startsWith("#") || line.startsWith(";")) {
                continue;
            }

            if (line.contains("#")) line = line.substring(0, line.indexOf("#")).trim();
            if (line.contains(";")) line = line.substring(0, line.indexOf(";")).trim();

            String[] parts = line.split("\\s+", 2);
            String mnemonic = parts[0].toUpperCase();
            String[] args = parts.length > 1 ? parts[1].split(",") : new String[0];

            for (int i = 0; i < args.length; i++) {
                args[i] = args[i].trim();
            }

            InstructionEncoder encoder = encoders.get(mnemonic);
            if (encoder == null) {
                throw new IllegalArgumentException("Unknown instruction mnemonic: " + mnemonic);
            }

            encoder.encode(args, bytecode);
        }

        return bytecode.stream().mapToInt(i -> i).toArray();
    }
}