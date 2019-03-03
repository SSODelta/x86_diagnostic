package oram.parse;

import oram.vm.ConditionCode;
import oram.vm.Instruction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static Instruction[] compile(String cCode) throws IOException, InterruptedException {
        Files.write(Paths.get("code.c"), cCode.getBytes());
        Runtime.getRuntime().exec("g++ -S code.c -fno-asynchronous-unwind-tables -fno-stack-protector").waitFor();
        String asm = new String(Files.readAllBytes(Paths.get("code.s")));
        Files.delete(Paths.get("code.c"));
        Files.delete(Paths.get("code.s"));
        return parse(asm);
    }

    static Instruction[] parse(String data){
        System.out.println(data);
        String[] lines = data.replace("\r","")
                             .split("\n");
        List<Instruction> instrs = new ArrayList<>();
        for(String line : lines){
            if(line.startsWith("\t.section")) continue;
            if(line.startsWith("\t.macosx"))  continue;
            if(line.startsWith("\t.globl"))    continue;
            if(line.startsWith("\t.p2align")) continue;
            if(line.contains("##"))    continue;
            if(line.startsWith("."))    continue;
            if(line.isEmpty())    continue;
            instrs.add(parseLine(line));
        }
        return instrs.toArray(new Instruction[]{});
    }

    private static Instruction parseLine(String line) {
        if(!line.startsWith("\t")){
            return new Instruction.Label(line.substring(0,line.indexOf(":")));
        }
        LineParser lp = new LineParser(line.replace(" ",""));
        switch(lp.hd()){
            case "pushq":
                return Instruction.push(lp.op(1));
            case "popq":
                return Instruction.pop(lp.op(1));
            case "retq":
                return Instruction.ret;
            case "subq":
                return Instruction.sub(lp.op(1), lp.op(2));
            case "movq":
            case "movl":
                return Instruction.mov(lp.op(1), lp.op(2));
            case "cmpl":
                return Instruction.cmp(lp.op(1), lp.op(2));
            case "jne":
                return Instruction.jmp(lp.lbl(1), ConditionCode.not_equal);
            case "jmp":
                return Instruction.jmp(lp.lbl(1));
        }
        throw new IllegalStateException("invalid instruction: "+lp.hd());
    }
}
