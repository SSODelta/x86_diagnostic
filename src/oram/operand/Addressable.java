package oram.operand;

import oram.vm.DataType;
import oram.vm.SimpleVM;
import oram.vm.VirtualMachine;

public abstract class Addressable implements Operand{

    public abstract long address(VirtualMachine vm);

    @Override
    public long get(VirtualMachine vm, DataType type) {
        try {
            return vm.read(address(vm), type);
        } catch(NullPointerException e){
            e.printStackTrace();
            System.out.println(((SimpleVM) vm).heap);
            throw new IllegalStateException("unable to read from address: "+address(vm));
        }
    }

    public int hashCode(){
        return toString().hashCode();
    }
}
