package org.mercurialftc.mercurialftc.silversurfer.follower;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.followable.MotionConstants;
import org.mercurialftc.mercurialftc.util.matrix.SimpleMatrix;

public class FFMecanumWaveFollower extends WaveFollower {
	private final DcMotorEx fl, bl, br, fr;
	private final SimpleMatrix transformMatrix;
	private final boolean preRotated;

	/**
	 * @param motionConstants motion constants to be used for scaling to power outputs
	 * @param preRotated      set true if the outputs being given to {@link #followOutput(Followable.Output, double)} will have already been rotated to account for the heading of the robot
	 * @param trackwidth      distance between opposing wheels, not the same as the lateral distance for tracker constants
	 * @param wheelbase       distance between adjacent wheels
	 * @param fl              front left motor
	 * @param bl              back left motor
	 * @param br              back right motor
	 * @param fr              front right motor
	 */
	public FFMecanumWaveFollower(MotionConstants motionConstants, boolean preRotated, double trackwidth, double wheelbase, DcMotorEx fl, DcMotorEx bl, DcMotorEx br, DcMotorEx fr) {
		super(motionConstants);
		this.preRotated = preRotated;
		this.fl = fl;
		this.bl = bl;
		this.br = br;
		this.fr = fr;
		double l = trackwidth / 2;
		double b = wheelbase / 2;

		transformMatrix = new SimpleMatrix(
				new double[][]{
						{1, -1, -(l + b)},
						{1, 1, -(l + b)},
						{1, -1, (l + b)},
						{1, 1, (l + b)}
				}
		);
	}

	@Override
	protected void followOutput(Followable.Output output, double loopTime) {
		Vector2D scaledTranslationVector = output.getTranslationVector().scalarMultiply(1 / getMotionConstants().getMaxTranslationalVelocity());
		if (!preRotated) {
			scaledTranslationVector = scaledTranslationVector.rotate(output.getPosition().getTheta());
		}

		double scaledAngularVelocity = output.getAngularVelocity() / getMotionConstants().getMaxAngularVelocity();

		SimpleMatrix inputValues = new SimpleMatrix(
				new double[][]{
						{scaledTranslationVector.getX()},
						{scaledTranslationVector.getY()},
						{scaledAngularVelocity}
				}
		);

		SimpleMatrix outputMatrix = transformMatrix.multiply(inputValues);

		fl.setPower(outputMatrix.getItem(0, 0));
		bl.setPower(outputMatrix.getItem(1, 0));
		br.setPower(outputMatrix.getItem(2, 0));
		fr.setPower(outputMatrix.getItem(3, 0));
	}
}
