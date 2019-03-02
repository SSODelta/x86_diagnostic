package oram.vm;

import oram.operand.Operand;
import oram.operand.Register;

/**
 * Interface for a 64-bit virtual machine
 * running on x86-64 machine code.
 */
public interface VirtualMachine {

    default boolean is(ConditionCode cc){
        return cc.is(flag(Flag.ZF), flag(Flag.SF), flag(Flag.OF), flag(Flag.CF));
    }

    long compute();

    /**
     * Jumps to the specified memory address.
     * @param loc
     */
    void jump(long loc);

    /**
     * Returns the value stored in the given register.
     * @param r
     * @return
     */
    long load(Register r);

    /**
     * Set condition codes according to 'value'.
     * @param value
     */
    void condition(long value);

    /**
     * Checks whether the given flag is set.
     * @param f
     * @return
     */
    boolean flag(Flag f);

    /**
     * Returns the word stored at the given memory address.
     * @param address
     * @return
     */
    long read(long address);

    /**
     * Pushes a word unto the stack.
     * @param word
     */
    void push(long word);

    /**
     * Set the value of the word located at the given operand.
     * @param o The operand
     * @param value The value
     */
    void set(Operand o, long value);

    /**
     * Pops the topmost word of the value.
     * @return
     */
    long pop();

}
