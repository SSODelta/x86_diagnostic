package x86diagnostic.test;

import com.sun.org.apache.regexp.internal.RE;
import x86diagnostic.vm.Computer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TestSuite {

    enum Status { ACCEPT, REJECT, ERROR; }

    static class Test implements Comparable<Test> {
        private final String file, description;
        private final long expected;

        private Test(String file, long expected, String description){
            this.file = file;
            this.expected = expected;
            this.description=description;
        }

        public static Test parse(String line){
            List<String> lines =
            Arrays.stream(line.split(";"))
                  .map(l->l.replace("  "," "))
                  .map(l -> {while(l.startsWith(" "))l=l.substring(1);return l;})
                  .collect(Collectors.toList());
            return new Test(lines.get(0), Long.parseLong(lines.get(1)), lines.get(2));
        }

        @Override
        public boolean equals(Object o){
            if(o.getClass()!=Test.class)
                return false;
            return file.equals(((Test)o).file);
        }

        @Override
        public int hashCode(){
            return file.hashCode();
        }

        @Override
        public int compareTo(Test o) {
            return file.compareTo(o.file);
        }

        public String toString(){
            return file;
        }
    }

    public static void main(String[] args) throws IOException {

        System.out.println("------------------------");
        System.out.println("-- RUNNING TEST SUITE --");
        System.out.println("------------------------");

        List<Test> tests = new ArrayList<>();
        for(String line : Files.readAllLines(Paths.get("tests/tests.txt"))){
            if(line.startsWith("#") || line.isEmpty())continue;
            tests.add(Test.parse(line));
        }
        Collections.sort(tests);

        int i=0;
        for(Test t : tests){
            Status status = Status.REJECT;

            try {
                if (Computer.computeTest(t.file) == t.expected) {
                    status = Status.ACCEPT;
                }
            } catch (Exception e){
                e.printStackTrace();
                status = Status.ERROR;
            }

            System.out.println(pad(++i+".", 5)+pad(t.toString(),26)+status);
        }
    }
    private static String pad(String s, int l){
        if(s.length()<l)
            return pad(s+" ",l);
        return s;
    }
}
