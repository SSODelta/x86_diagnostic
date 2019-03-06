package x86diagnostic.parse;

import x86diagnostic.vm.Instruction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

    public static List<String> preprocess(String x86){
        List<String> lines = new ArrayList<>();
        for(String line : x86.replace("\r","").split("\n")){
            if(line.startsWith("\t.section")) continue;
            if(line.startsWith("\t.macosx"))  continue;
            if(line.startsWith("\t.globl"))    continue;
            if(line.startsWith("\t.p2align")) continue;
            if(line.startsWith("."))    continue;
            if(line.contains("##"))
                line = line.substring(0, line.indexOf("##"));
            if(line.isEmpty())    continue;

            line = line.replace("movs","mov");
            while(line.contains("  "))
                line=line.replace("  "," ");
            if(line.equals(" "))continue;

            lines.add(line);
        }
        return lines;
    }

    public static Instruction[] compile(String cCode) throws IOException, InterruptedException {
        Files.write(Paths.get("code.c"), cCode.getBytes());
        Process p = Runtime.getRuntime().exec("g++ -S code.c -fno-asynchronous-unwind-tables -fno-stack-protector -Wdeprecated");
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        p.waitFor();

        try {
            String asm = new String(Files.readAllBytes(Paths.get("code.s")));
            Files.delete(Paths.get("code.c"));
            Files.delete(Paths.get("code.s"));
            return parse(asm);
        } catch(NoSuchFileException e){
            // read any errors from the attempted command
            String s;
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        }
        throw new IllegalStateException("compiler error");
    }

    public static Instruction[] parse(String data){
        return preprocess(data).stream()
                               .map(l -> Instruction.parse(l))
                               .collect(Collectors.toList())
                               .toArray(new Instruction[]{});
    }
}
