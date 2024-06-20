package expressions.operations;

import expressions.*;
import expressions.exceptions.*;

/**
 * An expression tree node representing the division operation.
 */
public final class DivOperation extends Operation {

    // Constructor for DivOperation
    public DivOperation(Expression left, Expression right) {
        super(Operator.DIVIDE, left, right);
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
            return new Constant(new DivOperation(optLeft, optRight).eval(MapVarTable.empty()));
        } catch (UnboundVariableException e) {
            return new DivOperation(optLeft, optRight);
        }
    }

    /**
     * Return an expression representing the derivative of this division operation with respect to
     * the variable with name `varName`, which is given by the quotient rule.
     */
    @Override
    public Expression differentiate(String varName) {
        Expression num1 = new MultOperation(leftOperand.differentiate(varName), rightOperand);
        Expression num2 = new MultOperation(rightOperand.differentiate(varName), leftOperand);
        Expression den = new PowOperation(rightOperand, new Constant(2.0));
        return new DivOperation(new SubOperation(num1, num2), den);
    }

    /**
     * Simplifies both operands and returns the simplified version of this division operation,
     * which will be a Constant if both simplified operands are Constants, the zero Constant if
     * the simplified leftOperand is the zero Constant, the Constant 1.0 if both simplified operands
     * are the same Variable, and this DivOperation with simplified operands otherwise.
     */
    @Override
    public Expression simplify() {
        Expression newLeft = leftOperand.simplify();
        Expression newRight = rightOperand.simplify();
        Expression newOp = new DivOperation(newLeft, newRight);
        return switch (newLeft) {
            case Constant constant when newRight instanceof Constant ->
                    new Constant(constant.value() / ((Constant) newRight).value());
            case Constant constant when ((Constant) newLeft).value() == 0.0 ->
                    new Constant(0.0);
            case Variable variable when newRight instanceof Variable
                    && ((Variable) newLeft).name().equals(((Variable) newRight).name()) ->
                    new Constant(1.0);
            case null, default -> newOp;
        };
    }
}