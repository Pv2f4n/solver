package solving;

import java.util.ArrayList;
import java.util.Arrays;
import expressions.exceptions.*;
import expressions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemSolverTest {

    @Test
    @DisplayName("When given a matrix and vector with the same length, the corresponding "
            + "augmented matrix should be returned")
    void testAugment() {
        double[][] mat1 = {{1}};
        double[] vec1 = {0};
        double[][] result1 = {{1, 0}};
        assertTrue(Arrays.deepEquals(result1, SystemSolver.augment(mat1, vec1)));

        double[][] mat2 = {{5.3, 2.4, 7}};
        double[] vec2 = {3.1};
        double[][] result2 = {{5.3, 2.4, 7, 3.1}};
        assertTrue(Arrays.deepEquals(result2, SystemSolver.augment(mat2, vec2)));

        double[][] mat3 = {{2.4, 2}, {9.1, 6}};
        double[] vec3 = {6.3, 4};
        double[][] result3 = {{2.4, 2, 6.3}, {9.1, 6, 4}};
        assertTrue(Arrays.deepEquals(result3, SystemSolver.augment(mat3, vec3)));

        double[][] mat4 = {{5.1, 6.6, 7.3}, {1, 2, 3}, {4.5, 6, 7.08}, {1.5, 4, 2}, {6, 5.3, 3}};
        double[] vec4 = {1, 2, 3, 4, 5};
        double[][] result4 =
                {{5.1, 6.6, 7.3, 1}, {1, 2, 3, 2}, {4.5, 6, 7.08, 3}, {1.5, 4, 2, 4}, {6, 5.3, 3, 5}};
        assertTrue(Arrays.deepEquals(result4, SystemSolver.augment(mat4, vec4)));
    }

    @Test
    @DisplayName("When there is a unique element with largest absolute value in the specified "
            + "column and below the specified row (inclusive), that element's row index is "
            + "returned")
    void testPartialPivotUnique() throws SolvingException {
        double[][] mat1 = {{2, 1.3, 5, 6}, {1, 7, 3.2, 9}, {2.01, 6, 4, 2}};
        assertEquals(2, SystemSolver.partialPivot(mat1, 0, 0));
        assertEquals(2, SystemSolver.partialPivot(mat1, 0, 1));
        assertEquals(2, SystemSolver.partialPivot(mat1, 0, 2));
        assertEquals(1, SystemSolver.partialPivot(mat1, 1, 0));
        assertEquals(1, SystemSolver.partialPivot(mat1, 1, 1));
        assertEquals(2, SystemSolver.partialPivot(mat1, 1, 2));
        assertEquals(0, SystemSolver.partialPivot(mat1, 2, 0));
        assertEquals(2, SystemSolver.partialPivot(mat1, 2, 1));
        assertEquals(1, SystemSolver.partialPivot(mat1, 3, 0));
    }

    @Test
    @DisplayName("When there are multiple elements with the same largest absolute value in the "
            + "specified column and below the specified row (inclusive), the topmost element's "
            + "row index is returned")
    void testPartialPivotRepeat() throws SolvingException {
        double[][] mat1 = {{1, 6, 4.2}, {1, 7, 4.2}, {1, 8.7, 4.2}, {0.5, 8.7, 4.2}};
        assertEquals(0, SystemSolver.partialPivot(mat1, 0, 0));
        assertEquals(1, SystemSolver.partialPivot(mat1, 0, 1));
        assertEquals(2, SystemSolver.partialPivot(mat1, 0, 2));
        assertEquals(3, SystemSolver.partialPivot(mat1, 0, 3));
        assertEquals(2, SystemSolver.partialPivot(mat1, 1, 0));
        assertEquals(2, SystemSolver.partialPivot(mat1, 1, 1));
        assertEquals(2, SystemSolver.partialPivot(mat1, 1, 2));
        assertEquals(3, SystemSolver.partialPivot(mat1, 1, 3));
        assertEquals(0, SystemSolver.partialPivot(mat1, 2, 0));
        assertEquals(1, SystemSolver.partialPivot(mat1, 2, 1));
        assertEquals(2, SystemSolver.partialPivot(mat1, 2, 2));
        assertEquals(3, SystemSolver.partialPivot(mat1, 2, 3));
    }

    @Test
    @DisplayName("When all elements in a specified column below a specified row are zero, a "
            + "SolvingException is thrown")
    void testPartialPivotZeros() {
        double[][] mat1 = {{6, 0}, {0, 0}, {0, 0}, {0, 0}};
        assertThrows(SolvingException.class, () -> SystemSolver.partialPivot(mat1, 0, 1));
        assertThrows(SolvingException.class, () -> SystemSolver.partialPivot(mat1, 0, 2));
        assertThrows(SolvingException.class, () -> SystemSolver.partialPivot(mat1, 0, 3));
        assertThrows(SolvingException.class, () -> SystemSolver.partialPivot(mat1, 1, 0));
    }

    @Test
    @DisplayName("When two rows in a matrix are swapped, the matrix will have those rows swapped "
            + "with the other rows remaining the same")
    void testSwapRows() {
        double[][] mat1 = {{1}};
        double[][] result1 = {{1}};
        SystemSolver.swapRows(mat1, 0, 0);
        assertTrue(Arrays.deepEquals(result1, mat1));

        double[][] mat2 = {{1, 2, 4.3}};
        double[][] result2 = {{1, 2, 4.3}};
        SystemSolver.swapRows(mat2, 0, 0);
        assertTrue(Arrays.deepEquals(result2, mat2));

        double[][] mat3 = {{5, 3.6, 7.2}, {6, 9, 7}};
        double[][] result3 = {{5, 3.6, 7.2}, {6, 9, 7}};
        double[][] result4 = {{6, 9, 7}, {5, 3.6, 7.2}};
        SystemSolver.swapRows(mat3, 0, 0);
        assertTrue(Arrays.deepEquals(result3, mat3));
        SystemSolver.swapRows(mat3, 0, 1);
        assertTrue(Arrays.deepEquals(mat3, result4));
        SystemSolver.swapRows(mat3, 0, 1);
        assertTrue(Arrays.deepEquals(mat3, result3));

        double[][] mat4 = {{0, 1, 2, 3}, {4, 5, 6, 7}, {8, 9, 10, 11}, {12, 1, 2, 4}, {0, 0, 0, 0}};
        double[][] result5 = {{0, 1, 2, 3}, {4, 5, 6, 7}, {12, 1, 2, 4}, {8, 9, 10, 11}, {0, 0, 0, 0}};
        double[][] result6 = {{0, 1, 2, 3}, {12, 1, 2, 4}, {4, 5, 6, 7}, {8, 9, 10, 11}, {0, 0, 0, 0}};
        double[][] result7 = {{0, 0, 0, 0}, {12, 1, 2, 4}, {4, 5, 6, 7}, {8, 9, 10, 11}, {0, 1, 2, 3}};
        SystemSolver.swapRows(mat4, 2, 3);
        assertTrue(Arrays.deepEquals(result5, mat4));
        SystemSolver.swapRows(mat4, 1, 2);
        assertTrue(Arrays.deepEquals(result6, mat4));
        SystemSolver.swapRows(mat4, 0, 4);
        assertTrue(Arrays.deepEquals(result7, mat4));
    }

    @Test
    @DisplayName("When a row of a matrix is scaled, the matrix will have that row scaled with "
            + "the other rows remaining the same")
    void testScaleRow() {
        double[][] mat1 = {{1, 2, 4.3}};
        double[][] result1 = {{5.2, 10.4, 22.36}};
        double[][] result2 = {{0, 0, 0}};
        SystemSolver.scaleRow(mat1, 0, 5.2);
        assertTrue(Arrays.deepEquals(result1, mat1));
        SystemSolver.scaleRow(mat1, 0, 1);
        assertTrue(Arrays.deepEquals(result1, mat1));
        SystemSolver.scaleRow(mat1, 0, 0);
        assertTrue(Arrays.deepEquals(result2, mat1));

        double[][] mat2 = {{0, 1, 2, 3}, {4, 5, 6, 7}, {8, 9, 10, 11}, {12, 1, 2, 4}, {0, 0, 0, 0}};
        double[][] result3 = {{-0.0, -2.5, -5, -7.5}, {4, 5, 6, 7}, {8, 9, 10, 11}, {12, 1, 2, 4},
                {0, 0, 0, 0}};
        double[][] result4 = {{-0.0, -2.5, -5, -7.5}, {4, 5, 6, 7}, {16, 18, 20, 22}, {12, 1, 2, 4},
                {0, 0, 0, 0}};
        SystemSolver.scaleRow(mat2, 0, -2.5);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++)
                System.out.println(mat2[i][j]);
        }
        assertTrue(Arrays.deepEquals(result3, mat2));
        SystemSolver.scaleRow(mat2, 2, 2);
        assertTrue(Arrays.deepEquals(result4, mat2));
        SystemSolver.scaleRow(mat2, 4, 15);
        assertTrue(Arrays.deepEquals(result4, mat2));
    }

    @Test
    @DisplayName("When rows below a specified row are eliminated, multiples of the specified row "
            + "are correctly added to zero out the lower pivot column")
    void testEliminateBelow() {
        double[][] mat1 = {{1, 0, 7, 22}, {1, 1, 0, 1}, {2, 13, -3, -7}};
        double[][] result1 = {{1, 0, 7, 22}, {1, 1, 0, 1}, {0, 11, -3, -9}};
        double[][] result2 = {{1, 0, 7, 22}, {0, 1, -7, -21}, {0, 11, -3, -9}};
        double[][] result3 = {{1, 0, 7, 22}, {0, 1, -7, -21}, {0, 0, 74, 222}};
        SystemSolver.eliminateBelow(mat1, 1, 0);
        assertTrue(Arrays.deepEquals(result1, mat1));
        SystemSolver.eliminateBelow(mat1, 0, 0);
        assertTrue(Arrays.deepEquals(result2, mat1));
        SystemSolver.eliminateBelow(mat1, 0, 0);
        assertTrue(Arrays.deepEquals(result2, mat1));
        SystemSolver.eliminateBelow(mat1, 1, 1);
        assertTrue(Arrays.deepEquals(result3, mat1));
    }

    @Test
    @DisplayName("When a pivot column is eliminated, multiples of the specified row are correctly "
            + "added to zero out the pivot column")
    void testEliminate() {
        double[][] mat1 = {{1, -2, -12, 12}, {1, 1, 1, 2}, {2, 3, 4, 3}};
        double[][] result1 = {{0, -3, -13, 10}, {1, 1, 1, 2}, {0, 1, 2, -1}};
        double[][] mat2 = {{1, 0, -5, 9}, {0, 1, -2, 3}, {0, 0, 1, -1}};
        double[][] result2 = {{1, 0, 0, 4}, {0, 1, 0, 1}, {0, 0, 1, -1}};
        SystemSolver.eliminate(mat1, 1, 0);
        assertTrue(Arrays.deepEquals(result1, mat1));
        SystemSolver.eliminate(mat2, 2, 2);
        assertTrue(Arrays.deepEquals(result2, mat2));
    }

    @Test
    @DisplayName("When a number is rounded to some number of decimal places, the correctly "
            + "rounded answer should be returned")
    void testRound() {
        double result1 = 3.141592653589793;
        double result2 = 3.141592653590;
        assertEquals(result1, SystemSolver.round(3.141592653589793238, 15));
        assertEquals(result2, SystemSolver.round(3.141592653589793238, 12));
        assertEquals(3.0, SystemSolver.round(3.141592653589793238, 0));
        assertEquals(1.0, SystemSolver.round(1.0000001, 5));
        assertEquals(0.7, SystemSolver.round(0.6999, 3));
    }

    @Test
    @DisplayName("When a linear system with the same number of equations as unknowns and with a "
            + "unique solution is solved, that solution vector is returned")
    void testLinearUnique() throws SolvingException {
        double[][] mat1 = {{2, 13, -3}, {1, 1, 0}, {1, 0, 7}};
        double[] vec1 = {-7, 1, 22};
        double[] result1 = {1, 0, 3};
        assertArrayEquals(result1, SystemSolver.linear_solve(mat1, vec1));

        double[][] mat2 = {{1, -2, -12}, {2, 2, 2}, {2, 3, 4}};
        double[] vec2 = {12, 4, 3};
        double[] result2 = {2, 1, -1};
        assertArrayEquals(result2, SystemSolver.linear_solve(mat2, vec2));
    }

    @Test
    @DisplayName("When a linear system has infinite solutions or no solutions, a SolvingException "
            + "is thrown")
    void testLinearNonUnique() {
        double[][] mat1 = {{3, -1, -2}, {3, -6, -6}, {6, -2, -4}};
        double[] vec1 = {-2, -24, -4};
        assertThrows(SolvingException.class, () -> SystemSolver.linear_solve(mat1, vec1));

        double[][] mat2 = {{3, -1, -2}, {3, -6, -6}, {6, -2, -4}};
        double[] vec2 = {-2, -24, 2};
        assertThrows(SolvingException.class, () -> SystemSolver.linear_solve(mat2, vec2));

        double[][] mat3 = {{1, 1, 1}, {1, -1, -1}, {2, 6, 6}};
        double[] vec3 = {5, 4, 12};
        assertThrows(SolvingException.class, () -> SystemSolver.linear_solve(mat3, vec3));

        double[][] mat4 = {{1, 3, 1}, {-1, -1, 1}, {2, 4, 0}};
        double[] vec4 = {4, -1, 0};
        assertThrows(SolvingException.class, () -> SystemSolver.linear_solve(mat4, vec4));
    }

    @Test
    @DisplayName("When a linear system with a unique solution is solved, that solution vector is "
            + "returned in String form")
    void testGenLinearUnique() throws SolvingException {
        double[][] mat1 = {{2, 13, -3}, {1, 1, 0}, {1, 0, 7}};
        double[] vec1 = {-7, 1, 22};
        ArrayList<String> ans1 = SystemSolver.linear_solve_general(mat1, vec1);
        assertEquals(3, ans1.size());
        assertEquals("x1 = 1.0", ans1.get(0));
        assertEquals("x2 = 0.0", ans1.get(1));
        assertEquals("x3 = 3.0", ans1.get(2));

        double[][] mat2 = {{1, -2, -12}, {2, 2, 2}, {2, 3, 4}};
        double[] vec2 = {12, 4, 3};
        ArrayList<String> ans2 = SystemSolver.linear_solve_general(mat2, vec2);
        assertEquals(3, ans2.size());
        assertEquals("x1 = 2.0", ans2.get(0));
        assertEquals("x2 = 1.0", ans2.get(1));
        assertEquals("x3 = -1.0", ans2.get(2));

        double[][] mat3 = {{-5, 5, 10}, {3, -1, -2}, {3, -6, -6}, {6, -2, -4}};
        double[] vec3 = {-10, -2, -24, -4};
        ArrayList<String> ans3 = SystemSolver.linear_solve_general(mat3, vec3);
        assertEquals(3, ans3.size());
        assertEquals("x1 = -2.0", ans3.get(0));
        assertEquals("x2 = 10.0", ans3.get(1));
        assertEquals("x3 = -7.0", ans3.get(2));
    }

    @Test
    @DisplayName("When a linear system with infinitely many solutions is solved, the simplified "
            + "equations are returned in String form")
    void testGenLinearInfinite() throws SolvingException {
        double[][] mat1 = {{4, -1, -2}, {3, -7, -6}, {8, -2, -4}};
        double[] vec1 = {-2, -24, -4};
        ArrayList<String> ans1 = SystemSolver.linear_solve_general(mat1, vec1);
        assertEquals(2, ans1.size());
        assertEquals("x1 - 0.32x3 = 0.4", ans1.get(0));
        assertEquals("x2 + 0.72x3 = 3.6", ans1.get(1));

        double[][] mat2 = {{1, 2, 1, -4, 1}, {1, 2, -1, 2, -1}, {2, 4, 1, -5, 1}, {1, 2, 3, -10, 2}};
        double[] vec2 = {0, 0, 0, 0};
        ArrayList<String> ans2 = SystemSolver.linear_solve_general(mat2, vec2);
        assertEquals(3, ans2.size());
        assertEquals("x1 + 2.0x2 - x4 = 0.0", ans2.get(0));
        assertEquals("x3 - 3.0x4 = 0.0", ans2.get(1));
        assertEquals("x5 = 0.0", ans2.get(2));

        double[][] mat3 = {{1, 1, 1}, {1, -1, -1}, {2, 6, 6}};
        double[] vec3 = {5, 4, 12};
        ArrayList<String> ans3 = SystemSolver.linear_solve_general(mat3, vec3);
        assertEquals(2, ans3.size());
        assertEquals("x1 = 4.5", ans3.get(0));
        assertEquals("x2 + x3 = 0.5", ans3.get(1));
    }

    @Test
    @DisplayName("When a linear system with no solutions is solved, a SolvingException is thrown")
    void testGenLinearNoSols() {
        double[][] mat1 = {{1, 2}, {3, 6}};
        double[] vec1 = {5, 7};
        assertThrows(SolvingException.class, () -> SystemSolver.linear_solve_general(mat1, vec1));

        double[][] mat2 = {{1, 3, 1}, {-1, -1, 1}, {2, 4, 0}};
        double[] vec2 = {4, -1, 0};
        assertThrows(SolvingException.class, () -> SystemSolver.linear_solve_general(mat2, vec2));
    }

    @Test
    @DisplayName("When a function is inputted in String form, the roots of the function "
            + "are returned given a sufficiently close initial guess")
    // Todo: find a more systemic way to test / add more tests
    void testNonLinearBasic()
            throws SolvingException, IncompleteExpressionException, UnreadableCharacterException {
        Expression[] input1 = {InputParser.parse("8x +  5")};
        String[] vars1 = {"x"};
        double[] start10 = {0.1};
        double[] start11 = {-1.5};
        double[] start12 = {-0.625};
        double[] result1 = {-0.625};
        assertArrayEquals(result1, SystemSolver.nonlinear_solve(input1, vars1, start10));
        assertArrayEquals(result1, SystemSolver.nonlinear_solve(input1, vars1, start11));
        assertArrayEquals(result1, SystemSolver.nonlinear_solve(input1, vars1, start12));

        Expression[] input2 = {InputParser.parse("x^2 + x - 6")};
        String[] vars2 = {"x"};
        double[] start20 = {2.7};
        double[] start21 = {0.8};
        double[] start22 = {-1.6};
        double[] start23 = {-5};
        double[] result20 = {2};
        double[] result21 = {-3};
        assertArrayEquals(result20, SystemSolver.nonlinear_solve(input2, vars2, start20));
        assertArrayEquals(result20, SystemSolver.nonlinear_solve(input2, vars2, start21));
        assertArrayEquals(result21, SystemSolver.nonlinear_solve(input2, vars2, start22));
        assertArrayEquals(result21, SystemSolver.nonlinear_solve(input2, vars2, start23));

        Expression[] input3 = {InputParser.parse("(3x^2-3)/(1+y^2)-2xz+2z"),
                InputParser.parse("2yz+((2y)(x^3-3x))/(1+y^2)^2"), InputParser.parse("(x-1)^2+y^2-9")};
        String[] vars3 = {"x", "y", "z"};
        double[] start30 = {-1.7, 0.2, -1.4};
        double[] start31 = {-2.2, -0.2, -1.7};
        double[] start32 = {4.2, 0.4, 7.3};
        double[] start33 = {3.6, 0.1, 7.3};
        double[] start34 = {1.1, -3.2, 0.01};
        double[] start35 = {0.8, -2.9, 0.03};
        double[] start36 = {0.8, 2.7, 0.02};
        double[] result30 = {-2, 0, -1.5};
        double[] result31 = {4, 0, 7.5};
        double[] result32 = {1, -3, 0.02};
        double[] result33 = {1, 3, 0.02};
        assertArrayEquals(result30, SystemSolver.nonlinear_solve(input3, vars3, start30));
        assertArrayEquals(result30, SystemSolver.nonlinear_solve(input3, vars3, start31));
        assertArrayEquals(result31, SystemSolver.nonlinear_solve(input3, vars3, start32));
        assertArrayEquals(result31, SystemSolver.nonlinear_solve(input3, vars3, start33));
        assertArrayEquals(result32, SystemSolver.nonlinear_solve(input3, vars3, start34));
        assertArrayEquals(result32, SystemSolver.nonlinear_solve(input3, vars3, start35));
        assertArrayEquals(result33, SystemSolver.nonlinear_solve(input3, vars3, start36));
    }

}
