package oram.operand;

import oram.vm.VirtualMachine;

public abstract class Addressable implements Operand {

    public abstract long address(VirtualMachine vm);

    @Override
    public long get(VirtualMachine vm) {
        return vm.read(address(vm));
    }
}
