package x86diagnostic.vm;

public class Configuration {

    private Instruction[] instructions;
    private boolean verbose, registers;

    public Configuration(Instruction[] instructions, boolean verbose, boolean registers){
        this.instructions = instructions;
        this.verbose = verbose;
        this.registers = registers;
    }

    public Instruction[] getInstructions() {
        return instructions;
    }

    public Configuration setInstructions(Instruction[] instructions) {
        this.instructions = instructions;
        return this;
    }

    public boolean isRegisters() {
        return registers;
    }

    public Configuration setRegisters(boolean registers) {
        this.registers = registers;
        return this;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public Configuration setVerbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }
}
