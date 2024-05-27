package expressions.operations;
import expressions.*;

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

    /**
     * Simplifies both operands and returns the simplified version of this multiplication operation,
     * which will be a Constant if both simplified operands are Constants, the zero Constant if
     * either simplified operand is the zero Constant, the simplified left/rightOperand if the
     * simplified right/leftOperand is the Constant 1.0, and this MultOperation with simplified
     * operands otherwise.
     */
    @Override
    public Expression simplify() {
        Expression newLeft = leftOperand.simplify();
        Expression newRight = rightOperand.simplify();
        Expression newOp = new MultOperation(newLeft, newRight);
        if (newLeft instanceof Constant && newRight instanceof Constant) {
            return new Constant(((Constant)newLeft).value() * ((Constant)newRight).value());
        } else if (newLeft instanceof Constant) {
            if (((Constant)newLeft).value() == 0.0) {
                return new Constant(0.0);
            } else if  (((Constant)newLeft).value() == 1.0) {
                return newRight;
            } else {return newOp;}
        } else if (newRight instanceof Constant) {
            if (((Constant)newRight).value() == 0.0) {
                return new Constant(0.0);
            } else if  (((Constant)newRight).value() == 1.0) {
                return newLeft;
            } else {return newOp;}
        } else {
            return newOp;
        }
    }
}
