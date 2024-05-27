package expressions;

import java.util.HashSet;
import java.util.Set;

/**
 * An expression tree node representing a variable
 */
public final class Variable implements Expression {
    // Name of the variable
    private String name;

    // Constructor for variable
    public Variable(String name) {
        assert name != null;
        this.name = name;
    }

    /**
     * Returns the name of this variable.
     */
    public String name() {
        return name;
    }

    /**
     * Evaluates to value of variable with name `name` in vars. Throws UnboundVariableException if
     * corresponding variable does not exist in vars.
     */
    @Override
    public double eval(VarTable vars) throws UnboundVariableException {
        assert vars != null;
        // If vars doesn't contain `name` throw that we don't have this variable
        if(!vars.contains(name)) throw new UnboundVariableException(name);
        return vars.get(name);
    }

    /**
     * Returns 0, since no operations are done to evaluate the value of a variable.
     */
    @Override
    public int opCount() {
        return 0;
    }

    /**
     * Returns the infix string representation of the Variable, which contains the variable's name.
     */
    @Override
    public String infixString() {
        return name;
    }

    /**
     * Returns the postfix string representation of the Variable, which contains the variable's name.
     */
    @Override
    public String postfixString() {
        return name;
    }

    /**
     * Returns self if variable doesn't exist in `vars`, otherwise return value of self as constant
     */
    @Override
    public Expression optimize(VarTable vars) {
        assert vars != null;
        try {
            return new Constant(eval(vars));
        } catch (UnboundVariableException e) {
            return this;
        }
    }

    /**
     * Returns a String corresponding to variable's own name in a set.
     */
    @Override
    public Set<String> dependencies() {
        HashSet<String> newSet = new HashSet<>();
        newSet.add(name);
        return newSet;
    }

    /**
     * Returns whether this Variable and other Variable are equal. Two Variables are equal if they
     * have the same name.
     */
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Variable)) {
            return false;
        }

        Variable otherVar = (Variable) other;
        return name.equals(otherVar.name);
    }

    /**
     * Returns the derivative of this variable with respect to the variable with name `varName`.
     * Will be the constant 1 if the variables have the same name and 0 otherwise.
     */
    @Override
    public Expression differentiate(String varName) {
        return name.equals(varName) ? new Constant(1.0) : new Constant(0.0);
    }

    /**
     * Returns the simplified version of this variable, which is itself.
     */
    @Override
    public Expression simplify() {
        return this;
    }
}
