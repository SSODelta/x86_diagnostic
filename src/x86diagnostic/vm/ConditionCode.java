package x86diagnostic.vm;


public interface ConditionCode {


    boolean is(boolean ZF, boolean SF, boolean OF, boolean CF);

    static ConditionCode mk(ConditionCode cc, String lbl){
        return new ConditionCode() {
            public boolean is(boolean ZF, boolean SF, boolean OF, boolean CF) {
                return cc.is(ZF,SF,OF,CF);
            }
            public String toString(){
                return lbl;
            }
        };
    }

    ConditionCode always            = mk((ZF,SF,OF,CF) -> true, "mp"),
                  equal             = mk((ZF,SF,OF,CF) -> ZF,"e"),
                  not_equal         = mk((ZF,SF,OF,CF) -> !ZF, "ne"),
                  negative          = mk((ZF,SF,OF,CF) -> SF, "s"),
                  nonnegative       = mk((ZF,SF,OF,CF) -> !SF, "ns"),
                  greater_signed    = mk((ZF,SF,OF,CF) -> !(SF^OF)&!ZF, "g"),
                  geq_signed        = mk((ZF,SF,OF,CF) -> !(SF^OF), "ge"),
                  less_signed       = mk((ZF,SF,OF,CF) -> SF^OF, "l"),
                  leq_signed        = mk((ZF,SF,OF,CF) -> (SF^OF)|ZF, "le"),
                  above_unsigned    = mk((ZF,SF,OF,CF) -> !CF & !ZF, "a"),
                  above_eq_unsigned = mk((ZF,SF,OF,CF) -> !CF, "ae"),
                  below_unsigned    = mk((ZF,SF,OF,CF) -> CF, "b"),
                  below_eq_unsigned = mk((ZF,SF,OF,CF) -> CF | ZF, "be");

    ConditionCode[] codes = new ConditionCode[]{ always, equal, not_equal, negative, nonnegative,
                                                 greater_signed, geq_signed, less_signed, leq_signed,
                                                 above_unsigned, above_eq_unsigned, below_unsigned, below_eq_unsigned };

}
