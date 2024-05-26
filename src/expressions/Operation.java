package expressions;

import java.util.Set;


/**
 * An expression tree node representing a binary operation. differentiate() and optimize() must
 * be implemented in concrete extending subclasses depending on the specific operation. Subclasses
 * have protected access to all fields.
 */
public abstract sealed class Operation implements Expression permits AddOperation, MultOperation,
SubOperation, DivOperation, PowOperation {
    // Operator used in operation
    protected Operator op;
    // Left and right side expressions
    protected Expression leftOperand;
    protected Expression rightOperand;

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
    @Override
    public double eval(VarTable vars) throws UnboundVariableException {
        assert vars != null;
        return op.operate(leftOperand.eval(vars), rightOperand.eval(vars));
    }

    /**
     * Returns one plus the number of operations required to evaluate leftOperand and rightOperand.
     */
    @Override
    public int opCount() {
        return leftOperand.opCount() + rightOperand.opCount() + 1;
    }

    /**
     * Returns the infix string representation of the Operation, which is made up of the infix
     * string representation of the leftOperand, the operation symbol, and the representation of
     * the rightOperand separated by spaces and all enclosed in parentheses.
     */
    @Override
    public String infixString() {
        return "(" + leftOperand.infixString() + " " + op.symbol() + " " + rightOperand.infixString()
                + ")";
    }

    /**
     * Returns the postfix string representation of the Operation, which is made of the postfix
     * string representation of the leftOperand, that of the rightOperand, and finally the symbol
     * of the operator separated by spaces.
     */
    @Override
    public String postfixString() {
        return leftOperand.postfixString() + " " + rightOperand.postfixString() + " " + op.symbol();
    }

    /**
     * Returns a set containing the strings of all variable names in the expressions of both
     * operands.
     */
    @Override
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


