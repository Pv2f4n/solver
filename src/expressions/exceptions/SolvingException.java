package expressions.exceptions;

/**
 * Indicates that the system of equations could not be solved.
 */
public class SolvingException extends Exception {
    /**
     * Create a SolvingException indicating that a system of equations could not be solved.
     */
    public SolvingException(String errorMessage) {
        super(errorMessage);
    }
}
