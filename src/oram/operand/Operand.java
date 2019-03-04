package oram.operand;

import oram.vm.DataType;
import oram.vm.VirtualMachine;

public interface Operand {

    static Operand parse(String arg) {
        if(arg.contains("arr")){
            if(arg.contains("%rip")){
                arg = arg.substring(0,arg.indexOf("("));
                int i = arg.indexOf("+"), offset = 0;
                String lbl = arg;
                if(i>-1) {
                    lbl = arg.substring(0,i);
                    offset = Integer.parseInt(arg.substring(i + 1));
                }
                return new ArrayRipOffset(lbl, offset);
            } else throw new IllegalStateException("unknown operand: "+arg);
        }
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
