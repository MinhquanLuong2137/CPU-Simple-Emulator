package com.cpu.hardware;

public class CPU {
    CPU() {
        reg = new RegisterFile();
        memory = new Memory();
        alu = new ALU();
    }

    private RegisterFile reg;
    private Memory memory;
    private ALU alu;

    public RegisterFile getReg() {
        return reg;
    }
    public Memory getMemory() {
        return memory;
    }
    public ALU getALU() {
        return alu;
    }
    public void stop() {}
}
