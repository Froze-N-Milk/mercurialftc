package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.EmptyObstacleMap;
import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.ObstacleMap;
import org.mercurialftc.mercurialftc.silversurfer.tracker.Tracker;

@SuppressWarnings("unused")
public class GVFWaveFollower extends WaveFollower {
	private final ArbFollower arbFollower;
	private final Tracker tracker;
	private final double rotationLimiter;
	private boolean inPosition;
	private MecanumMotionConstants.DirectionOfTravelLimiter errorDirectionOfTravelLimiter;
	private double previousTranslationError, previousRotationError;

	public GVFWaveFollower(@NotNull ArbFollower arbFollower, @NotNull Tracker tracker, @NotNull ObstacleMap obstacleMap) {
		super(arbFollower.getMotionConstants());
		this.arbFollower = arbFollower;
		this.tracker = tracker;

		rotationLimiter = Math.sqrt(getMotionConstants().getMaxRotationalVelocity());
	}

	public GVFWaveFollower(@NotNull ArbFollower arbFollower, @NotNull Tracker tracker) {
		this(arbFollower, tracker, new EmptyObstacleMap());
	}

	@Override
	protected void followOutput(@NotNull Followable.Output output, double loopTime) {
		Vector2D errorVector = output.getPosition().subtract(tracker.getPose2D()).toVector2D();

		Vector2D transformedTranslationVector = output.getTranslationVector();

		double errorVectorMagnitude = errorVector.getMagnitude();

		if (errorVectorMagnitude > 2) {
			errorDirectionOfTravelLimiter = arbFollower.getMotionConstants().makeDirectionOfTravelLimiter(errorVector.getHeading());

			Vector2D errorFeedback = Vector2D.fromPolar(modifyTranslationError(errorVectorMagnitude, errorVectorMagnitude - previousTranslationError, loopTime) * errorDirectionOfTravelLimiter.getVelocity(), errorVector.getHeading());

			transformedTranslationVector = transformedTranslationVector.add(errorFeedback);

			MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = arbFollower.getMotionConstants().makeDirectionOfTravelLimiter(transformedTranslationVector.getHeading());
			transformedTranslationVector = Vector2D.fromPolar(Math.min(directionOfTravelLimiter.getVelocity(), transformedTranslationVector.getMagnitude()), transformedTranslationVector.getHeading());
		}
		double transformedRotationalVelocity = output.getRotationalVelocity();
		double rotationalError = tracker.getPose2D().getTheta().findShortestDistance(output.getPosition().getTheta());

		if (Math.abs(rotationalError) > 0.035) {
			transformedRotationalVelocity += modifyRotationError(rotationalError, rotationalError - previousRotationError, loopTime) * getMotionConstants().getMaxRotationalVelocity();

			transformedRotationalVelocity = Math.min(getMotionConstants().getMaxRotationalVelocity(), Math.max(-getMotionConstants().getMaxRotationalVelocity(), transformedRotationalVelocity));
		}

		inPosition = errorVectorMagnitude < 5 && rotationalError < 0.035;

		Followable.Output tranformedOutput = new Followable.Output(
				transformedTranslationVector,
				transformedRotationalVelocity,
				output.getCallbackTime(),
				output.getPosition(),
				output.getDestination()
		);
		arbFollower.followOutput(tranformedOutput, loopTime);

		previousRotationError = rotationalError;
		previousTranslationError = errorVectorMagnitude;
	}

	private double modifyTranslationError(double error, double deltaError, double loopTime) {
		double output = Math.sqrt(error / errorDirectionOfTravelLimiter.getVelocity());
		output += ((deltaError * deltaError) / loopTime) / errorDirectionOfTravelLimiter.getVelocity();
		return Math.max(0, Math.min(output, 1));
	}

	private double modifyRotationError(double error, double deltaError, double loopTime) {
		double output = (Math.sqrt(Math.abs(error)) / rotationLimiter) * Math.signum(error);
		output += (deltaError / loopTime) / getMotionConstants().getMaxRotationalVelocity();
		return Math.max(-1, Math.min(output, 1));
	}

	@Override
	public boolean isFinished() {
		return super.isFinished() && inPosition;
	}
}
