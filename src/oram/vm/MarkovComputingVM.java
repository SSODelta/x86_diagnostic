package oram.vm;

import oram.markov.Markov;
import oram.markov.MarkovImaging;
import oram.markov.MarkovInstruction;
import oram.operand.Operand;
import oram.operand.Register;

import java.io.IOException;

public class MarkovComputingVM implements VirtualMachine {

    private VirtualMachine vm;
    private Markov markov;
    private MarkovInstruction lastInstruction = null;

    public MarkovComputingVM(VirtualMachine vm){
        this.vm = vm;
        this.markov =  new Markov("markov");
    }

    public void printMarkov() throws IOException {
        //System.out.println("\n------------------------"+
                           //"\n--    MARKOV MATRIX   --"+
                           //"\n------------------------");
       //markov.print();
        new MarkovImaging(markov).output("markov.png");
    }

    @Override
    public String mem() {
        return vm.mem();
    }

    @Override
    public void jump(String lbl) {
        vm.jump(lbl);
    }

    @Override
    public void jump(long loc) {
        vm.jump(loc);
    }

    @Override
    public long load(Register r) {
        MarkovInstruction mi = new MarkovInstruction(MarkovInstruction.Type.READ, r);
        if(lastInstruction == null){
            lastInstruction = mi;
        } else {
            markov.add(lastInstruction, mi);
            lastInstruction = mi;
        }
        return vm.load(r);
    }

    @Override
    public void condition(long value) {
        vm.condition(value);
    }

    @Override
    public boolean flag(Flag f) {
        return vm.flag(f);
    }

    @Override
    public long read(long address, DataType type) {
        MarkovInstruction mi = new MarkovInstruction(MarkovInstruction.Type.READ, address);
        if(lastInstruction==mi){
            lastInstruction = mi;
        } else {
            markov.add(lastInstruction, mi);
            lastInstruction = mi;
        }
        return vm.read(address, type);
    }

    @Override
    public void push(long word, DataType type) {
        vm.push(word, type);
    }

    @Override
    public void set(Operand o, long value, DataType type) {
        MarkovInstruction mi = new MarkovInstruction(MarkovInstruction.Type.SET, o);
        if(lastInstruction==null){
            lastInstruction = mi;
        } else {
            markov.add(lastInstruction, mi);
            lastInstruction = mi;
        }
        vm.set(o,value,type);
    }

    @Override
    public long pop(DataType type) {
        return vm.pop(type);
    }

    @Override
    public long arrayOffset(String array) {
        return vm.arrayOffset(array);
    }
}
