package com.cpu.hardware;

public class Memory {
    private int[] ram = new int[256];

    public void write(int address, int data) {
        if(address >= 0 && address < 256) {
            ram[address] = data;
        }
        else{
            System.out.println("Error: address out of range");
        }
    }

    public int read(int address) {
        if(address >= 0 && address < 256) {
            return ram[address];
        }
        else{
            System.out.println("Error: address out of range");
            return 0;
        }
    }

}
