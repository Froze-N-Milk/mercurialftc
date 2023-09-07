package org.mercurialftc.mercurialftc.silversurfer.follower;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.followable.MotionConstants;
import org.mercurialftc.mercurialftc.util.matrix.SimpleMatrix;

public class MecanumArbFollower extends ArbFollower {
	private final DcMotorEx fl, bl, br, fr;
	private final SimpleMatrix transformMatrix, scalingMatrix;

	/**
	 * an arbitrary feed forward output follower, does not care about any value in output other than
	 *
	 * @param motionConstants motion constants to be used for scaling to power outputs
	 * @param fl              front left motor
	 * @param bl              back left motor
	 * @param br              back right motor
	 * @param fr              front right motor
	 */
	public MecanumArbFollower(MotionConstants motionConstants, DcMotorEx fl, DcMotorEx bl, DcMotorEx br, DcMotorEx fr) {
		super(motionConstants);
		this.fl = fl;
		this.bl = bl;
		this.br = br;
		this.fr = fr;

		transformMatrix = new SimpleMatrix(
				new double[][]{
						{1, -1, -1},
						{1, 1, -1},
						{1, -1, 1},
						{1, 1, 1}
				}
		);

		scalingMatrix = new SimpleMatrix(
				new double[][]{
						{1 / getMotionConstants().getMaxTranslationalVelocity(), 0, 0},
						{0, 1 / getMotionConstants().getMaxTranslationalVelocity(), 0},
						{0, 0, 1 / getMotionConstants().getMaxRotationalVelocity()}
				}
		);
	}

	@Override
	public void follow(Vector2D translationVector, double rotationalVelocity) {
		SimpleMatrix inputValues = new SimpleMatrix(
				new double[][]{
						{translationVector.getX()},
						{translationVector.getY()},
						{rotationalVelocity}
				}
		);

		SimpleMatrix outputMatrix = transformMatrix.multiply(scalingMatrix.multiply(inputValues));

		fl.setPower(outputMatrix.getItem(0, 0));
		bl.setPower(outputMatrix.getItem(1, 0));
		br.setPower(outputMatrix.getItem(2, 0));
		fr.setPower(outputMatrix.getItem(3, 0));
	}
}
