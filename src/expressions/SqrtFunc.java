package expressions;

/**
 * An expression node tree representing the square root function.
 */
public final class SqrtFunc extends Application {

    // Constructor for SqrtFunc
    public SqrtFunc(Expression argument) {
        super(UnaryFunction.SQRT, argument);
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
            return new Constant(new SqrtFunc(optArg).eval(MapVarTable.empty()));
        } catch (UnboundVariableException e) {
            return new SqrtFunc(optArg);
        }
    }

    /**
     * Returns the derivative of this square root function with respect to the variable with name
     * `varName` as given by the power rule and chain rule.
     */
    @Override
    public Expression differentiate(String varName) {
        Expression firstPart = new MultOperation(new Constant(0.5),
                new PowOperation(argument, new Constant(-0.5)));
        return new MultOperation(firstPart, argument.differentiate(varName));
    }
}
