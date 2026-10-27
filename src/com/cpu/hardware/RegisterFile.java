package com.cpu.hardware;

public class RegisterFile {
    private int[] registers = new int[4];
    private int programCounter;
    private boolean zeroFlag;

    public int getRegister(int index) {
        if(index >= 0 && index < registers.length) {
            return registers[index];
        }
        else {
            System.out.println("Register index out of range");
            return 0;
        }
    }
    public void setRegister(int index, int value) {
        if(index >= 0 && index < registers.length) {
            registers[index] = value & 0xFF;
        }
    }

    public int getProgramCounter() {
        return programCounter;
    }
    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public boolean isZeroFlag() {
        return zeroFlag;
    }
    public void setZeroFlag(boolean zeroFlag) {
        this.zeroFlag = zeroFlag;
    }

    public void incrementPC() {
        programCounter++;
    }

}
