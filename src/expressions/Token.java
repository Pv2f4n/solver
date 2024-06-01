package expressions;
import expressions.functions.*;
import expressions.operations.*;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Represents a single token (e.g., a number, variable name, operator symbol, function name, or
 * left/right parentheses) within an expression string.
 */
public class Token {

    /**
     * The substring corresponding to the token.
     */
    protected final String value;

    /**
     * Initialize inherited value field to `value`.
     */
    protected Token(String value) {
        this.value = value;
    }

    /**
     * Return the substring that this token corresponds to.
     */
    public String value() {
        return value;
    }

    /**
     * Return the sequence of tokens contained in `str`.
     */
    public static Iterable<Token> tokenizer(String str) throws UnreadableCharacterException {
        // First check to make sure string only contains alphanumeric characters and operators
        // or parentheses - can't do this in the iterable itself
        for (int i = 0; i < str.length(); i++) {
            if (!(Character.isLetterOrDigit(str.charAt(i)) || str.substring(i, i + 1).matches(
                    "[-+*/^()]"))) {throw new UnreadableCharacterException(str.charAt(i));}
        }
        return () -> new Iterator<>() {
            int index = 0;

            public boolean hasNext() {
                return index < str.length();
            }

            public Token next() {
                if (Number.validNumber(str.substring(index, index + 1))) {
                    int endIdx = index + 1;
                    while (endIdx < str.length() &&
                            Number.validNumber(str.substring(index, endIdx + 1))) {
                        endIdx++;
                    }
                    int oldIdx = index;
                    index = endIdx;
                    return new Number(str.substring(oldIdx, endIdx));
                } else if (Operator.validOperator(str.substring(index, index + 1))) {
                    int oldIdx = index;
                    index++;
                    return new Operator(str.substring(oldIdx, oldIdx + 1));
                } else if (str.charAt(index) == '(') {
                    index++;
                    return new LeftParen();
                } else if (str.charAt(index) == ')') {
                    index++;
                    return new RightParen();
                } else if (str.substring(index).matches("(abs|exp|log|sin|cos|tan).*")) {
                    int oldIdx = index;
                    index += 3;
                    return new Function(str.substring(oldIdx, oldIdx + 3));
                } else if (str.substring(index).matches("(sqrt).*")) {
                    int oldIdx = index;
                    index +=4;
                    return new Function(str.substring(oldIdx, oldIdx + 4));
                } else {
                    // Otherwise must be a letter
                    int oldIdx = index;
                    index += 1;
                    return new Variable(str.substring(oldIdx, oldIdx + 1));
                }
            }
        };
    }

    /**
     * A token representing the name of a variable.
     */
    public static class Variable extends Token {

        /**
         * Construct a Variable token whose name is `value`.
         */
        private Variable(String value) {
            super(value);
        }
    }

    /**
     * A token representing a function call.
     */
    public static class Function extends Token {

        /**
         * Construct a Function token whose name is `value`.
         */
        private Function(String value) {
            super(value);
        }
    }

    /**
     * A token representing a floating-point number.
     */
    public static class Number extends Token {

        /**
         * Construct a new Number token whose value is represented by `value`.  Requires `value` is
         * a valid representation of a floating-point number (as determined by `validNumber()`).
         */
        private Number(String value) {
            super(value);
            assert validNumber(value);
        }

        /**
         * Return the numeric value represented by this token, in double precision.
         */
        public double doubleValue() {
            return Double.parseDouble(value);
        }

        /**
         * Return whether `value` represents  a valid floating-point number (as determined by Java's
         * `Double.valueOf()`).
         */
        public static boolean validNumber(String value) {
            try {
                Double.parseDouble(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    /**
     * A token representing a binary arithmetic operator.
     */
    public static class Operator extends Token {

        /**
         * Construct an Operator token whose symbol is `value`.  Requires `value` is a valid
         * operator symbol (as determined by `validOperator()`).
         */
        private Operator(String value) {
            super(value);
            assert validOperator(value);
        }

        /**
         * Return the operator represented by this token.
         */
        public expressions.Operator opValue() {
            return expressions.Operator.fromString(value);
        }

        /**
         * Return whether `value` represents a binary arithmetic operator recognized by the
         * `Operator` class.
         */
        public static boolean validOperator(String value) {
            return expressions.Operator.isOperator(value);
        }
    }

    /**
     * A token representing a left parenthesis.
     */
    public static class LeftParen extends Token {

        /**
         * Construct a LeftParen token.
         */
        private LeftParen() {
            super("(");
        }
    }

    /**
     * A token representing a right parenthesis.
     */
    public static class RightParen extends Token {

        /**
         * Construct a LeftParen token.
         */
        private RightParen() {
            super(")");
        }
    }

}
