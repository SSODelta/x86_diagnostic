package x86diagnostic.operand;

import x86diagnostic.vm.DataType;
import x86diagnostic.vm.VirtualMachine;

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
