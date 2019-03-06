package x86diagnostic;

import x86diagnostic.test.TestSuite;
import x86diagnostic.vm.Computer;
import x86diagnostic.vm.Configuration;
import x86diagnostic.vm.Instruction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static x86diagnostic.Main.Argument.HELP;
import static x86diagnostic.Main.Argument.TEST;
import static x86diagnostic.Main.Argument.VERBOSE;
import static x86diagnostic.Main.FileType.*;

/**
 * https://cs.brown.edu/courses/cs033/docs/guides/x64_cheatsheet.pdf
 */
public class Main {
    enum FileType {
        ASSEMBLER(f -> null), C_FILE(f -> null);

        public Function<String, Instruction[]> op;
        FileType(Function<String, Instruction[]> op){
            this.op = op;
        }
    }
    enum Argument {
        ALSO_REGISTERS('r', c -> c.setRegisters(true)),
        OUTPUT_PARSED('p',  c -> parse(c.getInstructions())),
        VERBOSE('v',        c -> c.setVerbose(true)),
        TEST('t',           c -> test(c)),
        DIAGNOSE('d',       c -> diag(c)),
        HELP('h',           c -> help());

        public char id;
        public Consumer<Configuration> f;
        Argument(char id, Consumer<Configuration> f){
            this.id = id;
            this.f = f;
        }
    }

    public static void main(String[] args) {
        String file = null;
        FileType type = null;

        List<Argument> arguments = new ArrayList<>();
        if(args.length==0)
            arguments.add(HELP);

        for(String arg : args){
            if(arg.endsWith(".c")) {
                file = arg;
                type = C_FILE;
            } else if(arg.endsWith(".s")){
                file = arg;
                type = ASSEMBLER;
            }
            if(arg.startsWith("-")){
                arg = arg.substring(1);
                for(char c : arg.toCharArray())
                for(Argument a : Argument.values())
                    if(c==a.id)arguments.add(a);
            }
        }
        System.out.println("--------------------------------");
        System.out.println("----   x86-64 DIAGNOSTICS   ----");
        System.out.println("--------------------------------");

        boolean test = arguments.contains(TEST);
        if(file==null && !test){
            help();
            return;
        } else if(file==null && test) {
            test(new Configuration(null, arguments.contains(VERBOSE), false));
            return;
        }
        if(arguments.isEmpty()){
            System.out.println("No arguments supplied.");
            return;
        }

        Configuration c = new Configuration(type.op.apply(file), false, false);

        Collections.sort(arguments);
        for(Argument a : arguments)
            a.f.accept(c);
    }

    private static void diag(Configuration c) {
        System.out.println("--------------------------");
        System.out.println("---     DIAGNOSING     ---");
        System.out.println("--------------------------");
        try {
            System.out.println(Computer.compute(c));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void parse(Instruction[] instrs) {
        System.out.println("--------------------------");
        System.out.println("---  PARSED ASSEMBLER  ---");
        System.out.println("--------------------------");
        Arrays.stream(instrs).forEach(System.out::println);
    }

    private static void test(Configuration c) {
        try {
            TestSuite.testAll(c.isVerbose());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void help() {
        System.out.println("By:  Nikolaj I. Schwartzbach");
        System.out.println("     Lasse Letager Hansen\n");
        System.out.println("This is an x86-64 virtual machine written in Java.\n");
        System.out.println("Its purpose is to compute memory access patterns");
        System.out.println("for compiled programs. To be precise, this program");
        System.out.println("computes the first-order markov transition matrix");
        System.out.println("for all instructions on the heap and the stack.\n");
        System.out.println("Example usage:\n");
        System.out.println("> java -jar x86diag.jar -pd tests/insertion_sort1.c\n");
        System.out.println("Arguments:");
        System.out.println(" - d: diagnose C file or assembler file.");
        System.out.println(" - p: output the parsed assembler file.");
        System.out.println(" - r: include registers in markov matrix.");
        System.out.println(" - t: run tests.");
        System.out.println(" - v: verbose mode; dump memory every instruction.");
    }
}
