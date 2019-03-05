package x86diagnostic.operand;

import x86diagnostic.vm.DataType;
import x86diagnostic.vm.SimpleVM;
import x86diagnostic.vm.VirtualMachine;

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
        try {
            return toString().hashCode();
        } catch (StackOverflowError e){
            System.out.println(this.getClass());
            return 0;
        }
    }
}
