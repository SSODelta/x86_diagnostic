package oram.operand;

import oram.vm.VirtualMachine;

public interface Operand {

    static Operand parse(String arg) {
        if(arg.startsWith("$"))
            return new Immediate(Long.parseLong(arg.substring(1)));
        if(arg.startsWith("%"))
            return Register.get(arg);
        return Register.RAX;
    }

    long get(VirtualMachine vm);

}
