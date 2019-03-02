package oram.operand;

import oram.vm.VirtualMachine;

public enum Register implements Operand {
    RAX, RCX, RDX, RBX,
    RSI, RDI, RSP, RBP,
    R8,  R9,  R10, R11,
    R12, R13, R14, R15,
    RIP;

    static Register get(String register){
        if(!register.startsWith("%"))
            throw new IllegalArgumentException("not a register: "+register);
        switch(register.substring(1).toLowerCase()){
            case "rip":
                return RIP;
            case "rax":
                return RAX;
            case "rcx":
                return RCX;
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
        return null;
    }

    @Override
    public long get(VirtualMachine vm) {
        return vm.load(this);
    }
}
