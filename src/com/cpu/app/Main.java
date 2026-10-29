package com.cpu.app;

import com.cpu.hardware.CPU;

public class Main {
    public static void main(String[] args) {
        CPU cpu = new CPU();

        int[] program = {
                0x20, 0, 5,       // [0..2]   LOAD_CONST R0, 5
                0x20, 1, 10,      // [3..5]   LOAD_CONST R1, 10

                // Test BLT (5 < 10 -> Nhảy tới 13)
                0x51, 0, 1, 13,   // [6..9]   BLT R0, R1, 13
                0x20, 2, 99,      // [10..12] LOAD_CONST R2, 99 (Bị bỏ qua nếu BLT đúng)
                0x20, 2, 1,       // [13..15] LOAD_CONST R2, 1

                // Test BGE (5 >= 10 -> Không nhảy)
                0x52, 0, 1, 26,   // [16..19] BGE R0, R1, 26 (Không nhảy)
                0x20, 3, 2,       // [20..22] LOAD_CONST R3, 2  (Được thực thi)
                0x10, 27,         // [23..24] JUMP 27
                0x20, 3, 99,      // [25..26] LOAD_CONST R3, 99 (Bị nhảy qua)
                0xFF              // [27]     HALT
        };        cpu.loadProgram(program, 0);



        //Run program
        while(cpu.isRunning()){
            cpu.step();
        }

        for(int i = 0; i < 4; i++){
            System.out.print("R" + i + ": ");
            System.out.println(cpu.getReg().getRegister(i));
        }
    }

}