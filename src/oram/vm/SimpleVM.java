package oram.vm;

import oram.operand.Addressable;
import oram.operand.Operand;
import oram.operand.Register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SimpleVM implements VirtualMachine {

    private long next_instruction = 0;
    private Instruction[] instructions;

    private boolean verbose = false;

    private EmptyVM mem;
    private Stack<Long> stack;
    private Map<Long, Long> heap;
    private Map<Register, Long> registers;
    private Map<Flag, Boolean> flags;
    private Map<String, Integer> labels;

    public SimpleVM(Instruction... instructions){
        mem = new EmptyVM();
        stack = new Stack<>();
        heap = new HashMap<>();
        registers = new HashMap<>();
        flags = new HashMap<>();
        labels = new HashMap<>();
        int i = 0;
        for(Instruction inst : instructions){
            if(inst instanceof Instruction.Label)
                labels.put(((Instruction.Label) inst).label(), i);
            i+=1;
        }
        this.instructions = instructions;
    }

    @Override
    public long compute() {
        Arrays.stream(instructions).forEach(i -> i.apply(this));
        return load(Register.RAX);
    }

    @Override
    public void jump(String lbl) {
        if(!labels.containsKey(lbl))throw new IllegalStateException("no such label: "+lbl);
        jump(labels.get(lbl));
    }

    @Override
    public void jump(long loc) {
        System.out.println("jumping to "+loc);
        next_instruction = loc/64;
    }

    @Override
    public long load(Register r) {
        System.out.println("loading register "+r);
        return registers.getOrDefault(r, mem.load(r));
    }

    @Override
    public void condition(long value) {

    }

    @Override
    public boolean flag(Flag f) {
        return flags.getOrDefault(f, mem.flag(f));
    }

    @Override
    public long read(long address) {
        System.out.println("reading from "+address);
        return heap.getOrDefault(address, mem.read(address));
    }

    @Override
    public void push(long word) {
        System.out.println("pushing "+word+" to stack");
        stack.push(word);
    }

    @Override
    public void set(Operand o, long value) {
        System.out.println("setting "+o+" to "+value);
        if(o instanceof Addressable)
            heap.put(((Addressable) o).address(this), value);
        else if(o instanceof Register)
            registers.put((Register)o, value);
    }

    @Override
    public long pop() {
        System.out.println("popping from stack");
        return stack.pop();
    }
}
