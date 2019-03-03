package oram.operand;

import oram.vm.DataType;
import oram.vm.VirtualMachine;

public interface Operand {

    static Operand parse(String arg) {
        if(arg.startsWith("$"))
            return new Immediate(Long.parseLong(arg.substring(1)));
        if(arg.startsWith("%"))
            return Register.get(arg);
        if(arg.contains(",")) { // four arguments

        } else { // with or without offset
            if(arg.startsWith("(")){ // no offset
                return new QuadAddress(0,Register.get(arg), Register.NONE, 0);
            } else {
                int s = arg.indexOf("(");
                String offset = arg.substring(0,s),
                       reg = arg.substring(s);
                return new QuadAddress(Long.parseLong(offset), Register.get(reg), Register.NONE, 0);
            }
        }
        return Register.RAX;
    }

    long get(VirtualMachine vm, DataType type);

}
