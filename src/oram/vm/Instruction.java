package oram.vm;

import oram.operand.Addressable;
import oram.operand.Operand;
import oram.operand.Register;
import oram.parse.LineParser;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Instruction {
    void apply(VirtualMachine vm);

    Instruction ret = mk(vm -> {},"ret");

    /// SPECIAL ///
    Instruction nop = vm -> {},
                err = vm -> {throw new IllegalStateException("error");};

    static Instruction mk(Consumer<VirtualMachine> f, Object... lbl){
        return new Instruction(){
            @Override
            public void apply(VirtualMachine vm) {
                f.accept(vm);
            }
            private String pad(String s, int l){
                if(s.length()<l)
                    return pad(s+" ",l);
                return s;
            }
            public String toString(){
                return "\t"+Arrays.stream(lbl).map(o -> pad(o.toString(),25)).reduce("", (s1,s2) -> s1+s2);
            }
        };
    }

    /// DATA MOVEMENT ///
    static Instruction mov(Register r1, Register r2){
        return mov(r1, r2, DataType.QUAD, DataType.QUAD);
    }
    static Instruction mov(Operand src, Operand dest, DataType type1, DataType type2){
        return mov(src, dest, ConditionCode.always, type1, type2);
    }
    static Instruction mov(Operand src, Operand dest, ConditionCode cc, DataType type1, DataType type2) {
        return mk(vm -> {if(vm.is(cc))vm.set(dest, src.get(vm,type1),type2);},"mov"+type1+(type2!=type1?type2:""),src,dest);
    }

    static Instruction push(Operand src, DataType type) {
        return mk(vm ->vm.push(src.get(vm, type), type),"push"+type,src);
    }

    static Instruction pop(Register dest) {
        return pop(dest, DataType.QUAD);
    }
    static Instruction pop(Operand dest, DataType type) {
        return mk(vm ->vm.set(dest, vm.pop(type), type),"pop"+type,dest);
    }

    /// UNARY OPERATIONS ///

    static Instruction unop(Operand dest, Function<Long, Long> f,String lbl, DataType type) {
        return mk(vm ->vm.set(dest, f.apply(dest.get(vm,type)),type),lbl+type,dest);
    }

    static Instruction inc(Register r){
        return inc(r, DataType.QUAD);
    }
    static Instruction inc(Operand dest, DataType type) {
        return unop(dest, x -> x + 1,"inc"+type, type);
    }
    static Instruction dec(Register r){
        return dec(r, DataType.QUAD);
    }
    static Instruction dec(Operand dest, DataType type) {
        return unop(dest, x -> x - 1,"dec"+type, type);
    }

    static Instruction neg(Operand dest, DataType type) {
        return unop(dest, x -> -x,"neg"+type, type);
    }

    static Instruction not(Operand dest, DataType type) {
        return unop(dest, x -> ~x,"not"+type, type);
    }

    /// BINARY OPERATIONS ///
    static Instruction lea(Addressable src, Operand dest, DataType type) {
        return mk(vm ->vm.set(dest, src.address(vm), type),"lea"+type,src,dest);
    }

    static Instruction binop(Operand src, Operand dest, BiFunction<Long, Long, Long> f, String lbl, DataType type) {
        return mk(vm ->vm.set(dest, f.apply(dest.get(vm,type), src.get(vm,type)),type),lbl+type,src,dest);
    }

    static Instruction add(Operand src, Operand dest, DataType type) {
        return binop(src, dest, (a, b) -> a + b,"add",type);
    }

    static Instruction sub(Operand src, Operand dest, DataType type) {
        return binop(src, dest, (a, b) -> a - b,"sub",type);
    }

    static Instruction imul(Operand src, Operand dest, DataType type) {
        return binop(src, dest, (a, b) -> a * b,"imul",type);
    }

    static Instruction xor(Operand src, Operand dest, DataType type) {
        return binop(src, dest, (a, b) -> a ^ b,"xor",type);
    }

    static Instruction or(Operand src, Operand dest, DataType type) {
        return binop(src, dest, (a, b) -> a | b,"or",type);
    }

    static Instruction and(Operand src, Operand dest, DataType type) {
        return binop(src, dest, (a, b) -> a & b, "and",type);
    }

    /// SHIFT OPERATIONS ///
    static Instruction sal(int k, Operand dest, DataType type) {
        return shl(k, dest,type);
    }

    static Instruction shl(int k, Operand dest, DataType type) {
        return unop(dest, x -> x << k,"shl",type);
    }

    static Instruction sar(int k, Operand dest, DataType type) {
        return unop(dest, x -> x >> k,"sar",type);
    }

    static Instruction shr(int k, Operand dest, DataType type) {
        return unop(dest, x -> x >>> k,"shr",type);
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
    static Instruction cmp(Operand s2, Operand s1, DataType type){
        return mk(vm ->vm.condition(s1.get(vm,type) - s2.get(vm,type)),"cmp"+type,s2,s1);
    }
    static Instruction test(Operand s2, Operand s1, DataType type){
        return mk(vm ->vm.condition(s1.get(vm,type) & s2.get(vm,type)),"test"+type,s2,s1);
    }

    /// CONDITIONAL SET INSTRUCTIONS ///
    static Instruction set(Operand dest, ConditionCode cc, DataType type){
        return mk(vm ->vm.set(dest, vm.is(cc)?1:0,type),"set",dest);
    }

    // LABEL ///
    class Label implements Instruction {
        private String str;
        public Label(String str){
            this.str = str;
        }
        public void apply(VirtualMachine vm) { }
        public String label() {
            return str;
        }
        public String toString() {return label()+":";}
    }
    static Instruction label(String str) { return new Label(str); }

    class Constant implements Instruction {
        private long value;
        private DataType type;
        public Constant(long value, DataType type) { this.value=value; this.type=type;}
        public void apply(VirtualMachine vm) { }
        public long value(){ return value; }
        public DataType type() { return type; }
        public String toString() { return "."+type.full() + "\t"+value;}
    }
    static Instruction constant(long value, DataType type){
        return new Constant(value, type);
    }

    /// JUMP INSTRUCTIONS ///
    static Instruction jmp(Operand location){
        return jmp(location, ConditionCode.always);
    }
    static Instruction jmp(Operand location, ConditionCode cc){
        return mk(vm ->{if(vm.is(cc))vm.jump(location.get(vm,DataType.QUAD));},"j"+cc.toString(),location);
    }
    static Instruction jmp(String lbl){
        return jmp(lbl, ConditionCode.always);
    }
    static Instruction jmp(String lbl, ConditionCode cc){
        return mk(vm ->{if(vm.is(cc))vm.jump(lbl);}, "j"+cc.toString(),lbl);
    }

    /// PROCEDURE CALL INSTRUCTION ///
    static Instruction call(Operand loc){
        return err;
    }

    static Instruction leave(){
        return compose("leave",
            mov(Register.RSP, Register.RBP),
            pop(Register.RBP));
    }

    static Instruction compose(String lbl,Instruction... instrs){
        return mk(vm ->Arrays.stream(instrs).forEach(i->i.apply(vm)),lbl);
    }

    static Instruction parse(String line) {
        if(!line.startsWith("\t")){
            if(!line.contains(":"))
                throw new IllegalStateException("invalid line, not a label: "+line);
            return new Instruction.Label(line.substring(0,line.indexOf(":")));
        }
        LineParser lp = new LineParser(line.replace(" ",""));
        if(lp.hd().startsWith("j") || lp.hd().startsWith("mov")){
            if(lp.hd().equals("mov")){
                if(lp.isLabel(1))
                    return Instruction.mov(lp.op(1), lp.op(2), ConditionCode.always, lp.type(1), lp.type(2));
                return Instruction.mov(lp.op(1), lp.op(2), ConditionCode.always, lp.type(1), lp.type(2));
            }
            for(ConditionCode cc : ConditionCode.codes){
                if(lp.hd().equals("j"+cc.toString())){
                    if(lp.isLabel(1))
                        return Instruction.jmp(lp.lbl(1), cc);
                    return Instruction.jmp(lp.op(1), cc);
                }
                if(lp.hd().equals("mov"+cc.toString())){
                    if(lp.isLabel(1))
                        return Instruction.mov(lp.op(1), lp.op(2), cc, lp.type(1), lp.type(2));
                    return Instruction.mov(lp.op(1), lp.op(2), cc, lp.type(1), lp.type(2));
                }
            }
        }
        switch(lp.hd()){
            case "test":
                return Instruction.test(lp.op(1), lp.op(2), lp.type(1));
            case "set":
                return Instruction.set(lp.op(1), ConditionCode.greater_signed, lp.type(1));
            case "push":
                return Instruction.push(lp.op(1), lp.type(1));
            case "pop":
                return Instruction.pop(lp.op(1), lp.type(1));
            case "ret":
                return Instruction.ret;
            case "add":
                return Instruction.add(lp.op(1), lp.op(2), lp.type(1));
            case "xor":
                return Instruction.xor(lp.op(1), lp.op(2), lp.type(1));
            case "sub":
                return Instruction.sub(lp.op(1), lp.op(2), lp.type(1));
            case "cmp":
                System.out.println("type: "+lp.type(1));
                return Instruction.cmp(lp.op(1), lp.op(2), lp.type(1));
            case ".long":
                return Instruction.constant(lp.word(1), DataType.LONG);
        }
        throw new IllegalStateException("invalid instruction: "+lp.hd());
    }
}
