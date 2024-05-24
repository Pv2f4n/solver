package expressions;

import java.util.HashSet;
import java.util.Set;

/**
 * An expression tree node representing a binary operation
 */
public class Operation implements Expression {
    // Operator used in operation
    private Operator op;
    // Left and right side expressions
    private Expression leftOperand;
    private Expression rightOperand;

    // Constructor for Operation
    public Operation(Operator op, Expression left, Expression right) {
        assert op != null && left != null && right != null;
        this.op = op;
        leftOperand = left;
        rightOperand = right;
    }

    /**
     * Returns the evaluation of the operator op applied to the evaluations of leftOperand and
     * rightOperand. Throws UnboundVariableException if a variable in leftOperand or rightOperand
     * is not in vars.
     */
    @java.lang.Override
    public double eval(VarTable vars) throws UnboundVariableException {
        assert vars != null;
        return op.operate(leftOperand.eval(vars), rightOperand.eval(vars));
    }

    /**
     * Returns one plus the number of operations required to evaluate leftOperand and rightOperand.
     */
    @java.lang.Override
    public int opCount() {
        return leftOperand.opCount() + rightOperand.opCount() + 1;
    }

    /**
     * Returns the infix string representation of the Operation, which is made up of the infix
     * string representation of the leftOperand, the operation symbol, and the representation of
     * the rightOperand separated by spaces and all enclosed in parentheses.
     */
    @java.lang.Override
    public String infixString() {
        return "(" + leftOperand.infixString() + " " + op.symbol() + " " + rightOperand.infixString()
                + ")";
    }

    /**
     * Returns the postfix string representation of the Operation, which is made of the postfix
     * string representation of the leftOperand, that of the rightOperand, and finally the symbol
     * of the operator separated by spaces.
     */
    @java.lang.Override
    public String postfixString() {
        return leftOperand.postfixString() + " " + rightOperand.postfixString() + " " + op.symbol();
    }

    /**
     * If both operands can be optimized to a constant, return a constant that is the evaluation of
     * the expression. Otherwise, return a partially optimized copy of self where operands are in
     * optimized forms.
     */
    @java.lang.Override
    public Expression optimize(VarTable vars) {
        assert vars != null;
        Expression optLeft = leftOperand.optimize(vars);
        Expression optRight = rightOperand.optimize(vars);
        try {
            return new Constant(new Operation(op, optLeft, optRight).eval(MapVarTable.empty()));
        } catch (UnboundVariableException e) {
            return new Operation(op, optLeft, optRight);
        }
    }

    /**
     * Returns a set containing the strings of all variable names in the expressions of both
     * operands.
     */
    @java.lang.Override
    public Set<String> dependencies() {
        Set<String> newSet = leftOperand.dependencies();
        newSet.addAll(rightOperand.dependencies());
        return newSet;
    }

    /**
     * Returns whether this Operation is equal to other Operation. Two Operations are equal if their
     * operands and operations are equal.
     */
    public boolean equals(Object other) {
        if (!(other instanceof Operation)) {
            return false;
        }

        Operation otherOp = (Operation) other;
        return leftOperand.equals(otherOp.leftOperand) && rightOperand.equals(otherOp.rightOperand)
                && op.symbol().equals(otherOp.op.symbol());
    }
}
