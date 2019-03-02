package oram.operand;

import oram.vm.VirtualMachine;

public class AbsoluteReg extends Addressable {

    private Register reg;
    public AbsoluteReg(Register reg){
        this.reg = reg;
    }

    @Override
    public long address(VirtualMachine vm) {
        return vm.load(reg);
    }

}
