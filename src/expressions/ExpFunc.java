package expressions;

/**
 * An expression tree node representing the exponential (e^) function.
 */
public final class ExpFunc extends Application {

    // Constructor for ExpFunc
    public ExpFunc(Expression argument) {
        super(UnaryFunction.EXP, argument);
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
            return new Constant(new ExpFunc(optArg).eval(MapVarTable.empty()));
        } catch (UnboundVariableException e) {
            return new ExpFunc(optArg);
        }
    }

    /**
     * Returns the derivative of this exponential function with respect to the variable with name
     * `varName`, which will be the derivative of the argument multiplied by itself by the
     * derivative of exp and chain rule.
     */
    @Override
    public Expression differentiate(String varName) {
        return new MultOperation(this, argument.differentiate(varName));
    }
}