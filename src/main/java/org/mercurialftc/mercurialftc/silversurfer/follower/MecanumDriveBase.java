package org.mercurialftc.mercurialftc.silversurfer.follower;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;
import org.mercurialftc.mercurialftc.scheduler.subsystems.Subsystem;
import org.mercurialftc.mercurialftc.scheduler.triggers.gamepadex.ContinuousInput;
import org.mercurialftc.mercurialftc.silversurfer.followable.MotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.followable.Wave;
import org.mercurialftc.mercurialftc.silversurfer.geometry.AngleRadians;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.tracker.Tracker;

public abstract class MecanumDriveBase extends Subsystem {
	protected final ContinuousInput x, y, t;
	protected final Pose2D startPose;
	protected DcMotorEx fl, bl, br, fr;
	protected VoltageSensor voltageSensor;
	protected WaveFollower waveFollower;
	protected MecanumArbFollower mecanumArbFollower;
	protected Tracker tracker;
	protected MotionConstants motionConstants;

	/**
	 * @param opModeEX  the opModeEX object to register against
	 * @param startPose the starting position
	 * @param x         the x controller
	 * @param y         the y controller
	 * @param t         the theta controller, positive turns clockwise
	 */
	public MecanumDriveBase(OpModeEX opModeEX, Pose2D startPose, ContinuousInput x, ContinuousInput y, ContinuousInput t) {
		super(opModeEX);
		this.startPose = startPose;
		this.x = x;
		this.y = y;
		this.t = t;
	}

	public abstract void initialiseConstants();

	@Override
	public void init() {
		initialiseConstants();

		tracker.reset(); // resets the encoders
		// sets the run modes
		fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
	}

	@Override
	public void periodic() {
		tracker.updatePose();
	}

	@Override
	public void defaultCommandExecute() {
		Vector2D translationVector = new Vector2D(x.getValue(), y.getValue());
		double scalingQuantity = Math.max(1, translationVector.getMagnitude()); // todo check this stuff
		translationVector = translationVector.scalarMultiply(1 / scalingQuantity).scalarMultiply(getMotionConstants().getMaxTranslationalVelocity());
		translationVector = translationVector.rotate(new AngleRadians(-tracker.getPose2D().getTheta().getRadians()));

		mecanumArbFollower.follow(
				translationVector,
				t.getValue() * getMotionConstants().getMaxRotationalVelocity()
		);
	}

	@Override
	public void close() {

	}

	/**
	 * an example command generator to set the robot to follow a prebuilt wave, for use during auto to prevent player inputs
	 * <p>requires {@link Command#queue()} to be called on this before it will run</p>
	 *
	 * @param wave the wave to follow
	 * @return the command to queue
	 */
	public Command followWave(Wave wave) {
		return new LambdaCommand()
				.addRequirements(this)
				.init(() -> waveFollower.setWave(wave))
				.execute(() -> waveFollower.update(opModeEX.getElapsedTime().seconds()))
				.finish(waveFollower::isFinished)
				.setInterruptable(true);
	}

	/**
	 * an example command generator to set the robot to follow a prebuilt wave, for use during teleop to allow player inputs to interrupt the movement
	 *
	 * @param wave the wave to follow
	 * @return the command to queue
	 */
	public Command followWaveInterruptible(Wave wave) {
		return new LambdaCommand()
				.addRequirements(this)
				.init(() -> waveFollower.setWave(wave))
				.execute(() -> waveFollower.update(opModeEX.getElapsedTime().seconds()))
				.finish(() -> waveFollower.isFinished() || x.getValue() != 0.0 || y.getValue() != 0.0 || t.getValue() != 0.0)
				.setInterruptable(true);
	}

	public Tracker getTracker() {
		return tracker;
	}

	public MotionConstants getMotionConstants() {
		return motionConstants;
	}

	public WaveFollower getWaveFollower() {
		return waveFollower;
	}

}
