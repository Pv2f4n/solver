package expressions.operations;

import expressions.*;
import expressions.exceptions.*;

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

    /**
     * Simplifies both operands and returns the simplified version of this subtraction operation,
     * which will be a Constant if both simplified operands are Constants, the simplified
     * left/rightOperand if the simplified right/leftOperand is the zero Constant, the zero
     * Constant if both simplified operands are the same Variable, and this SubOperation with
     * simplified operands otherwise.
     */
    @Override
    public Expression simplify() {
        Expression newLeft = leftOperand.simplify();
        Expression newRight = rightOperand.simplify();
        Expression newOp = new SubOperation(newLeft, newRight);
        if (newLeft instanceof Constant && newRight instanceof Constant) {
            return new Constant(
                    ((Constant) newLeft).value() - ((Constant) newRight).value());
        } else if (newLeft instanceof Constant && ((Constant)newLeft).value() == 0.0) {
            return new MultOperation(new Constant(-1.0), newRight);
        } else if (newRight instanceof Constant && ((Constant)newRight).value() == 0.0) {
            return newLeft;
        } else if (newLeft instanceof Variable && newRight instanceof Variable &&
                ((Variable)newLeft).name().equals(((Variable)newRight).name())) {
            return new Constant(0.0);
        } else {
            return newOp;
        }
    }
}
