package expressions;

import expressions.functions.*;
import expressions.operations.*;
import expressions.exceptions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParsingTest {

    @Test
    @DisplayName("Single variables and constants should parse to their respective nodes, "
            + "regardless of whitespace")
    void testSingleVarConst() throws UnreadableCharacterException,
            IncompleteExpressionException{
        Expression var1 = InputParser.parse("x");
        assertEquals(var1, new Variable("x"));

        Expression var2 = InputParser.parse("y  ");
        assertEquals(var2, new Variable("y"));

        Expression var3 = InputParser.parse(" z ");
        assertEquals(var3, new Variable("z"));

        Expression const1 = InputParser.parse("5");
        assertEquals(const1, new Constant(5.0));

        Expression const2 = InputParser.parse("0.3674");
        assertEquals(const2, new Constant(0.3674));

        Expression const3 = InputParser.parse("40.");
        assertEquals(const3, new Constant(40.0));

        Expression const4 = InputParser.parse(" 4 5");
        assertEquals(const4, new Constant(45.0));

        Expression const5 = InputParser.parse("3.7 ");
        assertEquals(const5, new Constant(3.7));
    }

    @Test
    @DisplayName("Simple binary operations should parse to their respective nodes, regardless "
            + "of whitespace")
    void testSingleBinOp() throws UnreadableCharacterException, IncompleteExpressionException{
        Expression addOp1 = InputParser.parse("5+3.4");
        assertEquals(addOp1, new AddOperation(new Constant(5.0), new Constant(3.4)));

        Expression addOp2 = InputParser.parse("x + 2");
        assertEquals(addOp2, new AddOperation(new Variable("x"), new Constant(2.0)));

        Expression addOp3 = InputParser.parse("4+  y");
        assertEquals(addOp3, new AddOperation(new Constant(4.0), new Variable("y")));

        Expression subOp1 = InputParser.parse("4.2-6");
        assertEquals(subOp1, new SubOperation(new Constant(4.2), new Constant(6.0)));

        Expression multOp1 = InputParser.parse("x * y");
        assertEquals(multOp1, new MultOperation(new Variable("x"), new Variable("y")));

        Expression divOp1 = InputParser.parse("3/ y");
        assertEquals(divOp1, new DivOperation(new Constant(3.0), new Variable("y")));

        Expression powOp1 = InputParser.parse("x^2");
        assertEquals(powOp1, new PowOperation(new Variable("x"), new Constant(2.0)));
    }

    @Test
    @DisplayName("Simple functions should parse to their respective nodes, regardless of "
            + "whitespace")
    void testSingleFunc() throws UnreadableCharacterException, IncompleteExpressionException{
        Expression absFunc1 = InputParser.parse("abs(4.0)");
        assertEquals(absFunc1, new AbsFunc(new Constant(4.0)));

        Expression sqrtFunc1 = InputParser.parse("sqrt ( 3 )");
        assertEquals(sqrtFunc1, new SqrtFunc(new Constant(3.0)));

        Expression expFunc1 = InputParser.parse("exp( x)");
        assertEquals(expFunc1, new ExpFunc(new Variable("x")));

        Expression logFunc1 = InputParser.parse("log(y)");
        assertEquals(logFunc1, new LogFunc(new Variable("y")));

        Expression sinFunc1 = InputParser.parse("sin(0.314)");
        assertEquals(sinFunc1, new SinFunc(new Constant(0.314)));

        Expression cosFunc1 = InputParser.parse("cos(0)");
        assertEquals(cosFunc1, new CosFunc(new Constant(0.0)));

        Expression tanFunc1 = InputParser.parse("tan(z)");
        assertEquals(tanFunc1, new TanFunc(new Variable("z")));
    }

    @Test
    @DisplayName("Compound expressions should parse to their respective node chains, regardless "
            + "of whitespace")
    void testCompoundExpr() throws UnreadableCharacterException, IncompleteExpressionException{
        Expression expr1 = InputParser.parse("tan(3 * x)");
        assertEquals(expr1, new TanFunc(new MultOperation(new Constant(3.0), new Variable("x"))));

        Expression expr2 = InputParser.parse("x/(3 + exp(y))");
        assertEquals(expr2, new DivOperation(new Variable("x"), new AddOperation(new Constant(3.0),
                new ExpFunc(new Variable("y")))));

        Expression expr3 = InputParser.parse("sin( x ) - 35.42/(y+2.0)");
        assertEquals(expr3, new SubOperation(new SinFunc(new Variable("x")),
                new DivOperation(new Constant(35.42), new AddOperation(new Variable("y"),
                        new Constant(2.0)))));

        Expression expr4 = InputParser.parse("(2.7 ^ (log (35 ^ 1.0) ))");
        assertEquals(expr4, new PowOperation(new Constant(2.7), new LogFunc(new PowOperation(
                new Constant(35.0), new Constant(1.0)))));
    }

    @Test
    @DisplayName("Chains of binary operators should parse according to operator precedence, "
            + "regardless of whitespace")
    void testPrecAssoc() throws UnreadableCharacterException, IncompleteExpressionException{
        Expression expr1 = InputParser.parse("2.0 + x +15+y");
        assertEquals(expr1, new AddOperation(new AddOperation(new AddOperation(new Constant(2.0),
                new Variable("x")), new Constant(15.0)), new Variable("y")));

        Expression expr2 = InputParser.parse("x-  5 - 3.2");
        assertEquals(expr2, new SubOperation(new SubOperation(new Variable("x"), new Constant(5.0)),
                new Constant(3.2)));

        Expression expr3 = InputParser.parse("x - (5-3.2)");
        assertEquals(expr3, new SubOperation(new Variable("x"), new SubOperation(new Constant(5.0),
                new Constant(3.2))));

        Expression expr4 = InputParser.parse("x + 2.0 - y*3/4-2 * x");
        assertEquals(expr4, new SubOperation(new SubOperation(new AddOperation(new Variable("x"),
                new Constant(2.0)), new DivOperation(new MultOperation(new Variable("y"),
                new Constant(3.0)), new Constant(4.0))), new MultOperation(new Constant(2.0),
                new Variable("x"))));

        Expression expr5 = InputParser.parse("2.0 + x ^ 0.3 ^ y * 2");
        assertEquals(expr5, new AddOperation(new Constant(2.0), new MultOperation(new PowOperation(
                new Variable("x"), new PowOperation(new Constant(0.3), new Variable("y"))),
                new Constant(2.0))));
    }

    @Test
    @DisplayName("When a subtraction operator is used as a negative sign, it is parsed as such")
    void testNeg() throws UnreadableCharacterException, IncompleteExpressionException{
        Expression expr1 = InputParser.parse("-1-x");
        assertEquals(expr1, new SubOperation(new MultOperation(new Constant(-1.0), new Constant(1.0)),
                new Variable("x")));

        Expression expr2 = InputParser.parse("-(3 * x /2)");
        assertEquals(expr2, new MultOperation(new Constant(-1.0), new DivOperation(new MultOperation(
                new Constant(3.0), new Variable("x")), new Constant(2.0))));

        Expression expr3 = InputParser.parse("15.3 * -x*y^-3");
        assertEquals(expr3, new MultOperation(new MultOperation(new Constant(15.3),
                new MultOperation(new Constant(-1.0), new Variable("x"))),
                new PowOperation(new Variable("y"), new MultOperation(new Constant(-1.0),
                        new Constant(3.0)))));

        Expression expr4 = InputParser.parse("--x + --(3.2-y)");
        assertEquals(expr4, new AddOperation(new MultOperation(new Constant(-1.0),
                new MultOperation(new Constant(-1.0), new Variable("x"))),
                new MultOperation(new Constant(-1.0), new MultOperation(new Constant(-1.0),
                        new SubOperation(new Constant(3.2), new Variable("y"))))));
    }

    @Test
    @DisplayName("When a parenthesized expression, variable, or function is used directly after "
            + "another atom, this is parsed as a multiplication between the two")
    void testMultShorthand() throws UnreadableCharacterException, IncompleteExpressionException{
        Expression expr1 = InputParser.parse("3x");
        assertEquals(expr1, new MultOperation(new Constant(3.0), new Variable("x")));

        Expression expr2 = InputParser.parse("3.2 y - 4 sin(5.01)");
        assertEquals(expr2, new SubOperation(new MultOperation(new Constant(3.2),
                new Variable("y")), new MultOperation(new Constant(4.0),
                new SinFunc(new Constant(5.01)))));

        Expression expr3 = InputParser.parse("3.2 y - 4 sin(5.01) *xlog(y)");
        assertEquals(expr3, new SubOperation(new MultOperation(new Constant(3.2),
                new Variable("y")), new MultOperation(new MultOperation(new MultOperation(
                        new Constant(4.0), new SinFunc(new Constant(5.01))), new Variable("x")),
                new LogFunc(new Variable("y")))));

        Expression expr4 = InputParser.parse("x (3tan(y) + 5(2/x))");
        assertEquals(expr4, new MultOperation(new Variable("x"), new AddOperation(new MultOperation(
                new Constant(3.0), new TanFunc(new Variable("y"))), new MultOperation(
                        new Constant(5.0), new DivOperation(new Constant(2.0), new Variable("x"))))));
    }
}
