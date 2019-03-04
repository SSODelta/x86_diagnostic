package oram.vm;

import oram.markov.Markov;
import oram.operand.Operand;
import oram.operand.Register;

import java.io.IOException;

public class MarkovComputingVM implements VirtualMachine {

    private VirtualMachine vm;
    private Markov registers, read, set;
    private Register lastRegister = null;
    private long lastRead = -1, lastSet = -1;

    public MarkovComputingVM(VirtualMachine vm){
        this.vm = vm;
        this.registers =  new Markov("regs");
        this.read = new Markov("read");
        this.set = new Markov("set");
    }

    public void printMarkov() throws IOException {
        System.out.println("\n------------------------"+
                           "\n--    MARKOV MATRIX   --"+
                           "\n------------------------");
        System.out.println("Registers:");
        registers.print();
        System.out.println("\nRead:");
        read.print();
        System.out.println("\nSet:");
        set.print();
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
        if(lastRegister == null){
            lastRegister = r;
        } else {
            registers.add(lastRegister, r);
            lastRegister = r;
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
        if(lastRead==-1){
            lastRead = address;
        } else {
            read.add(lastRead, address);
            lastRead = address;
        }
        return vm.read(address, type);
    }

    @Override
    public void push(long word, DataType type) {
        vm.push(word, type);
    }

    @Override
    public void set(Operand o, long value, DataType type) {
        if(lastSet==-1){
            lastSet = value;
        } else {
            set.add(lastSet, value);
            lastSet = value;
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
