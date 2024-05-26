package expressions;

/**
 * An expression node tree representing the subtraction operation.
 */
public final class SubOperation extends Operation {

    // Constructor for SubOperation
    public SubOperation(Expression left, Expression right) {
        super(Operator.SUBTRACT, left, right);
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
            return new Constant(new SubOperation(optLeft, optRight).eval(MapVarTable.empty()));
        } catch (UnboundVariableException e) {
            return new SubOperation(optLeft, optRight);
        }
    }

    /**
     * Return an expression representing the derivative of this subtraction operation with respect
     * to the variable with name `varName`, which will be the difference of the derivatives of
     * leftOperand and rightOperand.
     */
    @Override
    public Expression differentiate(String varName) {
        return new SubOperation(leftOperand.differentiate(varName),
                rightOperand.differentiate(varName));
    }
}
