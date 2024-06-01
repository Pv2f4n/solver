package expressions;
import expressions.functions.*;
import expressions.operations.*;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.function.ToDoubleBiFunction;
import java.util.Map;
import java.util.HashMap;

/**
 * Class for parsing input strings as expression trees.
 */
public class InputParser {

    /**
     * Associates each binary operation with its precedence (1 being the lowest, 3 the highest) and
     * its associativity (0 for left, 1 for right).
     */
    public static final Map<String, int[]> opMap;
        static {
            opMap = new HashMap<>();
            opMap.put("+", new int[]{1, 0});
            opMap.put("-", new int[]{1, 0});
            opMap.put("*", new int[]{2, 0});
            opMap.put("/", new int[]{2, 0});
            opMap.put("^", new int[]{3, 1});
        }
    /**
     * Parses the given input string `exprString` into its corresponding expression tree. Throws
     * UnreadableCharacterException if exprString contains a character that cannot be parsed.
     * Throws IncompleteExpressionException if the input string's mathematical expressions are
     * invalid, e.g. operators missing operands or unmatched parentheses.
     */
    public static Expression parse(String exprString) throws UnreadableCharacterException,
            IncompleteExpressionException {
        Tokenizer tokenizer = new Tokenizer(Token.tokenizer(exprString));
        return compute_expr(tokenizer, 1);
    }

    /**
     * Helper function for compute_expr. Deals with single numbers, variables, and parenthesized
     * expressions, including function arguments. Throws IncompleteExpressionException if the
     * input string's mathematical expression is invalid, e.g. operators missing operands or
     * unmatched parentheses.
     */
    private static Expression compute_atom(Tokenizer tokenizer) throws
            IncompleteExpressionException {
        Token tok = tokenizer.curTok();
        // Input token should be left parenthesis, variable, or number
        if (tok == null) {
            throw new IncompleteExpressionException("expression ended unexpectedly");
        } else if (tok instanceof Operator) {
            throw new IncompleteExpressionException("expected atom, not an operator");
        } else if (tok instanceof Token.RightParen) {
            throw new IncompleteExpressionException("unmatched right parenthesis");
        } else if (tok instanceof Token.LeftParen) {
            // Ensure something exists to the right of the left parenthesis
            if (!(tokenizer.hasNext())) {
                throw new IncompleteExpressionException("unmatched left parenthesis");
            }
            // Deal with expression inside parentheses
            tokenizer.next();
            Expression value = compute_expr(tokenizer, 1);
            // Ensure a right parenthesis ends the expression
            if (!(tokenizer.curTok() instanceof Token.RightParen)) {
                throw new IncompleteExpressionException("unmatched left parenthesis");
            }
            return value;
        } else if (tok instanceof Token.Function) {
            // Function must be followed by parentheses and argument inside
            if (!tokenizer.hasNext()) {
                throw new IncompleteExpressionException("function must be followed immediately "
                        + "by argument enclosed in parentheses");
            }
            tokenizer.next();
            if (!(tokenizer.curTok() instanceof Token.LeftParen)) {
                throw new IncompleteExpressionException("function must be followed immediately "
                        + "by argument enclosed in parentheses");
            }
            if (!tokenizer.hasNext()) {
                throw new IncompleteExpressionException("function must be followed immediately "
                        + "by argument enclosed in parentheses");
            }
            // Deal with function argument first
            tokenizer.next();
            Expression argument = compute_expr(tokenizer, 1);
            // Check ends with right parenthesis
            if (!(tokenizer.curTok() instanceof Token.RightParen)) {
                throw new IncompleteExpressionException("unmatched left parenthesis");
            }
            return switch (tok.value()) {
                case "abs" -> new AbsFunc(argument);
                case "sqrt" -> new SqrtFunc(argument);
                case "exp" -> new ExpFunc(argument);
                case "log" -> new LogFunc(argument);
                case "sin" -> new SinFunc(argument);
                case "cos" -> new CosFunc(argument);
                case "tan" -> new TanFunc(argument);
                // This shouldn't happen, since the function tokens yielded by the tokenizer can
                // only have the above names
                default -> throw new IncompleteExpressionException("function name does not "
                        + "correspond to valid function");
            };
        } else if (tok instanceof Token.Variable) {
            return new Variable(tok.value());
        } else {
            assert tok instanceof Token.Number;
            return new Constant(Double.parseDouble(tok.value()));
        }
    }

    /**
     * Parses the input string as an expression using recursive descent and precedence climbing.
     * Throws IncompleteExpressionException if the input string's mathematical expression is
     * invalid, e.g. operators missing operands or unmatched parentheses.
     */
    private static Expression compute_expr(Tokenizer tokenizer, int min_prec)
            throws IncompleteExpressionException {
        Expression left = compute_atom(tokenizer);
        // compute_atom does not advance the iterator after computation, so it is done here below
        // instead; moving it to compute_atom would yield the same result but with more writing
        // as it would have to be written before every return statement in each branch
        tokenizer.next();
        while (true) {
            Token tok = tokenizer.curTok();
            // Non-null and not instanceof Operator should only occur at a right parenthesis
            // since for now different atom tokens cannot be adjacent to each other
            if (tok == null || !(tok instanceof Token.Operator) ||
                    opMap.get(tok.value())[0] < min_prec) {
                break;
            }
            String operator = tok.value();
            int prec = opMap.get(tok.value())[0];
            int assoc = opMap.get(tok.value())[1];
            // Precedence climbing step, depends on associativity of operator
            int nextMinPrec = (assoc == 0) ? prec + 1 : prec;
            if (!(tokenizer.hasNext())) {
                throw new IncompleteExpressionException("operation does not have right operand");
            }
            // Recursive step: move on to next token
            tokenizer.next();
            Expression right = compute_expr(tokenizer, nextMinPrec);
            left = switch (operator) {
                case "+" -> new AddOperation(left, right);
                case "-" -> new SubOperation(left, right);
                case "*" -> new MultOperation(left, right);
                case "/" -> new DivOperation(left, right);
                case "^" -> new PowOperation(left, right);
                // This shouldn't happen, since the operator tokens yielded by the tokenizer can
                // only have the above names
                default -> throw new IncompleteExpressionException("operator value does not "
                        + "correspond to valid operator");
            };
        }
        return left;
    }
    public static void main(String[] args) {
        // Basic tests
        try {
            Expression test = parse("5*4+7^2- sqrt(36)");
            System.out.println(test.eval(MapVarTable.empty()));
        } catch (UnreadableCharacterException | UnboundVariableException |
                 IncompleteExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}
