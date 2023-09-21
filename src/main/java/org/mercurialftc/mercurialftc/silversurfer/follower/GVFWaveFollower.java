package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Angle;
import org.mercurialftc.mercurialftc.silversurfer.geometry.AngleRadians;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.tracker.Tracker;

/**
 * Consumes {@link org.mercurialftc.mercurialftc.silversurfer.followable.Followable.Output}s from the wave and then modifies them for use in an {@link  ArbFollower} such as {@link MecanumArbFollower}
 */
public class GVFWaveFollower extends WaveFollower {
	private final Tracker tracker;
	private final ArbFollower arbFollower;
	private Pose2D targetPose, currentPose;

	public GVFWaveFollower(Tracker tracker, ArbFollower arbFollower) {
		super(arbFollower.getMotionConstants());
		this.tracker = tracker;
		this.arbFollower = arbFollower;
	}

	public ArbFollower getArbFollower() {
		return arbFollower;
	}

	@Override
	protected void followOutput(Followable.Output output, double loopTime) {
		currentPose = tracker.getPose2D();
		Pose2D previousPose = tracker.getPreviousPose2D();

		targetPose = output.getPosition();

		Vector2D translationalDifference = new Vector2D(targetPose.getX() - currentPose.getX(), targetPose.getY() - currentPose.getY());
		Angle modifiedTranslationVectorAngle = translationalDifference.add(output.getTranslationVector()).getHeading();

		Vector2D currentVelocity = new Vector2D(currentPose.getX() - previousPose.getX(), currentPose.getY() - previousPose.getY());

		double translationalError = new Vector2D(targetPose.getX() - currentPose.getX(), targetPose.getY() - currentPose.getY()).getMagnitude();

		double translationalBreakDistance = (currentVelocity.getMagnitude() * currentVelocity.getMagnitude()) / (2 * getMotionConstants().getMaxTranslationalVelocity());

		int translationalBreakControl = (int) Math.signum(Math.abs(translationalError) - translationalBreakDistance);

		double modifiedTranslationVectorMagnitude = currentVelocity.getMagnitude() + translationalBreakControl * getMotionConstants().getMaxTranslationalAcceleration() * loopTime;
		modifiedTranslationVectorMagnitude = Math.max(modifiedTranslationVectorMagnitude, getMotionConstants().getMaxTranslationalVelocity());

		Vector2D modifiedTranslationalVector = Vector2D.fromPolar(modifiedTranslationVectorMagnitude, modifiedTranslationVectorAngle);
		modifiedTranslationalVector = modifiedTranslationalVector.rotate(new AngleRadians(-currentPose.getTheta().getRadians()));

		double rotationalError = currentPose.getTheta().findShortestDistance(targetPose.getTheta()); //shortest distance from estimated current position to target position

		double rotationalVelocity = previousPose.getTheta().findShortestDistance(currentPose.getTheta());

		double rotationalBreakDistance = (rotationalVelocity * rotationalVelocity) / (2 * getMotionConstants().getMaxRotationalAcceleration());

		int rotationalBreakControl = (int) Math.signum(Math.abs(rotationalError) - rotationalBreakDistance);

		double modifiedRotationalVelocity = rotationalVelocity + getMotionConstants().getMaxRotationalAcceleration() * Math.signum(rotationalError) * rotationalBreakControl * loopTime;
		modifiedRotationalVelocity = Math.min(modifiedRotationalVelocity, getMotionConstants().getMaxRotationalVelocity());

		arbFollower.follow(modifiedTranslationalVector, modifiedRotationalVelocity);
	}

	@Override
	public boolean isFinished() {
		double translationalError = new Vector2D(targetPose.getX() - currentPose.getX(), targetPose.getY() - currentPose.getY()).getMagnitude();
		return super.isFinished() && translationalError < 15;
	}
}
