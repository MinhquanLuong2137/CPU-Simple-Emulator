package com.cpu.app;

import com.cpu.assembler.SimpleAssembler;
import com.cpu.hardware.CPU;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CPU cpu = new CPU();
        int[] program = new int[0];

        System.out.println("Select Input Mode:");
        System.out.println("1. Machine Code (Bytecode array)");
        System.out.println("2. Assembly Code (Text instructions)");
        System.out.print("Enter your choice (1 or 2): ");

        int choice = 0;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid selection. Defaulting to Assembly mode.");
            choice = 2;
        }

        if (choice == 1) {
            System.out.println("\nEnter Machine Code separated by spaces or commas (e.g., 0x20 0 15 0x20 1 25 0x30 2 0 1 0xFF):");
            String input = scanner.nextLine().trim();
            String[] tokens = input.split("[,\\s]+");
            List<Integer> list = new ArrayList<>();
            for (String token : tokens) {
                if (!token.isEmpty()) {
                    if (token.startsWith("0x") || token.startsWith("0X")) {
                        list.add(Integer.parseInt(token.substring(2), 16));
                    } else {
                        list.add(Integer.parseInt(token));
                    }
                }
            }
            program = list.stream().mapToInt(i -> i).toArray();

        } else {
            System.out.println("\nEnter Assembly Code line by line (type 'END' or press Enter on an empty line to finish):");
            StringBuilder asmBuilder = new StringBuilder();
            while (true) {
                String line = scanner.nextLine();
                if (line.trim().equalsIgnoreCase("END") || line.trim().isEmpty()) {
                    break;
                }
                asmBuilder.append(line).append("\n");
            }

            // compile Assembly into Bytecode
            program = SimpleAssembler.assemble(asmBuilder.toString());
        }

        System.out.println("\n=== EXECUTING PROGRAM ===");
        cpu.loadProgram(program, 0);

        // Run program
        while (cpu.isRunning()) {
            cpu.step();
        }

        // Print final states
        System.out.println("\n=== REGISTERS ===");
        for (int i = 0; i < 4; i++) {
            System.out.print("R" + i + ": ");
            System.out.println(cpu.getReg().getRegister(i));
        }

        System.out.println("===================");
        System.out.println("=== RAM DUMP ===");
        for (int i = 0; i < 128; i++) {
            System.out.print("Ram[" + i + "]: ");
            System.out.print(cpu.getMemory().read(i) + " | ");
            if (i % 10 == 9) {
                System.out.println();
            }
        }
        System.out.println();
    }
}