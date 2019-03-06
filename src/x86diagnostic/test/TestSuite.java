package x86diagnostic.test;

import x86diagnostic.Main;
import x86diagnostic.parse.Parser;
import x86diagnostic.vm.Computer;
import x86diagnostic.vm.DivergeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TestSuite {

    enum Status { ACCEPT, REJECT, ERROR, DIVERGED; }

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

    public static void testAll(boolean verbose) throws IOException {

        System.out.println("Running test suite...\n");

        List<Test> tests = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get("tests/tests.txt"))) {
            if (line.startsWith("#") || line.isEmpty()) continue;
            tests.add(Test.parse(line));
        }

        Map<Test, Status> testResults = new HashMap<>();
        Map<Test, Long>   testValues = new HashMap<>();
        Map<Test, String>   testIts = new HashMap<>();
        int passed = 0;
        for (Test t : tests) {
            Status status = Status.REJECT;
            long x = -1, its = 0;
            try {
                Computer.Computation c = Computer.computeTest(t.file, verbose, 10000);
                x = c.x;
                its = c.instrs;
                if (x == t.expected) {
                    status = Status.ACCEPT;
                }
            } catch (Exception e) {
                e.printStackTrace();
                status = Status.ERROR;
            } catch (DivergeException e) {
                status = Status.DIVERGED;
            }
            if(status == Status.ACCEPT) {
                passed++;
                continue;
            }
            if(status!=Status.ERROR && status!=Status.DIVERGED)
                testValues.put(t, x);

            testResults.put(t, status);
            testIts.put(t, status==Status.DIVERGED?"-":""+its);
        }
        int i = 0;
        System.out.println("Note: Only showing incorrect tests ("+passed+" passed).\n");
        System.out.println("#     file                      iterations   received     expected     status         description");
        System.out.println("+----+-------------------------+------------+------------+------------+--------------+---------------------------------------------------------------------------------------------+");

        List<Test> sortedTests = new ArrayList<>(testResults.keySet());
        Collections.sort(sortedTests, (t,u) -> {
            Status st = testResults.get(t), su = testResults.get(u);
            if(st == su){
                return t.compareTo(u);
            } else
                return -st.compareTo(su);
        });

        for(Test t : sortedTests){
            String x = "-";
            if(testValues.containsKey(t)){
                x=""+testValues.get(t);
            }
            System.out.println(" "+pad(++i + ".", 5) + pad(t.toString(), 26) +" "+ padl(testIts.get(t), 10)+"     " + padl(x, 8) + "     " + padl(t.expected, 8) + "     " + pad(""+testResults.get(t), 9) + "      " + t.description);
        }

        Scanner s = new Scanner(System.in);
        System.out.print("\nEnter index to get more details.\n> ");

        int j = s.nextInt();
        if(j>=1 && j<=sortedTests.size()){
            try {
                Main.detailed("tests/"+sortedTests.get(j-1).file);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (DivergeException e) {
                e.printStackTrace();
            }
        }
    }
    private static String padl(long x, int l){
        return padl(x+"", l);
    }
    public static String pad(String s, int l){
        if(s.length()<l)
            return pad(s+" ",l);
        return s;
    }
    private static String padl(String s, int l){
        if(s.length()<l)
            return padl(" "+s,l);
        return s;
    }
}
