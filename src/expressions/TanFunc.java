package expressions;

/**
 * An expression node tree representing the tangent function.
 */
public final class TanFunc extends Application {

    // Constructor for TanFunc
    public TanFunc(Expression argument) {
        super(UnaryFunction.TAN, argument);
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
            return new Constant(new TanFunc(optArg).eval(MapVarTable.empty()));
        } catch (UnboundVariableException e) {
            return new TanFunc(optArg);
        }
    }

    /**
     * Returns the derivative of this tangent function with respect to the variable with name
     * `varName`, which will be derivative of the argument multiplied by the secant squared of the
     * argument as given by the chain rule.
     */
    @Override
    public Expression differentiate(String varName) {
        Expression secondPart = new PowOperation(new CosFunc(argument), new Constant(2.0));
        return new DivOperation(argument.differentiate(varName), secondPart);
    }
}
