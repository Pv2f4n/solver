package expressions;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantExpressionTest {

    @Test
    @DisplayName("A Constant node should evaluate to its value (regardless of var table)")
    void testEval() throws UnboundVariableException {
        Expression expr = new Constant(1.5);
        assertEquals(1.5, expr.eval(MapVarTable.empty()));
    }


    @Test
    @DisplayName("A Constant node should report that 0 operations are required to evaluate it")
    void testOpCount() {
        Expression expr = new Constant(1.5);
        assertEquals(0, expr.opCount());
    }


    @Test
    @DisplayName("A Constant node should produce an infix representation with just its value (as " +
            "formatted by String.valueOf(double))")
    void testInfix() {
        Expression expr = new Constant(1.5);
        assertEquals("1.5", expr.infixString());

        expr = new Constant(Math.PI);
        assertEquals("3.141592653589793", expr.infixString());
    }

    @Test
    @DisplayName("A Constant node should produce an postfix representation with just its value " +
            "(as formatted by String.valueOf(double))")
    void testPostfix() {
        Expression expr = new Constant(1.5);
        assertEquals("1.5", expr.postfixString());

        expr = new Constant(Math.PI);
        assertEquals("3.141592653589793", expr.postfixString());
    }


    @Test
    @DisplayName("A Constant node should equal itself")
    void testEqualsSelf() {
        Expression expr = new Constant(1.5);
        // Normally `assertEquals()` is preferred, but since we are specifically testing the
        // `equals()` method, we use the more awkward `assertTrue()` to make that call explicit.
        assertTrue(expr.equals(expr));
    }

    @Test
    @DisplayName("A Constant node should equal another Constant node with the same value")
    void testEqualsTrue() {
        Expression expr1 = new Constant(1.5);
        Expression expr2 = new Constant(1.5);
        assertTrue(expr1.equals(expr2));
    }

    @Test
    @DisplayName("A Constant node should not equal another Constant node with a different value")
    void testEqualsFalse() {
        Expression expr1 = new Constant(1.5);
        Expression expr2 = new Constant(2.0);
        assertFalse(expr1.equals(expr2));
    }


    @Test
    @DisplayName("A Constant node does not depend on any variables")
    void testDependencies() {
        Expression expr = new Constant(1.5);
        Set<String> deps = expr.dependencies();
        assertTrue(deps.isEmpty());
    }


    @Test
    @DisplayName("A Constant node should optimize to itself (regardless of var table)")
    void testOptimize() {
        Expression expr = new Constant(1.5);
        Expression opt = expr.optimize(MapVarTable.empty());
        assertEquals(expr, opt);
    }

    @Test
    @DisplayName("A Constant node should differentiate to the zero constant")
    void testDiff() {
        Expression expr = new Constant(2.2);
        Expression diff = expr.differentiate("x");
        assertEquals(diff, new Constant(0.0));
    }
}

class VariableExpressionTest {

    @Test
    @DisplayName("A Variable node should evaluate to its variable's value when that variable is " +
            "in the var map")
    void testEvalBound() throws UnboundVariableException {
        Expression var = new Variable("x");
        VarTable varMap = new MapVarTable();
        varMap.set("x", 5);
        assertEquals(5, var.eval(varMap));
    }

    @Test
    @DisplayName("A Variable node should throw an UnboundVariableException when evaluated if its " +
            "variable is not in the var map")
    void testEvalUnbound() {
        Expression expr = new Variable("x");
        assertThrows(UnboundVariableException.class, () -> expr.eval(MapVarTable.empty()));
    }


    @Test
    @DisplayName("A Variable node should report that 0 operations are required to evaluate it")
    void testOpCount() {
        Expression var = new Variable("x");
        assertEquals(0, var.opCount());
    }


    @Test
    @DisplayName("A Variable node should produce an infix representation with just its name")
    void testInfix() {
        Expression var = new Variable("x");
        assertEquals("x", var.infixString());
    }

    @Test
    @DisplayName("A Variable node should produce an postfix representation with just its name")
    void testPostfix() {
        Expression var = new Variable("x");
        assertEquals("x", var.postfixString());
    }


    @Test
    @DisplayName("A Variable node should equal itself")
    void testEqualsSelf() {
        Expression expr = new Variable("x");
        assertTrue(expr.equals(expr));
    }

    @Test
    @DisplayName("A Variable node should equal another Variable node with the same name")
    void testEqualsTrue() {
        Expression expr1 = new Variable(new String("x"));
        Expression expr2 = new Variable(new String("x"));
        assertTrue(expr1.equals(expr2));
    }

