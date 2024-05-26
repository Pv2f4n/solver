package expressions;

import java.util.Set;

/**
 * An expression tree node representing a function. differentiate() and optimize() must
 *  * be implemented in concrete extending subclasses depending on the specific operation.
 *  Subclasses have protected access to all fields.
 */
public abstract sealed class Application implements Expression permits SqrtFunc, ExpFunc, LogFunc,
SinFunc, CosFunc, TanFunc, AbsFunc {
    // Function that takes one argument
    protected UnaryFunction func;
    // Argument for the function
    protected Expression argument;

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
    @Override
    public double eval(VarTable vars) throws UnboundVariableException {
        assert vars != null;
        return func.apply(argument.eval(vars));
    }

    /**
     * Returns one plus the number of operations taken to evaluate the argument.
     */
    @Override
    public int opCount() {
        return argument.opCount() + 1;
    }

    /**
     * Returns the infix string representation of the Application, which is made up of the name of
     * the function followed by the infix representation of the argument in parentheses.
     */
    @Override
    public String infixString() {
        return func.name() + "(" + argument.infixString() + ")";
    }

    /**
     * Returns the postfix string representation of the Application, which is made up of the
     * postfix representation of the argument, then the name of the function with parentheses.
     */
    @Override
    public String postfixString() {
        return argument.postfixString() + " " + func.name() + "()";
    }

    /**
     * Returns dependencies of argument
     */
    @Override
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
