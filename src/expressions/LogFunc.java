package expressions;

/**
 * An expression tree node representing the natural logarithm function.
 */
public final class LogFunc extends Application {

    // Constructor for LogFunc
    public LogFunc(Expression argument) {
        super(UnaryFunction.LOG, argument);
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
            return new Constant(new LogFunc(optArg).eval(MapVarTable.empty()));
        } catch (UnboundVariableException e) {
            return new LogFunc(optArg);
        }
    }

    /**
     * Returns the derivative of this log function with respect to the variable with name
     * `varName`, which will be the derivative of the argument divided by the argument.
     */
    @Override
    public Expression differentiate(String varName) {
        return new DivOperation(argument.differentiate(varName), argument);
    }
}
