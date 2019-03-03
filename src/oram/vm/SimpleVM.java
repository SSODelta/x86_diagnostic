package oram.vm;

import oram.operand.*;
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

    private final static long OFFSET = 100000;

    private long min_heap = 0, max_heap = 0, min_stack = OFFSET;

    private EmptyVM mem;
    private Map<Long, Byte> heap;
    private Map<Register, Long> registers;
    private Map<Flag, Boolean> flags;
    private Map<String, Integer> instructionLabels, constantLabels;

    public SimpleVM(Map<String, Integer> instructionLabels, List<Byte> constants, Map<String, Integer> constantLabels){
        mem         = new EmptyVM();
        heap        = new HashMap<>();
        registers   = new HashMap<>();
        flags       = new HashMap<>();
        this.instructionLabels = instructionLabels;
        this.constantLabels = constantLabels;
        for(int i=0; i<constants.size(); i++)
            heap.put(OFFSET+1 + i, constants.get(i));
        set(Register.RSP, OFFSET);
        set(Register.RBP, OFFSET);
    }

    public String mem(){
        StringBuilder sb = new StringBuilder();
        sb.append("Heap:       |");
        for(long addr = min_heap; addr<max_heap; addr++)
            sb.append(String.format("%02x", (heap.getOrDefault(addr, (byte)0))) + "|");
        sb.append("\nStack:      |");
            for(long i = OFFSET; i>min_stack; i--){
                sb.append(String.format("%02x", (heap.get(i)))+"|");}
        sb.append("\nRegisters:  |");
            for(Register r : Register.values()){
                if(registers.containsKey(r))
                    sb.append(r+" = "+registers.get(r)+"|");
            }
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
            System.out.println("reading "+(address-i));
            x *= 256;
            x += heap.get(address-i);
        }
        return x;
    }

    @Override
    public void push(long word, DataType type) {
        set(QuadAddress.deref(Register.RSP), word, type);
        set(Register.RSP, load(Register.RSP)-type.bytes());
    }

    private void put(long addr, byte... bytes){
        for(int i=0; i<bytes.length; i++)
            heap.put(addr-i, bytes[i]);

        if(addr < OFFSET /2)
            max_heap = Math.max(addr, max_heap);
        else
            min_stack = Math.min(addr-bytes.length, min_stack);
    }

    @Override
    public void set(Operand o, long value, DataType type) {
        if(o instanceof Addressable) {
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
}
