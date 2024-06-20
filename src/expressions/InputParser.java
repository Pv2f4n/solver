package expressions;

import expressions.functions.*;
import expressions.operations.*;
import expressions.exceptions.*;
import java.util.ArrayList;
import java.util.Arrays;
import solving.*;

import java.util.Map;
import java.util.HashMap;

/**
 * Class for parsing input strings as expression trees.
 */
public class InputParser {

    /**
     * Associates each binary operation with its precedence (1 being the lowest, 3 the highest)
     * and its associativity (0 for left, 1 for right).
     */
    private static final Map<String, int[]> opMap;

    // Static constructor to initialize opMap and isEven
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
     * Helper function for compute_expr. Deals with single numbers and variables (including
     * negated terms), as well as parenthesized expressions, including function arguments. Throws
     * IncompleteExpressionException if the input string's mathematical expression is invalid, e.g.
     * operators missing operands or unmatched parentheses.
     */
    private static Expression compute_atom(Tokenizer tokenizer) throws
            IncompleteExpressionException {
        Token tok = tokenizer.curTok();

        if (tok == null) {
            throw new IncompleteExpressionException("expression ended unexpectedly");
        } else if (tok instanceof Token.Operator) {
            // Case of subtraction operator acting as negative sign
            if (tok.value().equals("-")) {
                // Check that there is something to the right of the negative sign
                if (!(tokenizer.hasNext())) {
                    throw new IncompleteExpressionException("hanging negative sign");
                }
                tokenizer.next();
                // Compute next atom without compute_expr and then add the negative sign in front
                // of it
                return new MultOperation(new Constant(-1.0), compute_atom(tokenizer));
            } else {
                throw new IncompleteExpressionException("expected number, variable, parentheses, "
                        + "or negative sign, not other operator");
            }
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
            // Break if tok is null (end of expression) or a right parenthesis (finished evaluating
            // parenthesized expression)
            if (tok == null || tok instanceof Token.RightParen) {
                break;
            } else if (tok instanceof Token.Number) {
                throw new IncompleteExpressionException("Number cannot be placed directly to the "
                        + "right of an atom without a connecting operator");
            }
            // Treat lack of operator as multiplication between left and right term when right
            // term is a parenthesized expression, variable, or function
            else if (tok instanceof Token.LeftParen || tok instanceof Token.Variable ||
            tok instanceof Token.Function) {
                // Stop evaluation and return if precedence of multiplication is less than minimum
                // precedence
                if (2 < min_prec) {break;}
                // Otherwise treat as if there is an invisible multiplication operator there
                Expression right = compute_expr(tokenizer, 3);
                left = new MultOperation(left, right);
            } else {
                assert tok instanceof Token.Operator;
                // Stop evaluation and return if precedence of next operator is less than minimum
                // precedence
                if (opMap.get(tok.value())[0] < min_prec) {
                    break;
                }
                String operator = tok.value();
                int prec = opMap.get(tok.value())[0];
                int assoc = opMap.get(tok.value())[1];
                // Precedence climbing step, depends on associativity of operator
                int nextMinPrec = (assoc == 0) ? prec + 1 : prec;
                if (!(tokenizer.hasNext())) {
                    throw new IncompleteExpressionException(
                            "operation does not have right operand");
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
        }
        return left;
    }
    public static void main(String[] args) {
        // Basic tests
        double[][] mat = {{1, 2, 1, -4, 1}, {1, 2, -1, 2, -1}, {2, 4, 1, -5, 1}, {1, 2, 3, -10, 2}};
        double[] vec = {0, 0, 0, 0};
        try {
            ArrayList<String> ans2 = SystemSolver.linear_solve_general(mat, vec);
            for (String equation : ans2) {
                System.out.println(equation);
            }
        } catch (SolvingException e) {
            throw new RuntimeException(e);
        }
        try {
            Expression[] expressions = {parse("(3x^2-3)/(1+y^2)-2xz+2z"),
                    parse("2yz+((2y)(x^3-3x))/(1+y^2)^2"), parse("(x-1)^2+y^2-9")};
            String[] vars = {"x", "y", "z"};
            double[] start = {-1.8,0.2,-1};
            double[] output = SystemSolver.nonlinear_solve(expressions, vars, start);
            for (int i = 0; i < 3; i++) {
                System.out.println(output[i]);
            }
        } catch (UnreadableCharacterException | IncompleteExpressionException | SolvingException e) {
            throw new RuntimeException(e);
        }
    }
}
