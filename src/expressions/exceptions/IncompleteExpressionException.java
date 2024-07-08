package expressions.exceptions;

/**
 * Indicates that an expression string could not be parsed to yield an expression.
 */
public class IncompleteExpressionException extends Exception {
    /**
     * Create an IncompleteExpressionException.
     */
    public IncompleteExpressionException(String errorMessage) {
        super(errorMessage);
    }
}

