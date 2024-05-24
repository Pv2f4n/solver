package expressions;

import java.util.Set;

/**
 * An expression tree node representing a function
 */
public class Application implements Expression {
    // Function that takes one argument
    private UnaryFunction func;
    // Argument for the function
    private Expression argument;

    // Constructor for application
    public Application(UnaryFunction func, Expression arg) {
        assert func != null && arg != null;
        this.func = func;
        this.argument = arg;
    }

    /**
     * Returns the result of applying the function to the evaluation of the argument. Throws
     * UnboundVariableException if the expression argument contains a variable not in vars.
     */
    @java.lang.Override
    public double eval(VarTable vars) throws UnboundVariableException {
        assert vars != null;
        return func.apply(argument.eval(vars));
    }

    /**
     * Returns one plus the number of operations taken to evaluate the argument.
     */
    @java.lang.Override
    public int opCount() {
        return argument.opCount() + 1;
    }

    /**
     * Returns the infix string representation of the Application, which is made up of the name of
     * the function followed by the infix representation of the argument in parentheses.
     */
    @java.lang.Override
    public String infixString() {
        return func.name() + "(" + argument.infixString() + ")";
    }

    /**
     * Returns the postfix string representation of the Application, which is made up of the
     * postfix representation of the argument, then the name of the function with parentheses.
     */
    @java.lang.Override
    public String postfixString() {
        return argument.postfixString() + " " + func.name() + "()";
    }

    /**
     * If the argument can be optimized to a constant, return a constant that is the evaluation of
     * the argument. Otherwise, return a partially optimized copy of self where the argument is in
     * optimized form.
     */
    @java.lang.Override
    public Expression optimize(VarTable vars) {
        assert vars != null;
        Expression optArg = argument.optimize(vars);
        try {
            return new Constant(new Application(func, optArg).eval(MapVarTable.empty()));
        } catch (UnboundVariableException e) {
            return new Application(func, optArg);
        }
    }

    /**
     * Returns dependencies of argument
     */
    @java.lang.Override
    public Set<String> dependencies() {
        return argument.dependencies();
    }

    /**
     * Returns whether this Application and other Application are equal. Two Applications are equal
     * if their functions and arguments are equal.
     */
    public boolean equals(Object other) {
        if(!(other instanceof Application)) {
            return false;
        }

        Application otherApp = (Application) other;
        // Check to make sure functions are the same (same name) and arguments are equal
        return func.name().equals(otherApp.func.name()) && argument.equals(otherApp.argument);
    }
}
