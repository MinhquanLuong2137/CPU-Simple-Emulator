package com.cpu.app;

import com.cpu.hardware.CPU;

public class Main {
    public static void main(String[] args) {
        CPU cpu = new CPU();

        int[] program = {
                0x20, 0, 5,       // [0..2]   LOAD_CONST R0, 5
                0x20, 1, 5,       // [3..5]   LOAD_CONST R1, 5
                0x50, 0, 1, 13,   // [6..9]   BEQ R0, R1, 13 (Nhảy tới ô nhớ 13 nếu R0 == R1)
                0x20, 2, 99,      // [10..12] LOAD_CONST R2, 99 (Lệnh này sẽ bị bỏ qua)
                0x20, 2, 42,      // [13..15] LOAD_CONST R2, 42
                0xFF              // [16]     HALT
        };        cpu.loadProgram(program, 0);



        //Run program
        while(cpu.isRunning()){
            cpu.step();
        }

        for(int i = 0; i < 4; i++){
            System.out.println(cpu.getReg().getRegister(i));
        }
    }

}