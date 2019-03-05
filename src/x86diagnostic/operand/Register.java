package x86diagnostic.operand;

import x86diagnostic.vm.DataType;
import x86diagnostic.vm.VirtualMachine;

import static x86diagnostic.operand.Register.Type.*;
import static x86diagnostic.vm.DataType.*;

public class Register implements Operand {

    public enum Type {
        RAX, RCX, RDX, RBX,
        RSI, RDI, RSP, RBP,
        R8, R9, R10, R11,
        R12, R13, R14, R15,
        RIP, NONE;
    }

    public static final Register NONE = new Register(Type.NONE, QUAD);

    private Type registerType;
    private DataType dataType;

    public Type type(){
        return registerType;
    }

    public Register(Type registerType, DataType dataType){
        this.registerType = registerType;
        this.dataType = dataType;
    }

    public boolean equals(Object o){
        if(o.getClass() != Register.class)return false;
        return registerType == ((Register)o).registerType;
    }

    @Override
    public int hashCode() {
        return registerType.hashCode();
    }

    public String toString(){
        return "%"+registerType.toString().toLowerCase();
    }

    static Register get(String register){
        register = register.replace("(","").replace(")","");
        if(!register.startsWith("%"))
            throw new IllegalArgumentException("not a register: "+register);
        switch(register.substring(1).toLowerCase()){
            case "rip":
                return new Register(RIP, QUAD);
            case "al":
                return new Register(RAX, WORD);
            case "eax":
                return new Register(RAX, LONG);
            case "rax":
                return new Register(RAX, QUAD);
            case "cl":
                return new Register(RCX, WORD);
            case "ecx":
                return new Register(RCX, LONG);
            case "rcx":
                return new Register(RCX, QUAD);
            case "dl":
                return new Register(RDX, WORD);
            case "edx":
                return new Register(RDX, LONG);
            case "rdx":
                return new Register(RDX, QUAD);
            case "rbx":
                return new Register(RBX, QUAD);
            case "rsi":
                return new Register(RSI, QUAD);
            case "rdi":
                return new Register(RDI, QUAD);
            case "rsp":
                return new Register(RSP, QUAD);
            case "rbp":
                return new Register(RBP, QUAD);
            case "r8":
                return new Register(R8, QUAD);
            case "r9":
                return new Register(R9, QUAD);
            case "r10":
                return new Register(R10, QUAD);
            case "r11":
                return new Register(R11, QUAD);
            case "r12":
                return new Register(R12, QUAD);
            case "r13":
                return new Register(R13, QUAD);
            case "r14":
                return new Register(R14, QUAD);
            case "r15":
                return new Register(R15, QUAD);

        }
        throw new IllegalStateException("no such register: "+register);
    }

    @Override
    public long get(VirtualMachine vm, DataType type) {
        return vm.load(this);
    }
}
