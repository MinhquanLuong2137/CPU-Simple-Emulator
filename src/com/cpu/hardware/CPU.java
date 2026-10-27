package com.cpu.hardware;

import com.cpu.instruction.*;

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

    private Instruction decode(int rawInstruction){
        switch (rawInstruction){
            case 0xFF: {
                return new HaltInstruction();
            }
            case 0x10:{
                int destReg = memory.read(reg.getProgramCounter());
                reg.incrementPC();
                return new JumpInstruction(destReg);
            }
            case 0x20:{
                int destReg = memory.read(reg.getProgramCounter());
                reg.incrementPC();
                int val = memory.read(reg.getProgramCounter());
                reg.incrementPC();
                return new LoadConstInstruction(destReg, val);
            }
            case 0x30:{
                int rd = memory.read(reg.getProgramCounter());
                reg.incrementPC();
                int rs1 = memory.read(reg.getProgramCounter());
                reg.incrementPC();
                int rs2 = memory.read(reg.getProgramCounter());
                reg.incrementPC();
                return new AddInstruction(rd, rs1, rs2);
            }
            case 0x40:{
                int rd = memory.read(reg.getProgramCounter());
                reg.incrementPC();
                int rs1 = memory.read(reg.getProgramCounter());
                reg.incrementPC();
                int rs2 = memory.read(reg.getProgramCounter());
                reg.incrementPC();
                return new SubInstruction(rd, rs1, rs2);
            }
            default:{
                System.out.println("Unknown Instruction");
                return null;
            }
        }
    }

    public void step(){
        if(!running){
            return;
        }

        int pc = reg.getProgramCounter();
        int rawInstruction = memory.read(pc);
        reg.incrementPC();

        Instruction instr = decode(rawInstruction);
        if(instr != null){
            instr.execute(this);
        }
    }


}
