package com.cpu.app;

import com.cpu.hardware.CPU;

public class Main {
    public static void main(String[] args) {
        CPU cpu = new CPU();

        int[] program = {
// 1. Nạp dữ liệu ban đầu
                0x20, 0, 0b11000011, // [0..2]   LOAD_CONST R0, 195 (0xC3)
                0x20, 1, 0b00001111, // [3..5]   LOAD_CONST R1, 15  (0x0F)
                0x20, 2, 2,          // [6..8]   LOAD_CONST R2, 2

                // 2. Test AND: R3 = R0 & R1
                0x31, 3, 0, 1,       // [9..12]  AND R3, R0, R1

                // 3. Test OR:  R3 = R0 | R1
                0x32, 3, 0, 1,       // [13..16] OR  R3, R0, R1

// Nạp dữ liệu
                0x20, 0, 100,      // [0..2]   LOAD_CONST R0, 100
                0x20, 1, 20,       // [3..5]   LOAD_CONST R1, 20
                0x20, 2, 2,        // [6..8]   LOAD_CONST R2, 2

                // 1. ADD:  R3 = R0 + R1  (100 + 20 = 120)
                0x30, 3, 0, 1,     // [9..12]  ADD R3, R0, R1

                // 2. SUB:  R3 = R0 - R1  (100 - 20 = 80)
                0x40, 3, 0, 1,     // [13..16] SUB R3, R0, R1

                // 3. SUB (Zero Flag Test): R3 = R0 - R0 (100 - 100 = 0 -> ZeroFlag = true)
                0x40, 3, 0, 0,     // [17..20] SUB R3, R0, R0

                // 4. AND:  R3 = R0 & R1  (100 & 20 = 4)
                0x31, 3, 0, 1,     // [21..24] AND R3, R0, R1

                // 5. OR:   R3 = R0 | R1  (100 | 20 = 116)
                0x32, 3, 0, 1,     // [25..28] OR  R3, R0, R1

                // 6. XOR:  R3 = R0 ^ R1  (100 ^ 20 = 112)
                0x33, 3, 0, 1,     // [29..32] XOR R3, R0, R1

                // 7. SLL:  R3 = R1 << R2 (20 << 2 = 80)
                0x34, 3, 1, 2,     // [33..36] SLL R3, R1, R2

                // 8. SRL:  R3 = R1 >> R2 (20 >> 2 = 5)
                0x35, 3, 1, 2,     // [37..40] SRL R3, R1, R2

                0xFF               // [41]     HALT
        };        cpu.loadProgram(program, 0);



        //Run program
        while(cpu.isRunning()){
            cpu.step();
        }

        for(int i = 0; i < 4; i++){
            System.out.print("R" + i + ": ");
            System.out.println(cpu.getReg().getRegister(i));
        }
        System.out.println("===================");
        for(int i = 0; i < 128; i++){
            System.out.print("Ram[" + i + "]: ");
            System.out.print(cpu.getMemory().read(i) + " | ");
            if(i % 10 == 9){
                System.out.println();
            }
        }
    }

}