package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Angle;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.tracker.Tracker;

/**
 * wrapper class for over {@link FFMecanumWaveFollower} or your own implementation, modifies the output and feeds it back in
 */
public class GVFWaveFollower extends WaveFollower {
	private final Tracker tracker;
	private final WaveFollower waveFollower;

	public GVFWaveFollower(Tracker tracker, WaveFollower waveFollower) {
		super(waveFollower.getMotionConstants());
		this.tracker = tracker;
		this.waveFollower = waveFollower;
	}

	@Override
	protected void followOutput(Followable.Output output, double loopTime) {
		Pose2D currentPose = tracker.getPose2D();
		Pose2D previousPose = tracker.getPreviousPose2D();

		Pose2D targetPose = output.getPosition();

		Vector2D translationalDifference = new Vector2D(targetPose.getX() - currentPose.getX(), targetPose.getY() - currentPose.getY());
		Angle modifiedTranslationVectorAngle = translationalDifference.add(output.getTranslationVector()).getHeading();

		Vector2D currentVelocity = new Vector2D(currentPose.getX() - previousPose.getX(), currentPose.getY() - previousPose.getY());

		double translationalError = new Vector2D(targetPose.getX() - currentPose.getX(), targetPose.getY() - currentPose.getY()).getMagnitude();

		double translationalBreakDistance = (currentVelocity.getMagnitude() * currentVelocity.getMagnitude()) / (2 * getMotionConstants().getMaxTranslationalVelocity());

		int translationalBreakControl = (int) Math.signum(Math.abs(translationalError) - translationalBreakDistance);

		double modifiedTranslationVectorMagnitude = currentVelocity.getMagnitude() + translationalBreakControl * getMotionConstants().getMaxTranslationalAcceleration() * loopTime;
		modifiedTranslationVectorMagnitude = Math.max(modifiedTranslationVectorMagnitude, getMotionConstants().getMaxTranslationalVelocity());

		Vector2D modifiedTranslationalVector = Vector2D.fromPolar(modifiedTranslationVectorMagnitude, modifiedTranslationVectorAngle);
		modifiedTranslationalVector = modifiedTranslationalVector.rotate(currentPose.getTheta());

		double rotationalError = currentPose.getTheta().findShortestDistance(targetPose.getTheta()); //shortest distance from estimated current position to target position

		double rotationalVelocity = previousPose.getTheta().findShortestDistance(currentPose.getTheta());

		double rotationalBreakDistance = (rotationalVelocity * rotationalVelocity) / (2 * getMotionConstants().getMaxRotationalAcceleration());

		int rotationalBreakControl = (int) Math.signum(Math.abs(rotationalError) - rotationalBreakDistance);

		double modifiedRotationalVelocity = rotationalVelocity + getMotionConstants().getMaxRotationalAcceleration() * Math.signum(rotationalError) * rotationalBreakControl * loopTime;
		modifiedRotationalVelocity = Math.max(modifiedRotationalVelocity, getMotionConstants().getMaxRotationalVelocity());

		Followable.Output modifiedOutput = new Followable.Output(
				modifiedTranslationalVector,
				modifiedRotationalVelocity,
				output.getCallbackTime(),
				output.getPosition(),
				output.getDestination()
		);

		waveFollower.followOutput(modifiedOutput, loopTime);
	}
}
