package oram.operand;

import oram.vm.VirtualMachine;

public class QuadAddress extends Addressable {

    private long imm, s;
    private Register r1, r2;
    public QuadAddress(long imm, Register r1, Register r2, long s){
        this.imm = imm;
        this.s = s;
        this.r1 = r1;
        this.r2 = r2;
    }

    @Override
    public long address(VirtualMachine vm) {
        return imm+vm.load(r1)+vm.load(r2)*s;
    }

    public String toString(){
        if(r2 == Register.NONE){
            return (imm!=0?imm:"")+"("+r1+")";
        }
        return super.toString();
    }
}
