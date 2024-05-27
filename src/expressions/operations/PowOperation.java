package expressions.operations;
import expressions.*;
import expressions.functions.*;

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
        if (rightOperand instanceof Constant) {
            Expression firstPart = new MultOperation(rightOperand,
                    new PowOperation(leftOperand,
                            new Constant(((Constant) rightOperand).value() - 1.0)));
            return new MultOperation(firstPart, leftOperand.differentiate(varName));
        } else {
            Expression firstPart = new MultOperation(rightOperand.differentiate(varName),
                    new LogFunc(leftOperand));
            Expression secondPart = new MultOperation(rightOperand, new DivOperation(
                    leftOperand.differentiate(varName), leftOperand));
            Expression thirdPart = new AddOperation(firstPart, secondPart);
            return new MultOperation(this, thirdPart);
        }
    }

    /**
     * Simplifies both operands and returns the simplified version of this power operation, which is
     * a Constant if both simplified operands are Constants, the simplified leftOperand if the
     * simplified rightOperand is the Constant 1.0, the Constant 1.0 if the simplified rightOperand
     * is the zero Constant, and this PowOperation with simplified operands otherwise.
     */
    @Override
    public Expression simplify() {
        Expression newLeft = leftOperand.simplify();
        Expression newRight = rightOperand.simplify();
        Expression newOp = new PowOperation(newLeft, newRight);
        if (newLeft instanceof Constant && newRight instanceof Constant) {
            return new Constant(Math.pow(((Constant)newLeft).value(), ((Constant)newRight).value()));
        } else if (newRight instanceof Constant) {
            if (((Constant)newRight).value() == 1.0) {
                return newLeft;
            } else if (((Constant)newRight).value() == 0.0) {
                return new Constant(1.0);
            } else {
                return newOp;
            }
        } else {
            return newOp;
        }
    }
}
