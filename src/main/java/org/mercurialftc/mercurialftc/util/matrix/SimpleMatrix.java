package org.mercurialftc.mercurialftc.util.matrix;

public class SimpleMatrix {
	private final double[][] matrix;
	
	/* STRUCTURE:
	 * double x = matrix[row][column];
	 *
	 * double[] row = matrix[row];
	 */
	
	/**
	 * assumes that the rows are all of the same length, if not, fills the empty spaces with 0
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
		if(fix) {
			formatMatrix(matrix, columns);
		}
		
		this.matrix = matrix;
	}
	
	/**
	 * for internal use, saves time if the matrix is guaranteed to have a consistent number of columns
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
	
	private void formatMatrix(double[][] matrix, int columns){
		for (int i = 0; i < matrix.length; i++) {
			if(matrix[i].length != columns) {
				double[] temp = new double[columns];
				System.arraycopy(matrix[i], 0, temp, 0, matrix[i].length);
				matrix[i] = temp;
			}
		}
	}
	
	/**
	 * returns an empty matrix if the matrices are mismatched, and so cannot be multiplied
	 * @param other
	 * @return
	 */
	public SimpleMatrix multiply(SimpleMatrix other) {
		if(this.columns() != other.rows()) {
			return new SimpleMatrix(new double[][]{}, true);
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
	
	public double[][] getMatrix() {
		return matrix;
	}
	
	public double getItem(int row, int column) {
		return matrix[row][column];
	}
	
	public int rows() {
		return matrix.length;
	}
	
	public int columns() {
		return matrix[0].length;
	}
}
