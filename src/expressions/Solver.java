package expressions;

public class Solver {
    public static void main(String[] args) {
        // Testing some basic stuff
        VarTable newTable = MapVarTable.of("x", 3.0, "y", 1.5);
        Expression testExpression = new MultOperation(new Constant(5.5), new SinFunc(
                new AddOperation(new MultOperation(new Variable("x"), new Variable("y")),
                        new DivOperation(new Variable("x"), new Constant(3.1)))));
        Expression newExpression = testExpression.differentiate("x");
        String out = newExpression.optimize(MapVarTable.empty()).infixString();
        String out2 = newExpression.infixString();
        try {
            double evaluated = newExpression.eval(newTable);
            System.out.println("" + evaluated);
        } catch (UnboundVariableException e) {
            System.out.println("unbound variable" + e);
        }
        System.out.println(out);
        System.out.println(out2);
    }
}
