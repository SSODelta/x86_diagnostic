package oram.markov;

import oram.vm.MarkovComputingVM;

public class MarkovInstruction implements Comparable<MarkovInstruction> {

    @Override
    public int compareTo(MarkovInstruction o) {
        return toString().compareTo(o.toString());
    }

    public enum Type { READ, SET }

    private Type type;
    private Object o;
    public MarkovInstruction(Type t, Object o){
        this.type = t;
        this.o = o;
    }
    public String toString(){ return "read["+o.toString()+"]";}
    public int hashCode(){
        return toString().hashCode();
    }

    public boolean equals(Object o){
         if(o.getClass() != MarkovInstruction.class)
             return false;
         return toString().equals(o.toString());
    }

}
