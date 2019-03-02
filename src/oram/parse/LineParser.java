package oram.parse;

import oram.operand.Operand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class LineParser {
    private String[] args;
    public LineParser(String line){
        List<String> args = new ArrayList<>();
        String[] k = line.substring(1).split("\t");
        args.add(k[0]);
        int depth = 0;
        String accum = "";
        if(k.length>1)
        for(char c : k[1].toCharArray()){
            if(c=='(')depth++;
            if(c==')')depth--;
            if(c==',' && depth==0){
                args.add(accum);
                accum="";
            } else accum+=c;
        }
        if(!accum.isEmpty())args.add(accum);
        this.args = args.toArray(new String[]{});
    }
    public String hd(){
        return args[0];
    }
    public Operand op(int i){
        return Operand.parse(args[i]);
    }

    public String toString(){
        return Arrays.stream(args).reduce("[", (s1, s2)->{if(s1.length()<=1) return s1+s2; else return s1+"], ["+s2;})+"]";
    }

    public String lbl(int i) {
        return args[i];
    }
}
