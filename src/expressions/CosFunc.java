package expressions;

/**
 * An expression tree node representing the cosine function.
 */
public final class CosFunc extends Application {

    // Constructor for CosFunc
    public CosFunc(Expression argument) {
        super(UnaryFunction.COS, argument);
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
            return new Constant(new CosFunc(optArg).eval(MapVarTable.empty()));
        } catch (UnboundVariableException e) {
            return new CosFunc(optArg);
        }
    }

    /**
     * Returns the derivative of this cosine function with respect to the variable with name
     * `varName`, which will be the negation of the derivative of the argument multiplied by the
     * sine of the argument as given by the chain rule.
     */
    @Override
    public Expression differentiate(String varName) {
        Expression firstPart = new MultOperation(new Constant(-1.0), new SinFunc(argument));
        return new MultOperation(firstPart, argument.differentiate(varName));
    }
}
