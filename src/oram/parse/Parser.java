package oram.parse;

import oram.vm.ConditionCode;
import oram.vm.Instruction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Parser {

    private class Preprocessed {
        public List<String> lines;
        public Map<String, Integer> constantOffsets;

        public Preprocessed(List<String> lines, Map<String, Integer> constantOffsets){

        }
    }

    public static List<String> preprocess(String x86){
        System.out.println(x86);
        List<String> lines = new ArrayList<>();
        Map<String, List<Long>> arrs = new HashMap<>();
        String arr = null;
        for(String line : x86.replace("\r","").split("\n")){
            if(line.startsWith("\t.section")) continue;
            if(line.startsWith("\t.macosx"))  continue;
            if(line.startsWith("\t.globl"))    continue;
            if(line.startsWith("\t.p2align")) continue;
            if(line.startsWith("."))    continue;
            if(line.contains("##"))
                line = line.substring(0, line.indexOf("##"));
            if(line.isEmpty())    continue;
            if(line.startsWith("\t")){
                if(arr != null){
                    arrs.get(arr).add(new LineParser(line).word(1));
                } else lines.add(line);
            } else {
                if(line.endsWith("arr:")){
                    String lbl = line.substring(0,line.indexOf(":"));
                    arr = lbl;
                    arrs.put(lbl, new ArrayList<>());
                } else {
                    arr = null;
                    lines.add(line);
                }
            }
        }
        List<String> lines2 = new ArrayList<>();
        for(String line : lines){
            for(String arrid : arrs.keySet()){
                if(line.contains(arrid)){
                    if(line.contains("+")){
                        int offset = Integer.parseInt(line.substring(0, line.indexOf("(")).substring(line.indexOf("+")+1));
                        line = line.replace(arrid+"+"+offset, ""+arrs.get(arrid).get(offset/8));
                    } else {
                        line = line.replace(arrid, ""+arrs.get(arrid).get(0));
                    }
                }
                if(!line.startsWith("\t") && !line.contains(":"))
                    continue;
                lines2.add(line.replace("movs","mov"));
            }
        }
        return lines2;
    }

    public static Instruction[] compile(String cCode) throws IOException, InterruptedException {
        Files.write(Paths.get("code.c"), cCode.getBytes());
        Runtime.getRuntime().exec("g++ -S code.c -fno-asynchronous-unwind-tables -fno-stack-protector").waitFor();
        String asm = new String(Files.readAllBytes(Paths.get("code.s")));
        Files.delete(Paths.get("code.c"));
        Files.delete(Paths.get("code.s"));
        return parse(asm);
    }

    public static Instruction[] parse(String data){
        return preprocess(data).stream()
                               .map(l -> Instruction.parse(l))
                               .collect(Collectors.toList())
                               .toArray(new Instruction[]{});
    }
}
