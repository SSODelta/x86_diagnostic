package oram.operand;

import oram.vm.DataType;
import oram.vm.VirtualMachine;

public enum Register implements Operand {
    RAX, RCX, RDX, RBX,
    RSI, RDI, RSP, RBP,
    R8,  R9,  R10, R11,
    R12, R13, R14, R15,
    RIP, NONE;

    public String toString(){
        return "%"+super.toString().toLowerCase();
    }

    static Register get(String register){
        register = register.replace("(","").replace(")","");
        if(!register.startsWith("%"))
            throw new IllegalArgumentException("not a register: "+register);
        switch(register.substring(1).toLowerCase()){
            case "rip":
                return RIP;
            case "al":
            case "rax":
            case "eax":
                return RAX;
            case "cl":
            case "ecx":
            case "rcx":
                return RCX;
            case "dl":
            case "edx":
            case "rdx":
                return RDX;
            case "rbx":
                return RBX;
            case "rsi":
                return RSI;
            case "rdi":
                return RDI;
            case "rsp":
                return RSP;
            case "rbp":
                return RBP;
            case "r8":
                return R8;
            case "r9":
                return R9;
            case "r10":
                return R10;
            case "r11":
                return R11;
            case "r12":
                return R12;
            case "r13":
                return R13;
            case "r14":
                return R14;
            case "r15":
                return R15;

        }
        throw new IllegalStateException("no such register: "+register);
    }

    @Override
    public long get(VirtualMachine vm, DataType type) {
//        if(type != DataType.QUAD)
  //          throw new IllegalStateException("trying to read register "+this+" as "+type+"; expected quad.");
        return vm.load(this);
    }
}
