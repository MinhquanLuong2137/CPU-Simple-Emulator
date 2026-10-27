package com.cpu.hardware;

public class CPU {
    public CPU() {
        reg = new RegisterFile();
        memory = new Memory();
        alu = new ALU();
    }

    private RegisterFile reg;
    private Memory memory;
    private ALU alu;
    private boolean running = true;

    public RegisterFile getReg() {
        return reg;
    }
    public Memory getMemory() {
        return memory;
    }
    public ALU getALU() {
        return alu;
    }

    public void stop() {
        running = false;
    }
    public boolean isRunning() {
        return running;
    }

    public void loadProgram(int[] program, int startAddress){
        for (int i = 0; i < program.length; i++) {
            memory.write(startAddress + i,  program[i]);
        }
        getReg().setProgramCounter(startAddress);
    }

}
