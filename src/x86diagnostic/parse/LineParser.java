package x86diagnostic.parse;

import x86diagnostic.operand.Operand;
import x86diagnostic.vm.DataType;
import x86diagnostic.vm.FunctionCall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LineParser {
    private String[] args;
    private List<String> suffixes;
    public LineParser(String line){
        suffixes = new ArrayList<>();
        line = line.replace("  "," ");
        while(line.endsWith(" "))
            line=line.substring(0,line.length()-1);
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
        String suff = this.args[0].substring(this.args[0].length()-1);
        if(!this.args[0].startsWith("j")) {
            while (DataType.isDataType(suff)) {
                int l = this.args[0].length();
                this.args[0] = this.args[0].substring(0, l - 1);
                suffixes.add(suff);
                suff = this.args[0].substring(l - 1);
            }
        }
        suffixes.add(null);
        Collections.reverse(suffixes);
    }
    public DataType type(int i){
        if(suffixes.size()>1)
            while(i>=suffixes.size()) {
                i -= suffixes.size() - 1;
            }
        else
            return DataType.QUAD;
        for(DataType t : DataType.values())
            if(suffixes.get(i).equals(t.toString()))
                return t;
        throw new IllegalStateException("invalid data type: "+args[0].substring(args[0].length()-1));
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

    public FunctionCall.LibraryMethod lib(int i){
        switch(lbl(i)){
            case "_memcpy":
                return FunctionCall.LibraryMethod.MEMCPY;
        }
        throw new IllegalStateException("no such library method: "+lbl(i));
    }

    public boolean isLabel(int i){
        return !args[i].contains("%") && !args[i].contains("$");
    }

    public String lbl(int i) {
        return args[i];
    }

    public long word(int i) {
        return Long.parseLong(args[i]);
    }
}
