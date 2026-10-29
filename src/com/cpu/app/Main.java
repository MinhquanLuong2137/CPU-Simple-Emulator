package com.cpu.app;

import com.cpu.hardware.CPU;

public class Main {
    public static void main(String[] args) {
        CPU cpu = new CPU();

        int[] program = {
                0x20, 0, 100,     // [0..2]  LOAD_CONST R0, 100 (địa chỉ cơ sở)
                0x20, 1, 42,      // [3..5]  LOAD_CONST R1, 42  (giá trị cần ghi)
                0x70, 1, 5, 0,    // [6..9]  SW R1, 5(R0)       (ghi 42 vào RAM[105])
                0x60, 2, 5, 0,    // [10..13] LW R2, 5(R0)      (đọc từ RAM[105] vào R2)
                0xFF              // [14]    HALT
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