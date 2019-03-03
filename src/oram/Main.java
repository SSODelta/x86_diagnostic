package oram;

import oram.parse.Parser;
import oram.vm.Instruction;
import oram.vm.SimpleVM;
import oram.vm.VirtualMachine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * https://cs.brown.edu/courses/cs033/docs/guides/x64_cheatsheet.pdf
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Instruction[] instrs =
                Parser.compile(new String(Files.readAllBytes(Paths.get(args[0]))));

        VirtualMachine vm = new SimpleVM(instrs);
        System.out.println("Result: "+vm.compute());
    }
}