    @Test
    @DisplayName("A Variable node should not equal another Variable node with a different name")
    void testEqualsFalse() {
        Expression expr1 = new Variable(new String("x"));
        Expression expr2 = new Variable(new String("y"));
        assertFalse(expr1.equals(expr2));
    }


    @Test
    @DisplayName("A Variable node only depends on its name")
    void testDependencies() {
        Expression expr = new Variable("x");
        Set<String> deps = expr.dependencies();
        assertTrue(deps.contains("x"));
        assertEquals(1, deps.size());
    }


    @Test
    @DisplayName("A Variable node should optimize to a Constant if its variable is in the var map")
    void testOptimizeBound() {
        Expression expr = new Variable("x");
        Expression opt = expr.optimize(MapVarTable.of("x", 1.5));
        assertEquals(new Constant(1.5), opt);
    }

    @Test
    @DisplayName("A Variable node should optimize to itself if its variable is not in the var map")
    void testOptimizeUnbound() {
        Expression expr = new Variable("x");
        assertEquals(expr, expr.optimize(MapVarTable.empty()));
    }

    @Test
    @DisplayName("When a Variable node is differentiated with respect to itself, the Constant 1.0 "
            + "should be returned")
    void testDiffSameVar() {
        Expression expr = new Variable("x");
        Expression diff = expr.differentiate("x");
        assertEquals(diff, new Constant(1.0));
    }

    @Test
    @DisplayName("When a Variable node is differentiated with respect to a different variable, "
            + "the Constant 0.0 should be returned")
    void testDiffDiffVar() {
        Expression expr = new Variable("x");
        Expression diff = expr.differentiate("y");
        assertEquals(diff, new Constant(0.0));
    }
}

class OperationExpressionTest {

    @Test
    @DisplayName("An Operation node for ADD with two Constant operands should evaluate to their " +
            "sum")
    void testEvalAdd() throws UnboundVariableException {
        Expression expr = new AddOperation(new Constant(1.5), new Constant(2));
        assertEquals(3.5, expr.eval(MapVarTable.empty()));
    }

    @Test
    @DisplayName("An Operation node for ADD with a Variable for an operand should evaluate " +
            "to its operands' sum when the variable is in the var map")
    void testEvalAddBound() throws UnboundVariableException {
        Expression var = new Variable("x");
        VarTable table = new MapVarTable();
        table.set("x",5);
        Expression expr = new AddOperation(var, new Constant(2));
        assertEquals(7, expr.eval(table));
    }

    @Test
    @DisplayName("An Operation node for ADD with a Variable for an operand should throw an " +
            "UnboundVariableException when evaluated if the variable is not in the var map")
    void testEvalAddUnbound() {
        Expression var = new Variable("x");
        VarTable table = new MapVarTable();
        Expression expr = new AddOperation(var, new Constant(2));
        assertThrows(UnboundVariableException.class, () -> expr.eval(table));
    }

    @Test
    @DisplayName("An Operation node for DIVIDE with two Constant operands should evaluate to their " +
            "quotient")
    void testEvalDivide() throws UnboundVariableException {
        Expression expr = new DivOperation(new Constant(1.0), new Constant(2.0));
        assertEquals(0.5, expr.eval(MapVarTable.empty()));
    }

    @Test
    @DisplayName("An Operation node with leaf operands should report that 1 operation is " +
            "required to evaluate it")
    void testOpCountLeaves() {
        Expression expr = new AddOperation(new Variable("x"), new Constant(4));
        VarTable table = new MapVarTable();
        table.set("x",3);
        assertEquals(1, expr.opCount());
    }


    @Test
    @DisplayName("An Operation node with an Operation for either or both operands should report " +
            "the correct number of operations to evaluate it")
    void testOpCountRecursive() {
        Expression expr = new AddOperation(
                new MultOperation(new Constant(1.5), new Variable("x")),
                new Constant(2.0));
        assertEquals(2, expr.opCount());

        expr = new SubOperation(
                new MultOperation(new Constant(1.5), new Variable("x")),
                new DivOperation(new Constant(1.5), new Variable("x")));
        assertEquals(3, expr.opCount());
    }


    @Test
    @DisplayName("An Operation node with leaf operands should produce an infix representation " +
            "consisting of its first operand, its operator symbol surrounded by spaces, and " +
            "its second operand, all enclosed in parentheses")
    void testInfixLeaves() {
        Expression expr = new AddOperation(new Variable("x"), new Constant(4.0));
        VarTable table = new MapVarTable();
        table.set("x",3);
        assertEquals("(x + 4.0)", expr.infixString());
    }

