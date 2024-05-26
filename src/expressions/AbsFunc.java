package expressions;

/**
 * An expression node tree representing the absolute value function.
 */
public final class AbsFunc extends Application {

    // Constructor for AbsFunc
    public AbsFunc(Expression argument) {
        super(UnaryFunction.ABS, argument);
    }

    /**
     * If the argument can be optimized to a constant, return a constant that is the evaluation of
     * the argument. Otherwise, return a partially optimized copy of self where the argument is in
     * optimized form.
     */
    @Override
    public Expression optimize(VarTable vars) {
        assert vars != null;
        Expression optArg = argument.optimize(vars);
        try {
            return new Constant(new AbsFunc(optArg).eval(MapVarTable.empty()));
        } catch (UnboundVariableException e) {
            return new AbsFunc(optArg);
        }
    }

    /**
     * Derivative of absolute value is not yet implemented in this version.
     */
    @Override
    public Expression differentiate(String varName) {
        return new Constant(1.0);
    }
}
