package x86diagnostic.operand;

import x86diagnostic.vm.VirtualMachine;

import static x86diagnostic.vm.DataType.QUAD;

public class QuadAddress extends Addressable {

    private long imm, s;
    private Register r1, r2;
    public QuadAddress(long imm, Register r1, Register r2, long s){
        this.imm = imm;
        this.s = s;
        this.r1 = r1;
        this.r2 = r2;
    }

    public static QuadAddress deref(Register r){
        return new QuadAddress(0,r,Register.NONE,0);
    }
    public static QuadAddress deref(Register.Type type){
        return deref(new Register(type, QUAD));
    }

    public QuadAddress add(long w){
        return new QuadAddress(imm+w,r1,r2,s);
    }

    @Override
    public long address(VirtualMachine vm) {
        if(r2 == Register.NONE)
            return imm+vm.load(r1);
        return imm+vm.load(r1)+vm.load(r2)*s;
    }

    public String toString(){
        if(r2 == Register.NONE){
            return (imm!=0?imm:"")+"("+r1+")";
        } else if(r1 != Register.NONE){
            return (imm!=0?imm:"")+"("+r1+","+r2+")";
        } else {
            return (imm != 0 ? imm : "") + "(," + r2 + ")";
        }
    }
}
