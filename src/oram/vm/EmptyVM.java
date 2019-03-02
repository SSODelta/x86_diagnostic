package oram.vm;

import oram.operand.Operand;
import oram.operand.Register;

public class EmptyVM implements VirtualMachine {
    @Override
    public long compute() { return 0; }

    @Override
    public void jump(String lbl) { }

    @Override
    public void jump(long loc) { }

    @Override
    public long load(Register r) { return 0; }

    @Override
    public void condition(long value) { }

    @Override
    public boolean flag(Flag f) { return false; }

    @Override
    public long read(long address) { return 0; }

    @Override
    public void push(long word) { }

    @Override
    public void set(Operand o, long value) { }

    @Override
    public long pop() { return 0; }
}
