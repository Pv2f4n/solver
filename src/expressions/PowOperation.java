package expressions;

/**
 * An expression tree node representing the power operation.
 */
public final class PowOperation extends Operation {

    // Constructor for PowOperation
    public PowOperation(Expression left, Expression right) {
        super(Operator.POW, left, right);
    }

    /**
     * If both operands can be optimized to a constant, return a constant that is the evaluation of
     * the expression. Otherwise, return a partially optimized copy of self where operands are in
     * optimized forms.
     */
    @Override
    public Expression optimize(VarTable vars) {
        assert vars != null;
        Expression optLeft = leftOperand.optimize(vars);
        Expression optRight = rightOperand.optimize(vars);
        try {
            return new Constant(new PowOperation(optLeft, optRight).eval(MapVarTable.empty()));
        } catch (UnboundVariableException e) {
            return new PowOperation(optLeft, optRight);
        }
    }

    /**
     * Return an expression representing the derivative of this operation with respect to the
     * variable with name `varName`, as given by the formula for the derivative of an expression
     * raised to another expression.
     */
    @Override
    public Expression differentiate(String varName) {
        Expression firstPart = new MultOperation(rightOperand.differentiate(varName),
                new LogFunc(leftOperand));
        Expression secondPart = new MultOperation(rightOperand, new DivOperation(
                leftOperand.differentiate(varName), leftOperand));
        Expression thirdPart = new AddOperation(firstPart, secondPart);
        return new MultOperation(this, thirdPart);
    }
}
