package com.cpu.assembler;

import java.util.Locale;

public class OperandParser {

    //"R0" or "r0" => int 0
    public static int parseRegister(String regStr) {
        String cleaned = regStr.trim().toUpperCase().replace("R", "");
        return  Integer.parseInt(cleaned);
    }

    //compile immStr to imm
    public static int parseImmediate(String immStr) {
        String cleaned = immStr.trim();
        if(cleaned.startsWith("0x") || cleaned.startsWith("0X")) {
            return Integer.parseInt(cleaned.substring(2), 16);
        }
        return Integer.parseInt(cleaned);
    }


    // imm(rs) => int array {offset, rs}
    public static int[] parseOffsetRegister(String arg) {
        String cleaned = arg.trim();
        int openParen = cleaned.indexOf('(');
        int closeParen = cleaned.indexOf(')');

        int offset = Integer.parseInt(cleaned.substring(0, openParen).trim());
        String regStr = cleaned.substring(openParen + 1, closeParen).trim();
        int rs = parseRegister(regStr);

        return new int[] {offset, rs};
    }



}
