package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.tracker.Tracker;

@SuppressWarnings("unused")
public class GVFWaveFollower extends WaveFollower {
	private final ArbFollower arbFollower;
	private final Tracker tracker;
	private boolean inPosition;
	private Vector2D accumulatedTranslationalError, previousTranslationalError;
	private double accumulatedRotationalError, previousRotationalError;

	public GVFWaveFollower(@NotNull ArbFollower arbFollower, Tracker tracker) {
		super(arbFollower.getMotionConstants());
		this.arbFollower = arbFollower;
		this.tracker = tracker;
		accumulatedRotationalError = previousRotationalError = 0;
		accumulatedTranslationalError = previousTranslationalError = new Vector2D(0, 0);
	}

	@Override
	protected void followOutput(@NotNull Followable.Output output, double loopTime) {
//		Pose2D velocityPose = tracker.getPose2D().subtract(tracker.getPreviousPose2D());
//		Vector2D velocityVector = new Vector2D(velocityPose.getX(), velocityPose.getY()).scalarMultiply(1 / loopTime);

		Pose2D errorPose = output.getPosition().subtract(tracker.getPose2D());
		Vector2D errorVector = new Vector2D(errorPose.getX(), errorPose.getY());

		accumulatedTranslationalError.add(errorVector.subtract(previousTranslationalError));

		Vector2D transformedTranslationVector = output.getTranslationVector();
		if (errorVector.getMagnitude() > 3) {
			transformedTranslationVector = transformedTranslationVector.add(accumulatedTranslationalError);
		}

		MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = arbFollower.getMotionConstants().makeDirectionOfTravelLimiter(transformedTranslationVector.getHeading());
		transformedTranslationVector = Vector2D.fromPolar(Math.min(directionOfTravelLimiter.getVelocity(), transformedTranslationVector.getMagnitude()), transformedTranslationVector.getHeading());


//		double targetVelocity = transformedTranslationVector.getMagnitude();
//		double maxVelocity = Math.min(directionOfTravelLimiter.getVelocity(), velocityVector.getMagnitude() + loopTime * directionOfTravelLimiter.getAcceleration());
//		double minVelocity = Math.max(0, velocityVector.getMagnitude() - loopTime * directionOfTravelLimiter.getAcceleration());

//		transformedTranslationVector = Vector2D.fromPolar(Math.max(minVelocity, Math.min(targetVelocity, maxVelocity)), transformedTranslationVector.getHeading());

		double transformedRotationalVelocity = output.getRotationalVelocity();
		double rotationalError = tracker.getPose2D().getTheta().findShortestDistance(output.getPosition().getTheta());
		double acceptableError = tracker.getPreviousPose2D().getTheta().findShortestDistance(tracker.getPose2D().getTheta());
//		double rotationalVelocity = acceptableError / loopTime;

		accumulatedRotationalError += rotationalError - previousRotationalError;

		if (Math.abs(rotationalError) > Math.abs(acceptableError)) {
			transformedRotationalVelocity += accumulatedRotationalError;
		}
		transformedRotationalVelocity = Math.min(getMotionConstants().getMaxRotationalVelocity(), Math.max(-getMotionConstants().getMaxRotationalVelocity(), transformedRotationalVelocity));

//		double maxRotationalVelocity = Math.min(getMotionConstants().getMaxRotationalVelocity(), rotationalVelocity + loopTime * getMotionConstants().getMaxRotationalAcceleration());
//		double minRotationalVelocity = Math.max(-getMotionConstants().getMaxRotationalVelocity(), rotationalVelocity - loopTime * getMotionConstants().getMaxRotationalAcceleration());

//		transformedRotationalVelocity = Math.max(minRotationalVelocity, Math.min(transformedRotationalVelocity, maxRotationalVelocity));

		inPosition = errorVector.getMagnitude() < 5 && rotationalError < 0.025;

		Followable.Output tranformedOutput = new Followable.Output(
				transformedTranslationVector,
				transformedRotationalVelocity,
				output.getCallbackTime(),
				output.getPosition(),
				output.getDestination()
		);
		arbFollower.followOutput(tranformedOutput, loopTime);

		previousTranslationalError = errorVector;
		previousRotationalError = rotationalError;
	}

	@Override
	public boolean isFinished() {
		return super.isFinished() && inPosition;
	}
}
