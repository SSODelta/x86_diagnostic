package oram.markov;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Markov {

    private Map<Object, Map<Object, Long>> matrix;
    private String name;

    public Markov(String name){
        this.name=name;
        matrix = new HashMap<>();
    }

    public void add(Object a, Object b){
        if(!matrix.containsKey(a))
            matrix.put(a, new HashMap<>());

        matrix.get(a).put(b, matrix.get(a).getOrDefault(b,0L)+1);
    }

    public long get(Object a, Object b){
        if(matrix.containsKey(a))
            return matrix.get(a).getOrDefault(b, 0L);
        return 0;
    }

    public List<Object> objects(){
        Set<Object> objs = new HashSet<>();
        for(Object a : matrix.keySet()){
            objs.add(a);
            for(Object b : matrix.get(a).keySet())
                objs.add(b);
        }
        List<Object> o = new ArrayList<>(objs);
        Collections.sort(o, Comparator.comparing(a -> ((Comparable) a)));
        return o;
    }

    public void print() throws IOException {
        System.out.println(this);
        PrintWriter pw = new PrintWriter(name+".csv");
        pw.println("sep=,");
        List<Object> os = objects();
        for(Object o : os)
            pw.println(","+o.toString());

        for(Object a : os)
        for(Object b : os)
            pw.println(matrix.getOrDefault(a, new HashMap<>()).getOrDefault(b, 0L)+",");

        pw.close();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        List<Object> objs = objects();

        sb.append(pad("",14));
        for(Object a : objs)
            sb.append(pad(a.toString(),14));
        sb.append("\n");
        for(Object a : objs){
            sb.append(pad(a.toString(), 14));
            for(Object b : objs){
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
