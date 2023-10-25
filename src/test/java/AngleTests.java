import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.Angle;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.AngleDegrees;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.AngleRadians;

public class AngleTests {
	@Test
	void zeroAngle() {
		Angle angle1 = new AngleRadians(0);
		Angle angle2 = new AngleDegrees(0);

		Assertions.assertEquals(0, angle1.getRadians());
		Assertions.assertEquals(0, angle2.getRadians());
		Assertions.assertEquals(0, angle1.getDegrees());
		Assertions.assertEquals(0, angle2.getDegrees());
	}
}
