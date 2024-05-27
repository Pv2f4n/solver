package expressions;
import expressions.functions.*;
import expressions.operations.*;
import java.util.Map;

public class InputParser {
    public static Expression inputParse(String exprString, Map<String, UnaryFunction> funcDefs)
        throws UndefinedFunctionException {
        throw new UnsupportedOperationException();
    }
    public static void main(String[] args) {
        // Testing some basic stuff
        Expression testExpression = new DivOperation(new SubOperation(new PowOperation(
                new Variable ("x"), new Constant(3.0)), new MultOperation(new Constant(3.0),
                        new Variable("x"))), new AddOperation(new Constant(1.0), new PowOperation(
                        new Variable("y"), new Constant(2.0))));
        Expression newExpression = testExpression.differentiate("x").simplify();
        Expression newExpression2 = testExpression.differentiate("x").differentiate("x").simplify();
        String out = newExpression.infixString();
        String out2 = newExpression2.infixString();
        System.out.println("Simplified: " + out);
        System.out.println("Unsimplified: " + out2);
    }
}
