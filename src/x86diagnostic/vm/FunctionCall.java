package x86diagnostic.vm;

import x86diagnostic.operand.AbsoluteImm;
import x86diagnostic.operand.Register;
public class FunctionCall implements Instruction {

    public String toString(){
        return "call"+type+"\t"+name;
    }

    static class ArgumentList {
        private long[] arguments;
        public long val(int i){
            return arguments[i-1];
        }
        public ArgumentList(VirtualMachine vm){
            this.arguments=new long[6];
            arguments[0] = vm.load(Register.Type.RDI);
            arguments[1] = vm.load(Register.Type.RSI);
            arguments[2] = vm.load(Register.Type.RDX);
            arguments[3] = vm.load(Register.Type.RCX);
            arguments[4] = vm.load(Register.Type.R8);
            arguments[5] = vm.load(Register.Type.R9);
        }
    }

    public interface LibraryMethod {

        long compute(ArgumentList al, VirtualMachine vm, DataType type);

        LibraryMethod MEMCPY = (al, vm, type) -> {
            long str1 = al.val(1),
                    str2 = al.val(2),
                    n = al.val(3);

            for (int i = 0; i < n; i++)
                vm.set(new AbsoluteImm(str1 + i), vm.read(str2 + i, type), type);

            return 0L;
        };
    }

    private String name;
    private LibraryMethod method;
    private DataType type;
    public FunctionCall(String name, LibraryMethod method, DataType type){
        this.name = name;
        this.method = method;
        this.type = type;
    }

    @Override
    public void apply(VirtualMachine vm) {
        vm.set(Register.Type.RAX, method.compute(new ArgumentList(vm), vm,type));
    }
}
