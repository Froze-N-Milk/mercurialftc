import org.junit.jupiter.api.Test;
import org.mercurialftc.mercurialftc.util.matrix.SimpleMatrix;

import java.util.Arrays;

public class MatrixTests {
	@Test
	void maximumMotorAngularVelocity() {
		SimpleMatrix transformMatrix = new SimpleMatrix( // matrix used for controlling the mecanum drive base
				new double[][]{
						{1, -1, -1},
						{1, 1, -1},
						{1, -1, 1},
						{1, 1, 1}
				}
		);

		SimpleMatrix inputValues = new SimpleMatrix(
				new double[][]{
						{50},
						{0},
						{0}
				}
		);

		SimpleMatrix scaling = new SimpleMatrix(
				new double[][]{
						{1 / 50.0, 0, 0},
						{0, 1 / 50.0, 0},
						{0, 0, 1 / 6.0}
				}
		);

		SimpleMatrix scaled = scaling.multiply(inputValues);

		System.out.println(Arrays.deepToString(scaled.getMatrix()));

		SimpleMatrix outputMatrix = transformMatrix.multiply(scaled);

		System.out.println(outputMatrix.getItem(0, 0));
		System.out.println(outputMatrix.getItem(1, 0));
		System.out.println(outputMatrix.getItem(2, 0));
		System.out.println(outputMatrix.getItem(3, 0));
	}
}
