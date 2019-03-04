package oram.vm;

import oram.operand.Operand;
import oram.operand.Register;

public class EmptyVM implements VirtualMachine {

    @Override
    public String mem() { return null; }

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
    public long read(long address, DataType type) { return 0; }

    @Override
    public void push(long word, DataType type) { }

    @Override
    public void set(Operand o, long value, DataType type) { }

    @Override
    public long pop(DataType type) { return 0; }

    @Override
    public long arrayOffset(String array) { return 0; }
}
