package x86diagnostic.operand;

import x86diagnostic.vm.VirtualMachine;

public class AbsoluteImm extends Addressable {

    private long imm;
    public AbsoluteImm(long imm){
        this.imm = imm;
    }

    @Override
    public long address(VirtualMachine vm) {
        return imm;
    }

    public String toString(){
        return "($"+imm+")";
    }
}
