package expressions;

/**
 * An expression tree node representing the multiplication operation.
 */
public final class MultOperation extends Operation {

    // Constructor for MultOperation
    public MultOperation(Expression left, Expression right) {
        super(Operator.MULTIPLY, left, right);
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
            return new Constant(new MultOperation(optLeft, optRight).eval(MapVarTable.empty()));
        } catch (UnboundVariableException e) {
            return new MultOperation(optLeft, optRight);
        }
    }

    /**
     * Return an expression representing the derivative of this multiplication operation with
     * respect to the variable with name `varName` as given by the product rule.
     */
    @Override
    public Expression differentiate(String varName) {
        Expression expr1 = new MultOperation(leftOperand.differentiate(varName), rightOperand);
        Expression expr2 = new MultOperation(rightOperand.differentiate(varName), leftOperand);
        return new AddOperation(expr1, expr2);
    }
}
