package oram;

import oram.parse.Parser;
import oram.vm.Instruction;
import oram.vm.SimpleVM;
import oram.vm.VirtualMachine;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Instruction[] instrs =
                Parser.compile("#include <stdio.h>\n" +
                "int main()\n" +
                "{\n" +
                "   int x = (7+8)*13;\n" +
                "   return x;\n" +
                "}");

        VirtualMachine vm = new SimpleVM(instrs);
        System.out.println("Result: "+vm.compute());
    }
}
