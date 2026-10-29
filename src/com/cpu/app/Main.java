package com.cpu.app;

import com.cpu.hardware.CPU;

public class Main {
    public static void main(String[] args) {
        CPU cpu = new CPU();

        int[] program = {
// [0..8] Nạp dữ liệu ban đầu
                0x20, 0, 5,       // [0..2]   LOAD_CONST R0, 5
                0x20, 1, 10,      // [3..5]   LOAD_CONST R1, 10
                0x20, 2, 5,       // [6..8]   LOAD_CONST R2, 5

                // --- TEST BEQ ---
                // 1. BEQ (5 == 5 -> Đúng, Nhảy tới 16)
                0x50, 0, 2, 16,   // [9..12]  BEQ R0, R2, 16
                0x20, 3, 99,      // [13..15] LOAD_CONST R3, 99 (Bị nhảy qua)
                0x20, 3, 1,       // [16..18] LOAD_CONST R3, 1  (R3 = 1)

                // 2. BEQ (5 == 10 -> Sai, Chạy tiếp)
                0x50, 0, 1, 29,   // [19..22] BEQ R0, R1, 29
                0x20, 3, 2,       // [23..25] LOAD_CONST R3, 2  (R3 = 2)

                // --- TEST BLT ---
                // 3. BLT (5 < 10 -> Đúng, Nhảy tới 33)
                0x51, 0, 1, 33,   // [26..29] BLT R0, R1, 33
                0x20, 3, 99,      // [30..32] LOAD_CONST R3, 99 (Bị nhảy qua)
                0x20, 3, 3,       // [33..35] LOAD_CONST R3, 3  (R3 = 3)

                // 4. BLT (10 < 5 -> Sai, Chạy tiếp)
                0x51, 1, 0, 43,   // [36..39] BLT R1, R0, 43
                0x20, 3, 4,       // [40..42] LOAD_CONST R3, 4  (R3 = 4)

                // --- TEST BGE ---
                // 5. BGE (10 >= 5 -> Đúng, Nhảy tới 50)
                0x52, 1, 0, 50,   // [43..46] BGE R1, R0, 50
                0x20, 3, 99,      // [47..49] LOAD_CONST R3, 99 (Bị nhảy qua)
                0x20, 3, 5,       // [50..52] LOAD_CONST R3, 5  (R3 = 5)

                // 6. BGE (5 >= 5 -> Đúng, Nhảy tới 60)
                0x52, 0, 2, 60,   // [53..56] BGE R0, R2, 60
                0x20, 3, 99,      // [57..59] LOAD_CONST R3, 99 (Bị nhảy qua)
                0x20, 3, 6,       // [60..62] LOAD_CONST R3, 6  (R3 = 6)

                // 7. BGE (5 >= 10 -> Sai, Chạy tiếp)
                0x52, 0, 1, 70,   // [63..66] BGE R0, R1, 70
                0x20, 3, 7,       // [67..69] LOAD_CONST R3, 7  (R3 = 7)

                0xFF              // [70]     HALT
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