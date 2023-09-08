package org.mercurialftc.mercurialftc.util.matrix;

public class SimpleMatrix {
	private final double[][] matrix;

	/* STRUCTURE:
	 * double x = matrix[row][column];
	 *
	 * double[] row = matrix[row];
	 */

	/**
	 * assumes that the rows are all the same length, if not, fills the empty spaces with 0
	 *
	 * @param matrix
	 */
	public SimpleMatrix(double[][] matrix) {
		int columns = matrix[0].length;
		boolean fix = false;

		for (double[] doubles : matrix) {
			if (doubles.length != columns) {
				columns = Math.max(columns, doubles.length);
				fix = true;
			}
		}
		if (fix) {
			formatMatrix(matrix, columns);
		}

		this.matrix = matrix;
	}

	/**
	 * for internal use, saves time if the matrix is guaranteed to have a consistent number of columns
	 *
	 * @param matrix
	 * @param safeOrigin
	 */
	private SimpleMatrix(double[][] matrix, boolean safeOrigin) {
		if (!safeOrigin) {
			int columns = matrix[0].length;
			boolean fix = false;

			for (double[] doubles : matrix) {
				if (doubles.length != columns) {
					columns = Math.max(columns, doubles.length);
					fix = true;
				}
			}
			if (fix) {
				formatMatrix(matrix, columns);
			}
		}
		this.matrix = matrix;
	}

	private void formatMatrix(double[][] matrix, int columns) {
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[i].length != columns) {
				double[] temp = new double[columns];
				System.arraycopy(matrix[i], 0, temp, 0, matrix[i].length);
				matrix[i] = temp;
			}
		}
	}

	/**
	 * <p>non-mutating</p>
	 * throws a runtime exception if the matrices are mismatched and the operation cannot be performed
	 *
	 * @param other
	 * @return
	 */
	public SimpleMatrix multiply(SimpleMatrix other) {
		if (this.columns() != other.rows()) {
			throw new RuntimeException("matrix 1 does not have the same number of columns as matrix 2 has rows");
		}

		double[][] result = new double[this.rows()][other.columns()];

		for (int i = 0; i < result.length; i++) {
			double[] row = result[i];
			for (int j = 0; j < row.length; j++) {
				double dotproduct = 0;
				for (int k = 0; k < this.columns(); k++) {
					dotproduct += this.matrix[i][k] * other.matrix[k][j];
				}
				result[i][j] = dotproduct;
			}
		}

		return new SimpleMatrix(result, true);
	}

	/**
	 * non-mutating
	 *
	 * @param scalar
	 * @return
	 */
	public SimpleMatrix scalarMultiply(double scalar) {
		double[][] result = matrix.clone();

		for (int i = 0; i < result.length; i++) {
			double[] row = result[i];
			for (int j = 0; j < row.length; j++) {
				result[i][j] *= scalar;
			}
		}

		return new SimpleMatrix(result, true);
	}

	public double[][] getMatrix() {
		return matrix;
	}

	public double getItem(int row, int column) {
		return matrix[row][column];
	}

	/**
	 * non-mutating
	 *
	 * @param row
	 * @param column
	 * @param input  new value
	 * @return
	 */
	public SimpleMatrix setItem(int row, int column, double input) {
		double[][] result = matrix.clone();
		result[row][column] = input;
		return new SimpleMatrix(result, true);
	}

	public int rows() {
		return matrix.length;
	}

	public int columns() {
		return matrix[0].length;
	}
}
