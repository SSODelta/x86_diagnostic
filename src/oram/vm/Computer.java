package oram.vm;

import oram.operand.Register;
import oram.parse.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Computer {
    public static long compute(String cCode){
        return new Computer(SimpleVM::new).execute(Parser.parse(cCode));
    }
    public static long compute(Instruction[] instrs){
        return new Computer(SimpleVM::new).execute(instrs);
    }
    private BiFunction<Map<String, Integer>, SimpleVM.InitialMemory, VirtualMachine> vm_gen;
    private int depth;
    public Computer(BiFunction<Map<String, Integer>, SimpleVM.InitialMemory, VirtualMachine> vm_gen){
        this.vm_gen =  vm_gen;
        this.depth = 0;
    }
    public long execute(Instruction[] instructions){
        int i = 0;
        System.out.println(1);
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
        System.out.println(2);
        VirtualMachine vm = vm_gen.apply(instructionLabels, initialMemory);
        if(instructions.length == 0)
            throw new IllegalStateException("no instructions to perform");
        System.out.println("\n-----------------------"+
                           "\n-- EXECUTING PROGRAM --"+
                           "\n-----------------------\n");
        while(true) {
            Instruction next = instructions[(int)vm.load(Register.RIP)];
            Instruction.inc(Register.RIP).apply(vm);
            if (depth == 0 && next.equals(Instruction.ret)) {
                return vm.load(Register.RAX);
            }
            System.out.println("Executing: "+next);
            next.apply(vm);
            System.out.println(vm.mem());
        }
    }
}
