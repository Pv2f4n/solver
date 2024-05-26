package expressions;

/**
 * An expression tree node representing the add operation.
 */
public final class AddOperation extends Operation {

    // Constructor for AddOperation
    public AddOperation(Expression left, Expression right) {
        super(Operator.ADD, left, right);
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
            return new Constant(new AddOperation(optLeft, optRight).eval(MapVarTable.empty()));
        } catch (UnboundVariableException e) {
            return new AddOperation(optLeft, optRight);
        }
    }

    /**
     * Return the derivative of this add operation with respect to the variable with name `varName`,
     * which will be the sum of the derivatives of leftOperand and rightOperand.
     */
    @Override
    public Expression differentiate(String varName) {
        return new AddOperation(leftOperand.differentiate(varName),
                rightOperand.differentiate(varName));
    }
}
