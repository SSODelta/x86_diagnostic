package x86diagnostic.vm;

import x86diagnostic.operand.Register;
import x86diagnostic.parse.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Computer {
    public static long computeTest(String file, boolean verbose, long limit) throws IOException, InterruptedException, DivergeException {
        Computer c = new Computer((f, m) -> new MarkovComputingVM(new SimpleVM(f, m)), verbose);
        return c.execute(Parser.compile(new String(Files.readAllBytes(Paths.get("tests/" + file)))), file.replace(".c", ""), limit);
    }
    public static long compute(Configuration conf) throws IOException {
        Computer c = new Computer((f,m) -> new MarkovComputingVM(new SimpleVM(f,m)),conf.isVerbose());
         return c.execute(conf.getInstructions(), "markov");

    }


    private BiFunction<Map<String, Integer>, SimpleVM.InitialMemory, MarkovComputingVM> vm_gen;
    private int depth;
    private boolean verbose;
    public Computer(BiFunction<Map<String, Integer>, SimpleVM.InitialMemory, MarkovComputingVM> vm_gen, boolean verbose){
        this.vm_gen =  vm_gen;
        this.depth = 0;
        this.verbose = verbose;
    }
    public long execute(Instruction[] instructions, String markov_out) throws IOException {
        try {
            return execute(instructions, markov_out, Long.MAX_VALUE);
        } catch (DivergeException e) {
            return 0;
        }
    }
    public long execute(Instruction[] instructions, String markov_out, long limit) throws IOException, DivergeException {
        int i = 0;
        Map<String, Integer> instructionLabels = new HashMap<>();
        SimpleVM.InitialMemory initialMemory = new SimpleVM.InitialMemory();
        for(Instruction inst : instructions){
            if(inst instanceof Instruction.Label) {
                String lbl = ((Instruction.Label) inst).label();
                if(lbl.endsWith("arr")){
                    initialMemory.label(lbl);
                } else {
                    instructionLabels.put(lbl, i);
                }
            }
            if(inst instanceof Instruction.Constant) {
                Instruction.Constant c = ((Instruction.Constant) inst);
                initialMemory.push(c.value(), c.type());
            }
            i+=1;
        }
        MarkovComputingVM vm = vm_gen.apply(instructionLabels, initialMemory);
        if(instructions.length == 0)
            throw new IllegalStateException("no instructions to perform");
        if(verbose)
            System.out.println("\n-----------------------"+
                               "\n-- EXECUTING PROGRAM --"+
                               "\n-----------------------\n");
        int k=0;
        while(true) {
            if(k++>limit)
                throw new DivergeException(limit);
            Instruction next = instructions[(int)vm.load(Register.Type.RIP)];
            Instruction.inc(Register.Type.RIP).apply(vm);
            if (depth == 0 && next.equals(Instruction.ret)) {
                vm.printMarkov(markov_out);
                return vm.load(Register.Type.RAX);
            }
            if(verbose) {
                System.out.println(
                        "\n------------------------" +
                        "\n--   INSTRUCTION #" + (k) + "  --" +
                        "\n------------------------");
                System.out.println("Executing: " + next);
            }
            next.apply(vm);
            if(verbose)
                System.out.println(vm.mem());


        }
    }
}
