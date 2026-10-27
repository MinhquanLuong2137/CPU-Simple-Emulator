package com.cpu.app;

import com.cpu.hardware.CPU;

public class Main {
    public static void main(String[] args) {
        CPU cpu = new CPU();
        int[] program = {0x20,0,10,0x20,1,20,0x30,2,0,1,0xFF};
        cpu.loadProgram(program, 0);
        while(cpu.isRunning()){
            cpu.step();
        }

        for(int i = 0; i < 4; i++){
            System.out.println(cpu.getReg().getRegister(i));
        }
    }
}