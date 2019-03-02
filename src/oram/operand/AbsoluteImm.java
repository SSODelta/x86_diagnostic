package oram.operand;

import oram.vm.VirtualMachine;

public class AbsoluteImm extends Addressable {

    private long imm;
    public AbsoluteImm(long imm){
        this.imm = imm;
    }

    @Override
    public long address(VirtualMachine vm) {
        return imm;
    }
}
