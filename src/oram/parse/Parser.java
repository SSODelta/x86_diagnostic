package oram.parse;

import oram.vm.Instruction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static Instruction[] compile(String cCode) throws IOException, InterruptedException {
        Files.write(Paths.get("code.c"), cCode.getBytes());
        Runtime.getRuntime().exec("g++ -S code.c -fno-asynchronous-unwind-tables").waitFor();
        String asm = new String(Files.readAllBytes(Paths.get("code.s")));
        Files.delete(Paths.get("code.c"));
        Files.delete(Paths.get("code.s"));
        return parse(asm);
    }

    static Instruction[] parse(String data){
        String[] lines = data.replace("\r","")
                             .split("\n");
        List<Instruction> instrs = new ArrayList<>();
        for(String line : lines){
            if(line.startsWith("\t.section")) continue;
            if(line.startsWith("\t.macosx"))  continue;
            if(line.startsWith("\t.globl"))    continue;
            if(line.startsWith("\t.p2align")) continue;
            if(line.startsWith("##"))    continue;
            if(line.startsWith("."))    continue;
            if(line.isEmpty())    continue;
            instrs.add(parseLine(line));
        }
        return instrs.toArray(new Instruction[]{});
    }

    private static Instruction parseLine(String line) {
        if(!line.startsWith("\t")){
            return new Instruction.Label(line.substring(0,line.length()-1));
        }
        LineParser lp = new LineParser(line.replace(" ",""));
        switch(lp.hd()){
            case "pushq":
                return Instruction.push(lp.op(1));
            case "popq":
                return Instruction.pop(lp.op(1));
            case "retq":
                return Instruction.ret();
            case "movq":
            case "movl":
                return Instruction.mov(lp.op(1), lp.op(2));
        }
        throw new IllegalStateException("invalid instruction: "+lp.hd());
    }
}
