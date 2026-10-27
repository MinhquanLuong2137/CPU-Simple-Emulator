package com.cpu.hardware;

public class ALU {
    public int add(int a, int b) {
        return (a + b) & 0xFF;
    }
    public int sub(int a, int b) {
        return (a - b) & 0xFF;
    }
    public int and(int a, int b) {
        return (a & b) & 0xFF;
    }
    public int or(int a, int b) {
        return (a | b) & 0xFF;
    }
    public int xor(int a, int b) {
        return (a ^ b) & 0xFF;
    }


}
