package oram.vm;


public interface ConditionCode {

    ConditionCode always = (ZF,SF,OF,CF) -> true;

    boolean is(boolean ZF, boolean SF, boolean OF, boolean CF);

    ConditionCode equal             = (ZF,SF,OF,CF) -> ZF,
                  not_equal         = (ZF,SF,OF,CF) -> !ZF,
                  negative          = (ZF,SF,OF,CF) -> SF,
                  nonnegative       = (ZF,SF,OF,CF) -> !SF,
                  greater_signed    = (ZF,SF,OF,CF) -> !(SF^OF)&!ZF,
                  geq_signed        = (ZF,SF,OF,CF) -> !(SF^OF),
                  less_signed       = (ZF,SF,OF,CF) -> SF^OF,
                  leq_signed        = (ZF,SF,OF,CF) -> (SF^OF)|ZF,
                  above_unsigned    = (ZF,SF,OF,CF) -> !CF & !ZF,
                  above_eq_unsigned = (ZF,SF,OF,CF) -> !CF,
                  below_unsigned    = (ZF,SF,OF,CF) -> CF,
                  below_eq_unsigned = (ZF,SF,OF,CF) -> CF | ZF;


}
