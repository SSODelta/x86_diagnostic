package x86diagnostic.operand;

import x86diagnostic.vm.SimpleVM;
import x86diagnostic.vm.VirtualMachine;

public class ArrayRipOffset extends Addressable {

    private String array;
    private int offset;
    public ArrayRipOffset(String array, int offset){
        this.array  = array;
        this.offset = offset;
    }

    @Override
    public String toString(){
        return array + ((offset > 0)?"+"+offset:"") + "(%rip)";
    }

    @Override
    public long address(VirtualMachine vm) {
        return SimpleVM.OFFSET + offset + vm.arrayOffset(array);
    }

}