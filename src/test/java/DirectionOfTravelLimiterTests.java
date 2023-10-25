import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.AngleDegrees;

public class DirectionOfTravelLimiterTests {
	private MecanumMotionConstants mecanumMotionConstants;

	@BeforeEach
	public void init() {
		mecanumMotionConstants = new MecanumMotionConstants(
				2,
				1.5,
				1,
				1,
				1,
				1,
				1,
				1
		);
	}

	@Test
	public void test() {
		AngleDegrees testAngle = new AngleDegrees(0);
		MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter;

		directionOfTravelLimiter = mecanumMotionConstants.makeDirectionOfTravelLimiter(testAngle);
		Assertions.assertEquals(1.5, directionOfTravelLimiter.getVelocity(), 0.002);

		testAngle = testAngle.add(new AngleDegrees(45)).toAngleDegrees();

		directionOfTravelLimiter = mecanumMotionConstants.makeDirectionOfTravelLimiter(testAngle);
		Assertions.assertEquals(1, directionOfTravelLimiter.getVelocity(), 0.002);

		testAngle = testAngle.add(new AngleDegrees(45)).toAngleDegrees();

		directionOfTravelLimiter = mecanumMotionConstants.makeDirectionOfTravelLimiter(testAngle);
		Assertions.assertEquals(2, directionOfTravelLimiter.getVelocity(), 0.002);

		testAngle = testAngle.add(new AngleDegrees(45)).toAngleDegrees();

		directionOfTravelLimiter = mecanumMotionConstants.makeDirectionOfTravelLimiter(testAngle);
		Assertions.assertEquals(1, directionOfTravelLimiter.getVelocity(), 0.002);

		testAngle = testAngle.add(new AngleDegrees(45)).toAngleDegrees();

		directionOfTravelLimiter = mecanumMotionConstants.makeDirectionOfTravelLimiter(testAngle);
		Assertions.assertEquals(1.5, directionOfTravelLimiter.getVelocity(), 0.002);

		testAngle = testAngle.add(new AngleDegrees(45)).toAngleDegrees();

		directionOfTravelLimiter = mecanumMotionConstants.makeDirectionOfTravelLimiter(testAngle);
		Assertions.assertEquals(1, directionOfTravelLimiter.getVelocity(), 0.002);

		testAngle = testAngle.add(new AngleDegrees(45)).toAngleDegrees();

		directionOfTravelLimiter = mecanumMotionConstants.makeDirectionOfTravelLimiter(testAngle);
		Assertions.assertEquals(2, directionOfTravelLimiter.getVelocity(), 0.002);

		testAngle = testAngle.add(new AngleDegrees(45)).toAngleDegrees();

		directionOfTravelLimiter = mecanumMotionConstants.makeDirectionOfTravelLimiter(testAngle);
		Assertions.assertEquals(1, directionOfTravelLimiter.getVelocity(), 0.002);
	}
}
