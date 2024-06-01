package expressions;

/**
 * Indicates that an expression string could not be parsed to yield an expression.
 */
public class IncompleteExpressionException extends Exception {
    /**
     * Create an IncompleteExpressionException indicating that `expression` could not be parsed.
     */
    public IncompleteExpressionException(String errorMessage) {
        super(errorMessage);
    }
}

