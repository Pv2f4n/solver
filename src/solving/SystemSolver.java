package solving;

import expressions.*;
import expressions.exceptions.*;
import java.util.ArrayList;

/**
 * Class for solving systems of equations.
 */
public class SystemSolver {

    /**
     * Returns a solution as computed by Newton's method beginning at the point `start` to the
     * system of equations specified by setting each of the Expressions in `equations` to 0.
     * Coordinate order of solution is determined by the order of variables in `vars`. Requires
     * `equations`, `vars`, and `start` all have the same length. Throws SolvingException
     * if iteration has not converged after twelve iterations or if derivative is not invertible at
     * any point; to prevent this, avoid including zeros in `start`. Convergence is defined as the
     * norm of the difference between the previous and current iteration result being less than
     * 1e-15. All returned values are rounded to 14 decimal places to account for accumulating
     * floating point errors.
     */
    public static double[] nonlinear_solve(Expression[] equations, String[] vars, double[] start)
            throws SolvingException {
        assert equations.length == vars.length && vars.length == start.length;

        int dim = equations.length;
        // Creating Jacobian matrix
        Expression[][] jacobian = new Expression[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                jacobian[i][j] = equations[i].differentiate(vars[j]);
            }
        }
        double[] curPoint = start;
        double[][] df = new double[dim][dim];
        MapVarTable varmap = MapVarTable.of(vars, curPoint);
        double[] bvector = new double[dim];
        int iterCount = 0;
        while (true) {
            // Creating derivative matrix at curPoint
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    try {
                        df[i][j] = jacobian[i][j].eval(varmap);
                    } catch (UnboundVariableException e) {
                        // Shouldn't happen since we are using the same variable list `vars`
                        throw new RuntimeException(e);
                    }
                }
            }
            // Calculating negation of system of equations evaluated at curPoint
            for (int i = 0; i < dim; i++) {
                try {
                    bvector[i] = -1.0 * equations[i].eval(varmap);
                } catch (UnboundVariableException e) {
                    // Shouldn't happen since we are using the same variable list `vars`
                    throw new RuntimeException(e);
                }
            }
            // Calculate nextPoint - curPoint in [Df(curPoint)](nextPoint - curPoint) = bvector
            double[] tempPoint = linear_solve(df, bvector);
            // Check for convergence
            double squareSum = 0;
            for (int i = 0; i < dim; i++) {
                squareSum += tempPoint[i] * tempPoint[i];
            }
            if (Math.sqrt(squareSum) < 1e-15) {
                for (int i = 0; i < dim; i++) {
                    curPoint[i] = round(curPoint[i], 14);
                }
                return curPoint;
            } else if (iterCount >= 12) {
                throw new SolvingException("Did not converge within 12 iterations.");
            } else {
                // Set curPoint to nextPoint and update varmap
                for (int i = 0; i < dim; i++) {
                    curPoint[i] = tempPoint[i] + curPoint[i];
                }
                varmap = MapVarTable.of(vars, curPoint);
                iterCount++;
            }
        }
    }

    /**
     * Returns the unique solution vector x to the system of linear equations Ax = b where A is the
     * square matrix 'mat' and b is the vector 'vec'. Requires `mat` is a square matrix with each
     * dimension being the same as the length of `vec`. Throws SolvingException if there is no
     * solution to the system or if there are infinitely many solutions. For non-square matrices
     * or sparse matrices for which there are likely infinitely many solutions, use
     * linear_solve_general instead. All returned values are rounded to 14 decimal places to
     * account for accumulating floating point errors.
     */
    public static double[] linear_solve(double[][] mat, double[] vec) throws SolvingException {
        assert mat.length == mat[0].length && mat.length == vec.length;

        int dim = mat.length;
        // Creating the augmented matrix
        double[][] augment = augment(mat, vec);
        // Row reduction
        for (int j = 0; j < dim; j++) {
            // Partial pivoting
            int maxIdx = partialPivot(augment, j, j);
            // Swapping pivot row into jth row
            swapRows(augment, j, maxIdx);
            // Scaling jth row (pivot won't be zero by check above)
            scaleRow(augment, j, 1 / augment[j][j]);
            // Elimination step (partial row reduction, so only care about lower rows)
            eliminateBelow(augment, j, j);
        }
        // Back substitution (only suitable for unique solution systems)
        double[] ansVector = new double[dim];
        for (int i = dim - 1; i >= 0; i--) {
            double rowSum = augment[i][dim];
            for (int k = dim - 1; k > i; k--) {
                rowSum -= augment[i][k] * ansVector[k];
            }
            ansVector[i] = rowSum;
        }
        for (int i = 0; i < dim; i++) {
            ansVector[i] = round(ansVector[i], 14);
        }
        return ansVector;
    }

    /**
     * Returns the solution set, expressed as an ArrayList of strings in terms of variables x1
     * through xm (e.g. "x1 = 0", "x2 + 2.0x3 = 5"), to the system of linear equations Ax = b where
     * A is the rectangular m-by-n matrix `mat` and b is the m-vector `vec`. Requires `mat` is a
     * rectangular matrix whose number of rows is equal to the length of `vec`. Throws
     * SolvingException if there is no solution to the system. All returned values are rounded to
     * 14 decimal places to account for accumulating floating point errors.
     */
    public static ArrayList<String> linear_solve_general(double[][] mat, double[] vec)
            throws SolvingException {
        assert mat.length == vec.length;

        int rows = mat.length;
        int cols = mat[0].length;
        double[][] augment = augment(mat, vec);
        int curCol = 0;
        int curRow = 0;
        while (curCol < cols && curRow < rows) {
            // Row reduction
            try {
                int maxIdx = partialPivot(augment, curCol, curRow);
                swapRows(augment, curRow, maxIdx);
                scaleRow(augment, curRow, 1 / augment[curRow][curCol]);
                eliminate(augment, curRow, curCol);
                curRow++;
                curCol++;
            } catch (SolvingException e) {
                curCol++;
            }
        }
        // Rounding matrix entries before converting into Strings
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols + 1; j++) {
                augment[i][j] = round(augment[i][j], 14);
            }
        }
        // Reading result into ArrayList of Strings giving the final equations in terms of
        // variables x1, x2, etc.
        ArrayList<String> toReturn = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            StringBuilder temp = new StringBuilder();
            for (int j = 0; j < cols; j++) {
                if (augment[i][j] != 0) {
                    if (temp.isEmpty()) {
                        if (augment[i][j] == 1) {
                            temp.append("x").append(j + 1);
                        } else {
                            temp.append(augment[i][j]).append("x").append(j + 1);
                        }
                    } else if (augment[i][j] < 0) {
                        if (augment[i][j] == -1) {
                            temp.append(" - ").append("x").append(j + 1);
                        } else {
                            temp.append(" - ").append(-1.0 * augment[i][j]).append("x")
                                    .append(j + 1);
                        }
                    } else {
                        if (augment[i][j] == 1) {
                            temp.append(" + ").append("x").append(j + 1);
                        } else {
                            temp.append(" + ").append(augment[i][j]).append("x").append(j + 1);
                        }
                    }
                }
            }
            if (temp.isEmpty()) {
                // If entire row zero but last entry is nonzero, no solutions; if last entry zero,
                // then equation does not contribute to results
                if (augment[i][cols] != 0) {
                    throw new SolvingException("System has no solutions");
                }
            } else {
                temp.append(" = ").append(augment[i][cols]);
                toReturn.add(temp.toString());
            }
        }
        return toReturn;
    }

    /**
     * Returns the matrix creating by appending vector 'vec' to the right of matrix `mat`.
     * Requires the length of `vec` equals the length (number of rows) of `mat` and `mat` is
     * a rectangular matrix.
     */
    public static double[][] augment(double[][] mat, double[] vec) {
        assert mat.length == vec.length;

        double[][] toReturn = new double[mat.length][mat[0].length + 1];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                toReturn[i][j] = mat[i][j];
            }
            toReturn[i][mat[0].length] = vec[i];
        }
        return toReturn;
    }

    /**
     * Returns the row index of the element with the largest absolute value in column `col` at or
     * below row `startRow` in matrix `mat`. If two elements have the same largest absolute value,
     * the one in the highest (lowest index) row will be returned. Requires `mat` is rectangular,
     * 0 <= col < mat[0].length, and 0 <= startRow < mat.length. Throws SolvingException if there
     * is no pivot in `col` (i.e. all values at or below row `startRow` in `col` are less than
     * 1e-15 from 0).
     */
    public static int partialPivot(double[][] mat, int col, int startRow) throws SolvingException {
        assert 0 <= col && col < mat[0].length;
        assert 0 <= startRow && startRow < mat.length;

        int maxIdx = startRow;
        for (int i = startRow + 1; i < mat.length; i++) {
            if (Math.abs(mat[i][col]) > Math.abs(mat[maxIdx][col])) {
                maxIdx = i;
            }
        }
        if (Math.abs(mat[maxIdx][col]) < 1e-15) {
            throw new SolvingException("No pivot in column " + col + " of input matrix");
        }
        return maxIdx;
    }

    /**
     * Swaps rows `row1` and `row2` in the matrix `mat`. Requires 0 <= row1 < mat.length,
     * 0 <= row2 < mat.length, and mat[row1].length == mat[row2].length.
     */
    public static void swapRows(double[][] mat, int row1, int row2) {
        assert 0 <= row1 && row1 < mat.length;
        assert 0 <= row2 && row2 < mat.length;
        assert mat[row1].length == mat[row2].length;

        for (int j = 0; j < mat[row1].length; j++) {
            double temp = mat[row1][j];
            mat[row1][j] = mat[row2][j];
            mat[row2][j] = temp;
        }
    }

    /**
     * Scales all entries in row `row` of matrix `mat` by `scaleFactor`. Requires
     * 0 <= row < mat.length.
     */
    public static void scaleRow(double[][] mat, int row, double scaleFactor) {
        for (int j  = 0; j < mat[row].length; j++) {
            mat[row][j] = scaleFactor * mat[row][j];
        }
    }

    /**
     * Adds multiples of `row` to lower rows to zero out the entries in column `pivotCol` below
     * `row`. Assumes that all entries to the left of `pivotCol` in `row` are 0 and
     * that mat[row][pivotCol] == 1. Requires `mat` is rectangular, 0 <= row < mat.length, and
     * 0 <= pivotCol < mat[0].length.
     */
    public static void eliminateBelow(double[][] mat, int row, int pivotCol) {
        assert 0 <= row && row < mat.length;
        assert 0 <= pivotCol && pivotCol < mat[0].length;

        for (int i = row + 1; i < mat.length; i++) {
            double scaleFactor = -1.0 * mat[i][pivotCol];
            for (int j = pivotCol; j < mat[0].length; j++) {
                mat[i][j] = mat[i][j] + scaleFactor * mat[row][j];
            }
        }
    }

    /**
     * Adds multiples of `row` to all rows except `row` to zero out the entries in column
     * `pivotCol`. Assumes that all entries to the left of `pivotCol` in `row` are 0 and that
     * mat[row][pivotCol] == 1. Requires `mat` is rectangular, 0 <= row < mat.length, and
     * 0 <= pivotCol < mat[0].length.
     */
    public static void eliminate(double[][] mat, int row, int pivotCol) {
        assert 0 <= row && row < mat.length;
        assert 0 <= pivotCol && pivotCol < mat[0].length;
        for (int i = 0; i < mat.length; i++) {
            if (i != row) {
                double scaleFactor = -1.0 * mat[i][pivotCol];
                for (int j = pivotCol; j < mat[0].length; j++) {
                    mat[i][j] = mat[i][j] + scaleFactor * mat[row][j];
                }
            }
        }
    }

    /**
     * Returns `num` rounded to `decimalPlaces` decimal places. Requires 0 <= decimalPlaces <= 15.
     */
    public static double round(double num, int decimalPlaces) {
        assert 0 <= decimalPlaces && decimalPlaces <= 15;

        double rounded = Math.round(num * Math.pow(10, decimalPlaces));
        rounded = rounded / Math.pow(10, decimalPlaces);
        return rounded;
    }
}
