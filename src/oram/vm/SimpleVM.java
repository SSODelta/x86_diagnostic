package oram.vm;

import oram.operand.Addressable;
import oram.operand.Operand;
import oram.operand.Register;
import oram.parse.LineParser;

import java.util.*;

public class SimpleVM implements VirtualMachine {

    private Instruction[] instructions;

    private boolean verbose = false;

    private EmptyVM mem;
    private Stack<Long> stack;
    private Map<Long, Long> heap;
    private Map<Register, Long> registers;
    private Map<Flag, Boolean> flags;
    private Map<String, Integer> labels;
    private Map<String, List<Long>> arrays;
    private int depth = 0;

    public SimpleVM(Instruction... instructions){
        mem = new EmptyVM();
        stack = new Stack<>();
        heap = new HashMap<>();
        registers = new HashMap<>();
        flags = new HashMap<>();
        labels = new HashMap<>();
        int i = 0;
        String arr = null;
        for(Instruction inst : instructions){
            if(inst instanceof Instruction.Label) {
                String lbl = ((Instruction.Label) inst).label();
                labels.put(lbl, i);
                arr = lbl.endsWith("arr")?lbl:null;
                if(lbl.endsWith("arr"))
                    arrays.put(lbl, new ArrayList<>());
            } else if(arr!=null){
                arrays.get(arr).add(new LineParser());
            }
            i+=1;
        }
        this.instructions = instructions;
    }

    @Override
    public long compute() {
        while(true) {
            Instruction next = instructions[(int)load(Register.RIP)];
            Instruction.inc(Register.RIP).apply(this);
            if (depth == 0 && next.equals(Instruction.ret)) {
                return load(Register.RAX);
            }
            next.apply(this);
        }
    }

    @Override
    public void jump(String lbl) {
        if(!labels.containsKey(lbl))throw new IllegalStateException("no such label: "+lbl);
        jump(labels.get(lbl));
    }

    @Override
    public void jump(long loc) {
        System.out.println("jumping to "+loc);
        set(Register.RIP, loc);
    }

    @Override
    public long load(Register r) {
        if(r!=Register.NONE)
            System.out.println("loading register "+r);
        return registers.getOrDefault(r, mem.load(r));
    }

    @Override
    public void condition(long value) {
        flags.put(Flag.ZF, value==0);
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
