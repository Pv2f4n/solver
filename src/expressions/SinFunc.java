package expressions;

/**
 * An expression node tree representing the sine function.
 */
public final class SinFunc extends Application {

    // Constructor for SinFunc
    public SinFunc(Expression argument) {
        super(UnaryFunction.SIN, argument);
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
            return new Constant(new SinFunc(optArg).eval(MapVarTable.empty()));
        } catch (UnboundVariableException e) {
            return new SinFunc(optArg);
        }
    }

    /**
     * Returns the derivative of this sine function with respect to the variable with name
     * `varName`, which will be the derivative of the argument multiplied by the cosine of the
     * argument as given by the chain rule.
     */
    @Override
    public Expression differentiate(String varName) {
        return new MultOperation(new CosFunc(argument), argument.differentiate(varName));
    }
}