    @Test
    @DisplayName("An Operation node with an Operation for either operand should produce the " +
            "expected infix representation with parentheses around each operation")
    void testInfixRecursive() {
        Expression expr = new AddOperation(new MultOperation(new Constant(1.5), new Variable("x")),
                new Constant(2.0));
        assertEquals("((1.5 * x) + 2.0)", expr.infixString());

        expr = new SubOperation(new Constant(2.0),
                new DivOperation(new Constant(1.5), new Variable("x")));
        assertEquals("(2.0 - (1.5 / x))", expr.infixString());
    }


    @Test
    @DisplayName("An Operation node with leaf operands should produce a postfix representation " +
            "consisting of its first operand, its second operand, and its operator symbol " +
            "separated by spaces")
    void testPostfixLeaves() {
        Expression expr = new AddOperation(new Variable("x"), new Constant(4.0));
        VarTable table = new MapVarTable();
        table.set("x",3);
        assertEquals("x 4.0 +", expr.postfixString());
    }

    @Test
    @DisplayName("An Operation node with an Operation for either operand should produce the " +
            "expected postfix representation")
    void testPostfixRecursive() {
        Expression expr = new AddOperation(
                new MultOperation(new Constant(1.5), new Variable("x")),
                new Constant(2.0));
        assertEquals("1.5 x * 2.0 +", expr.postfixString());

        expr = new SubOperation(
                new Constant(2.0),
                new DivOperation(new Constant(1.5), new Variable("x")));
        assertEquals("2.0 1.5 x / -", expr.postfixString());
    }


    @Test
    @DisplayName("An Operation node should equal itself")
    void testEqualsSelf() {
        Expression expr = new AddOperation(new Constant(1.5), new Variable("x"));
        assertTrue(expr.equals(expr));
    }

    @Test
    @DisplayName("An Operation node should equal another Operation node with the same " +
            "operator and operands")
    void testEqualsTrue() {
        Expression expr1 = new AddOperation(new Constant(1.5), new Variable("x"));
        Expression expr2 = new AddOperation(new Constant(1.5), new Variable("x"));
        assertTrue(expr1.equals(expr2));
    }

    @Test
    @DisplayName("An Operation node should not equal another Operation node with a different " +
            "operator")
    void testEqualsFalse() {
        Expression expr1 = new AddOperation(new Constant(1.5), new Variable("x"));
        Expression expr2 = new SubOperation(new Constant(1.5), new Variable("x"));
        assertFalse(expr1.equals(expr2));
    }


    @Test
    @DisplayName("An Operation node depends on the dependencies of both of its operands")
    void testDependencies() {
        Expression expr = new AddOperation(new Variable("x"), new Variable("y"));
        Set<String> deps = expr.dependencies();
        assertTrue(deps.contains("x"));
        assertTrue(deps.contains("y"));
        assertEquals(2, deps.size());
    }

    @Test
    @DisplayName("An Operation node depends on the dependencies of both of its operands (when"
            + "both are Constants")
    void testDependenciesConstant() {
        Expression expr = new MultOperation(new Constant(5.0), new Constant(2.0));
        Set<String> deps = expr.dependencies();
        assertEquals(0, deps.size());
    }

    @Test
    @DisplayName("An Operation node for ADD with two Constant operands should optimize to a " +
            "Constant containing their sum")
    void testOptimizeAdd() {
        Expression expr = new AddOperation(new Constant(6.0), new Constant(9.0));
        Expression optExpr = new Constant(15.0);
        assertEquals(optExpr, expr.optimize(MapVarTable.empty()));
    }

    @Test
    @DisplayName("An Operation node for MULTIPLY with two unbound Variable operands should "
            + "optimize to the same operation")
    void testOptimizeMultiplyUnbound() {
        Expression expr = new MultOperation(new Variable("x"), new Variable("y"));
        assertEquals(expr, expr.optimize(MapVarTable.empty()));
    }
}

class ApplicationExpressionTest {

    @Test
    @DisplayName("An Application node for SQRT with a Constant argument should evaluate to its " +
            "square root")
    void testEvalSqrt() throws UnboundVariableException {
        Expression expr = new LogFunc(new Constant(9.0));
        assertEquals(3.0, expr.eval(MapVarTable.empty()));
    }

    @Test
    @DisplayName("An Application node with a Variable for its argument should throw an " +
            "UnboundVariableException when evaluated if the variable is not in the var map")
    void testEvalAbsUnbound() {
        Expression expr = new LogFunc(new Variable("x"));
        assertThrows(UnboundVariableException.class, () -> expr.eval(MapVarTable.empty()));
    }


    @Test
    @DisplayName("An Application node with a leaf argument should report that 1 operation is " +
            "required to evaluate it")
    void testOpCountLeaf() {
        Expression expr = new LogFunc(new Constant(9.0));
        assertEquals(1, expr.opCount());

        Expression expr2 = new AbsFunc(new Variable("x"));
        assertEquals(1, expr2.opCount());
    }


