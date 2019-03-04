package oram.vm;

import oram.operand.*;
import oram.parse.Parser;

import java.util.*;

/**
 * Very simple memory organization.
 *
 * | heap              stack | constants(arrays)
 * | grows ->       <- grows | grows ->
 * |-------------------------|-----...
 * 0                       OFFSET
 */
public class SimpleVM implements VirtualMachine {

    public final static long OFFSET = 100000;

    private long min_heap = 0, max_heap = 0, min_stack = OFFSET, constant_size;

    private EmptyVM mem;
    public Map<Long, Byte> heap;
    private Map<Register, Long> registers;
    private Map<Flag, Boolean> flags;
    private Map<String, Integer> instructionLabels, constantLabels;

    static class InitialMemory {

        final Map<String, Integer> consts = new HashMap<>();
        final List<Byte> bytes = new ArrayList<>();
        private int i=0;

        void push(byte b){
            bytes.add(b);
            i+=1;
        }
        void push(long word, DataType type){
            byte[] bs = type.arr(word);
            for(int i=0; i<bs.length; i++)
                push(bs[i]);
        }
        void label(String str){
            consts.put(str, i);
        }
        void install(SimpleVM vm){
            vm.constantLabels = consts;
            vm.constant_size = bytes.size();
            for(int j=0; j<bytes.size(); j++)
                vm.heap.put(OFFSET+j, bytes.get(j));
        }
    }

    public SimpleVM(Map<String, Integer> instructionLabels, InitialMemory initMem){
        mem         = new EmptyVM();
        heap        = new HashMap<>();
        registers   = new HashMap<>();
        flags       = new HashMap<>();
        this.instructionLabels = instructionLabels;
        initMem.install(this);

        set(Register.RSP, OFFSET);
        set(Register.RBP, OFFSET);
    }

    public String mem(){
        StringBuilder sb = new StringBuilder();
        sb.append("Heap:       |");
        for(long addr = min_heap; addr<max_heap; addr++)
            sb.append(String.format("%02x", (heap.getOrDefault(addr, (byte)0))) + "|");

        sb.append("\nStack:      "+((OFFSET==load(Register.RSP)?OFFSET==load(Register.RBP)?"§":"$":OFFSET==load(Register.RBP)?"#":"|")));
        for(long i = OFFSET-1; i>=min_stack; i--){
            sb.append(String.format("%02x", (heap.getOrDefault(i,(byte)0)))+(i==load(Register.RSP)+1?load(Register.RSP)==load(Register.RBP)?"§":"$":i==load(Register.RBP)+1?"#":i==99960?"+":"|"));}

        sb.append("\nRegisters:  |");
        for(Register r : Register.values())
            if(registers.containsKey(r))
                sb.append(r+" = "+registers.get(r)+"|");

        sb.append("\nConstants:  |");
        for(long i=0; i<constant_size; i++)
            sb.append(String.format("%02x", (heap.getOrDefault(OFFSET+i,(byte)0)))+"|");

        return sb.append("\n").toString();
    }

    @Override
    public void jump(String lbl) {
        if(!instructionLabels.containsKey(lbl))throw new IllegalStateException("no such label: "+lbl);
        jump(instructionLabels.get(lbl));
    }

    @Override
    public void jump(long loc) {
        set(Register.RIP, loc, DataType.LONG);
    }

    @Override
    public long load(Register r) {
        if(r!=Register.NONE)
            return registers.getOrDefault(r, mem.load(r));
        throw new IllegalStateException("trying to load from invalid register: none");
    }

    @Override
    public void condition(long value) {
        System.out.println("VALUE: "+value);
        flags.put(Flag.ZF, value==0);
    }

    @Override
    public boolean flag(Flag f) {
        return flags.getOrDefault(f, mem.flag(f));
    }

    @Override
    public long read(long address,DataType type) {
        long x = 0;
        for(int i=0; i<type.bytes(); i++) {
            x *= 256;
            long addr = address + 8 - type.bytes() + i;
            System.out.println(address+"\ty="+(heap.getOrDefault(addr,(byte)0) & 0xff));
            x += heap.getOrDefault(addr,(byte)0) & 0xff;
        }
        System.out.println("reading from "+address+": "+x);
        return x;
    }

    @Override
    public void push(long word, DataType type) {
        set(Register.RSP, load(Register.RSP)-type.bytes());
        set(QuadAddress.deref(Register.RSP), word, type);
    }

    private void put(long addr, byte... bytes){
        for(int i=0; i<bytes.length; i++) {
            long a = addr + i;
            System.out.println(i+"\tsetting "+(a));
            heap.put(a, bytes[i]);
        }

        if(addr < OFFSET /2)
            max_heap = Math.max(addr, max_heap);
        else
            min_stack = Math.min(addr-bytes.length, min_stack);
    }

    @Override
    public void set(Operand o, long value, DataType type) {
        if(o instanceof Addressable) {
            System.out.println(type);
            long addr = ((Addressable) o).address(this);
            put(addr, type.arr(value));
            min_heap = Math.min(addr, min_heap);
            if(addr < OFFSET /2)
                max_heap = Math.max(addr, max_heap);
            else
                min_stack = Math.min(min_stack, addr);
        }
        else if(o instanceof Register)
            registers.put((Register)o, value);
    }

    @Override
    public long pop(DataType type) {
        long address = load(Register.RSP);
        set(Register.RSP, address+type.bytes());
        return read(address, DataType.QUAD);
    }

    @Override
    public long arrayOffset(String array) {
        return constantLabels.get(array);
    }
}
