package oram.operand;

import oram.vm.DataType;
import oram.vm.VirtualMachine;

public class Immediate implements Operand {

    private long word;
    public Immediate(long word){
        this.word = word;
    }

    @Override
    public long get(VirtualMachine vm, DataType type) {
        return word;
    }

    public String toString(){
        return "$"+word;
    }
}