    @Test
    @DisplayName("An Application node with non-leaf expressions for its argument should report " +
            "the correct number of operations to evaluate it")
    void testOpCountRecursive() {
        Expression expr = new LogFunc(
                new MultOperation(new Constant(1.5), new Variable("x")));
        assertEquals(2, expr.opCount());
    }


    @Test
    @DisplayName(
            "An Application node with a leaf argument should produce an infix representation " +
                    "consisting of its name, followed by the argument enclosed in parentheses.")
    void testInfixLeaves() {
        Expression expr = new TanFunc(new Constant(1337));
        assertEquals("tan(1337.0)", expr.infixString());
    }

    @Test
    @DisplayName("An Application node with an Operation for its argument should produce the " +
            "expected infix representation with redundant parentheses around the argument")
    void testInfixRecursive() {
        Expression expr = new AbsFunc(
                new MultOperation(new Constant(1.5), new Variable("x")));
        assertEquals("abs((1.5 * x))", expr.infixString());
    }


    @Test
    @DisplayName("An Application node with a leaf argument should produce a postfix " +
            "representation consisting of its argument, followed by a space, followed by its " +
            "function's name appended with parentheses")
    void testPostfixLeaves() {
        Expression expr = new SinFunc(new Variable("x"));
        assertEquals("x sin()", expr.postfixString());
    }

    @Test
    @DisplayName("An Application node with an Operation for its argument should produce the " +
            "expected postfix representation")
    void testPostfixRecursive() {
        Expression expr = new SinFunc(new MultOperation(
                new Constant(0.5), new Variable("x")));
        assertEquals("0.5 x * sin()", expr.postfixString());

        Expression expr2 = new AbsFunc(new DivOperation(
                new Variable("y"), new Variable("x")));
        assertEquals("y x / abs()", expr2.postfixString());
    }

    @Test
    @DisplayName("An Application node should equal itself")
    void testEqualsSelf() {
        Expression expr = new LogFunc(new Constant(4.0));
        assertTrue(expr.equals(expr));
    }

    @Test
    @DisplayName("An Application node should equal another Application node with the same " +
            "function and argument")
    void testEqualsTrue() {
        Expression expr1 = new LogFunc(new Constant(4.0));
        Expression expr2 = new LogFunc(new Constant(4.0));
        assertTrue(expr1.equals(expr2));
    }

    @Test
    @DisplayName("An Application node should not equal another Application node with a different " +
            "argument")
    void testEqualsFalseArg() {
        Expression expr1 = new LogFunc(new Constant(4.0));
        Expression expr2 = new LogFunc(new Constant(4.2));
        assertFalse(expr1.equals(expr2));

        Expression expr3 = new LogFunc(new Constant(4.0));
        Expression expr4 = new LogFunc(new Variable("x"));
        VarTable varTable = new MapVarTable();
        varTable.set("x", 4.0);
        assertFalse(expr1.equals(expr2));

        Expression expr5 = new LogFunc(new Variable("y"));
        Expression expr6 = new LogFunc(new Variable("x"));
        VarTable varTable2 = new MapVarTable();
        varTable2.set("x", 4.0);
        varTable2.set("y", 4.0);
        assertFalse(expr1.equals(expr2));
    }

    @Test
    @DisplayName("An Application node should not equal another Application node with a different " +
            "function")
    void testEqualsFalseFunc() {
        Expression expr1 = new LogFunc(new Constant(4.0));
        Expression expr2 = new AbsFunc(new Constant(4.0));
        assertFalse(expr1.equals(expr2));
    }


    @Test
    @DisplayName("An Application node has the same dependencies as its argument")
    void testDependencies() {
        Expression arg = new Variable("x");
        Expression expr = new LogFunc(arg);
        Set<String> argDeps = arg.dependencies();
        Set<String> exprDeps = expr.dependencies();
        assertEquals(argDeps, exprDeps);
    }


    @Test
    @DisplayName("An Application node for SQRT with a Constant argument should optimize to a " +
            "Constant containing its square root")
    void testOptimizeConstant() {
        Expression expr = new LogFunc(new Constant(9.0));
        Expression optExpr = new Constant(3.0);
        assertEquals(optExpr, expr.optimize(MapVarTable.empty()));
    }


    @Test
    @DisplayName("An Application node with an argument depending on a variable should optimize " +
            "to an Application node if the variable is unbound")
    void testOptimizeUnbound() {
        Expression expr = new LogFunc(new Variable("x"));
        Expression opt = expr.optimize(MapVarTable.empty());
        assertInstanceOf(Application.class, opt);
    }

}
