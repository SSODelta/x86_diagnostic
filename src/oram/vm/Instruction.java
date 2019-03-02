package oram.vm;

import oram.operand.AbsoluteReg;
import oram.operand.Addressable;
import oram.operand.Operand;
import oram.operand.Register;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Instruction {
    static Instruction ret() {
        return nop;
    }

    void apply(VirtualMachine vm);

    /// SPECIAL ///
    Instruction nop = vm -> {},
                err = vm -> {throw new IllegalStateException("error");};


    /// DATA MOVEMENT ///
    static Instruction mov(Operand src, Operand dest){
        return mov(src, dest, ConditionCode.always);
    }
    static Instruction mov(Operand src, Operand dest, ConditionCode cc) {
        return vm -> vm.set(dest, src.get(vm));
    }

    static Instruction push(Operand src) {
        return vm -> vm.push(src.get(vm));
    }

    static Instruction pop(Operand dest) {
        return vm -> vm.set(dest, vm.pop());
    }

    /// UNARY OPERATIONS ///

    static Instruction unop(Operand dest, Function<Long, Long> f) {
        return vm -> vm.set(dest, f.apply(dest.get(vm)));
    }

    static Instruction inc(Operand dest) {
        return unop(dest, x -> x + 1);
    }

    static Instruction dec(Operand dest) {
        return unop(dest, x -> x - 1);
    }

    static Instruction neg(Operand dest) {
        return unop(dest, x -> -x);
    }

    static Instruction not(Operand dest) {
        return unop(dest, x -> ~x);
    }

    /// BINARY OPERATIONS ///
    static Instruction lea(Addressable src, Operand dest) {
        return vm -> vm.set(dest, src.address(vm));
    }

    static Instruction binop(Operand src, Operand dest, BiFunction<Long, Long, Long> f) {
        return vm -> vm.set(dest, f.apply(dest.get(vm), src.get(vm)));
    }

    static Instruction add(Operand src, Operand dest) {
        return binop(src, dest, (a, b) -> a + b);
    }

    static Instruction sub(Operand src, Operand dest) {
        return binop(src, dest, (a, b) -> a - b);
    }

    static Instruction imul(Operand src, Operand dest) {
        return binop(src, dest, (a, b) -> a * b);
    }

    static Instruction xor(Operand src, Operand dest) {
        return binop(src, dest, (a, b) -> a ^ b);
    }

    static Instruction or(Operand src, Operand dest) {
        return binop(src, dest, (a, b) -> a | b);
    }

    static Instruction and(Operand src, Operand dest) {
        return binop(src, dest, (a, b) -> a & b);
    }

    /// SHIFT OPERATIONS ///
    static Instruction sal(int k, Operand dest) {
        return shl(k, dest);
    }

    static Instruction shl(int k, Operand dest) {
        return unop(dest, x -> x << k);
    }

    static Instruction sar(int k, Operand dest) {
        return unop(dest, x -> x >> k);
    }

    static Instruction shr(int k, Operand dest) {
        return unop(dest, x -> x >>> k);
    }

    /// SPECIAL ARITHMETIC OPERATIONS ///
    static Instruction imul(Operand src){
        return err;
    }
    static Instruction mul(Operand src){
        return err;
    }
    static Instruction idiv(Operand src){
        return err;
    }
    static Instruction div(Operand src){
        return err;
    }

    /// Comparison and Test Instruction///
    static Instruction cmp(Operand s2, Operand s1){
        return vm -> vm.condition(s1.get(vm) - s2.get(vm));
    }
    static Instruction test(Operand s2, Operand s1){
        return vm -> vm.condition(s1.get(vm) & s2.get(vm));
    }

    /// CONDITIONAL SET INSTRUCTIONS ///
    static Instruction set(Operand dest, ConditionCode cc){
        return vm -> vm.set(dest, vm.is(cc)?1:0);
    }

    // LABEL ///
    class Label implements Instruction {

        private String str;
        public Label(String str){
            this.str = str;
        }
        @Override
        public void apply(VirtualMachine vm) { }

        public String label() {
            return str;
        }
    }
    static Instruction label(String str){
        return new Label(str);
    }

    /// JUMP INSTRUCTIONS ///
    static Instruction jmp(Operand location){
        return jmp(location, ConditionCode.always);
    }
    static Instruction jmp(Operand location, ConditionCode cc){
        return vm -> {if(vm.is(cc))vm.jump(location.get(vm));};
    }
    static Instruction jmp(String lbl){
        return jmp(lbl, ConditionCode.always);
    }
    static Instruction jmp(String lbl, ConditionCode cc){
        return vm -> {if(vm.is(cc))vm.jump(lbl);};
    }

    /// PROCEDURE CALL INSTRUCTION ///
    static Instruction call(Operand loc){
        return err;
    }

    static Instruction leave(){
        return compose(
            mov(Register.RSP, Register.RBP),
            pop(Register.RBP));
    }

    static Instruction compose(Instruction... instrs){
        return vm -> Arrays.stream(instrs).forEach(i->i.apply(vm));
    }
}
