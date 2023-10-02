import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.AngleDegrees;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.AngleRadians;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

public class VectorTests {
	@Test
	public void zeroRotation() {
		int iterations = 10 * 4; //must be a multiple of 4
		for (int i = 0; i < iterations; i++) {
			Vector2D input = Vector2D.fromPolar(1, new AngleDegrees(i * (360.0 / iterations)));
			Vector2D output = input.rotate(new AngleRadians(0));
			Assertions.assertEquals(input.getHeading().getRadians(), output.getHeading().getRadians());
			Assertions.assertEquals(input.getX(), output.getX());
			Assertions.assertEquals(input.getY(), output.getY());
			Assertions.assertEquals(input.getMagnitude(), output.getMagnitude());
		}
	}

	@Test
	public void halfRotation() {
		int iterations = 10 * 4; //must be a multiple of 4
		for (int i = 0; i < iterations; i++) {
			Vector2D input = Vector2D.fromPolar(1, new AngleDegrees(i * (360.0 / iterations)));
			Vector2D output = input.rotate(new AngleDegrees(180));
			Assertions.assertEquals(input.getHeading().add(new AngleDegrees(180)).getDegrees(), output.getHeading().getDegrees(), 0.001);
			Assertions.assertEquals(input.getMagnitude(), output.getMagnitude(), 0.001);
		}
	}
}
