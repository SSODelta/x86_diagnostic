package oram.operand;

import oram.vm.VirtualMachine;

public class Immediate implements Operand {

    private long word;
    public Immediate(long word){
        this.word = word;
    }

    @Override
    public long get(VirtualMachine vm) {
        return word;
    }
}
