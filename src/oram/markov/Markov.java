package oram.markov;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Markov {

    private Map<MarkovInstruction, Map<MarkovInstruction, Long>> matrix;
    private String name;

    public Markov(String name){
        this.name=name;
        matrix = new HashMap<>();
    }

    public long get(MarkovInstruction a, MarkovInstruction b){
        return matrix.getOrDefault(a, new HashMap<>()).getOrDefault(b, 0L);
    }

    public void add(MarkovInstruction a, MarkovInstruction b){
        if(!matrix.containsKey(a))
            matrix.put(a, new HashMap<>());

        matrix.get(a).put(b, matrix.get(a).getOrDefault(b,0L)+1);
    }

    public List<MarkovInstruction> objects(){
        Set<MarkovInstruction> objs = new HashSet<>();
        for(MarkovInstruction a : matrix.keySet()){
            objs.add(a);
            for(MarkovInstruction b : matrix.get(a).keySet())
                objs.add(b);
        }
        List<MarkovInstruction> o = new ArrayList<>(objs);
        Collections.sort(o, Comparator.comparing(a -> a.toString()));
        return o;
    }

    public void print() throws IOException {
        System.out.println(this);
        PrintWriter pw = new PrintWriter(name+".csv");
        pw.println("sep=,");
        List<MarkovInstruction> os = objects();
        for(MarkovInstruction o : os)
            pw.println(","+o.toString());

        for(MarkovInstruction a : os)
        for(MarkovInstruction b : os)
            pw.println(matrix.getOrDefault(a, new HashMap<>()).getOrDefault(b, 0L)+",");

        pw.close();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        List<MarkovInstruction> objs = objects();

        sb.append(pad("",14));
        for(MarkovInstruction a : objs)
            sb.append(pad(a.toString(),14));
        sb.append("\n");
        for(MarkovInstruction a : objs){
            sb.append(pad(a.toString(), 14));
            for(MarkovInstruction b : objs){
                sb.append(pad(matrix.getOrDefault(a, new HashMap<>()).getOrDefault(b, 0L)+"", 14));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String pad(String s, int l){
        if(s.length()>=l)
            return s;
        return pad(s+" ",l);
    }
}
