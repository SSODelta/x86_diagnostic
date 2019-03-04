package oram.operand;

import oram.vm.DataType;
import oram.vm.SimpleVM;
import oram.vm.VirtualMachine;

public class ArrayRipOffset implements Operand {

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
    public long get(VirtualMachine vm, DataType type) {
        long addr = SimpleVM.OFFSET + offset + vm.arrayOffset(array);
        return vm.read(addr,type);
    }
}