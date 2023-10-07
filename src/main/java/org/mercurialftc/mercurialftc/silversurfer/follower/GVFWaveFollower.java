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

		if (errorVector.getMagnitude() > 2.5) {
			errorDirectionOfTravelLimiter = arbFollower.getMotionConstants().makeDirectionOfTravelLimiter(errorVector.getHeading());

			Vector2D errorFeedback = Vector2D.fromPolar(modifyTranslationError(errorVector.getMagnitude()) * errorDirectionOfTravelLimiter.getVelocity(), errorVector.getHeading());

			transformedTranslationVector = transformedTranslationVector.add(errorFeedback);

			MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = arbFollower.getMotionConstants().makeDirectionOfTravelLimiter(transformedTranslationVector.getHeading());
			transformedTranslationVector = Vector2D.fromPolar(Math.min(directionOfTravelLimiter.getVelocity(), transformedTranslationVector.getMagnitude()), transformedTranslationVector.getHeading());
		}
		double transformedRotationalVelocity = output.getRotationalVelocity();
		double rotationalError = tracker.getPose2D().getTheta().findShortestDistance(output.getPosition().getTheta());

		if (Math.abs(rotationalError) > 0.035) {
			transformedRotationalVelocity += modifyRotationError(rotationalError) * getMotionConstants().getMaxRotationalVelocity();

			transformedRotationalVelocity = Math.min(getMotionConstants().getMaxRotationalVelocity(), Math.max(-getMotionConstants().getMaxRotationalVelocity(), transformedRotationalVelocity));
		}

		inPosition = errorVector.getMagnitude() < 5 && rotationalError < 0.035;

		Followable.Output tranformedOutput = new Followable.Output(
				transformedTranslationVector,
				transformedRotationalVelocity,
				output.getCallbackTime(),
				output.getPosition(),
				output.getDestination()
		);
		arbFollower.followOutput(tranformedOutput, loopTime);
	}

	private double modifyTranslationError(double error) {
		double output = Math.sqrt(error / errorDirectionOfTravelLimiter.getVelocity());
		double deltaError = tracker.getPose2D().toVector2D().subtract(tracker.getPreviousPose2D().toVector2D()).getMagnitude();
		output -= Math.max(0, (deltaError * deltaError) / errorDirectionOfTravelLimiter.getVelocity());
		return Math.max(0, Math.min(output, 1));
	}

	private double modifyRotationError(double error) {
		double output = (Math.sqrt(Math.abs(error)) / rotationLimiter) * Math.signum(error);
		double deltaError = tracker.getPose2D().getTheta().findShortestDistance(tracker.getPreviousPose2D().getTheta());
		output -= (deltaError) / getMotionConstants().getMaxRotationalVelocity();
		return Math.max(-1, Math.min(output, 1));
	}

	@Override
	public boolean isFinished() {
		return super.isFinished() && inPosition;
	}
}
