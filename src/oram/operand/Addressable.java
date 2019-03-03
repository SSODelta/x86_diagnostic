package oram.operand;

import oram.vm.DataType;
import oram.vm.VirtualMachine;

public abstract class Addressable implements Operand {

    public abstract long address(VirtualMachine vm);

    @Override
    public long get(VirtualMachine vm, DataType type) {
        return vm.read(address(vm),type);
    }
}
