package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.EmptyObstacleMap;
import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.ObstacleMap;
import org.mercurialftc.mercurialftc.silversurfer.tracker.Tracker;

@SuppressWarnings("unused")
public class GVFWaveFollower extends AbstractWaveFollower {
	private final WaveFollower waveFollower;
	private final Tracker tracker;
	private final double rotationLimiter;
	private final MecanumMotionConstants mecanumMotionConstants;
	private boolean inPosition;
	private MecanumMotionConstants.DirectionOfTravelLimiter errorDirectionOfTravelLimiter;
	private double previousTranslationError, previousRotationError;

	public GVFWaveFollower(@NotNull WaveFollower waveFollower, @NotNull MecanumMotionConstants mecanumMotionConstants, @NotNull Tracker tracker, @NotNull ObstacleMap obstacleMap) {
		this.waveFollower = waveFollower;
		this.mecanumMotionConstants = mecanumMotionConstants;
		this.tracker = tracker;

		rotationLimiter = Math.sqrt(mecanumMotionConstants.getMaxRotationalVelocity());
	}

	public GVFWaveFollower(@NotNull WaveFollower waveFollower, @NotNull MecanumMotionConstants mecanumMotionConstants, @NotNull Tracker tracker) {
		this(waveFollower, mecanumMotionConstants, tracker, new EmptyObstacleMap());
	}

	@Override
	public void followOutput(@NotNull Followable.Output output, double loopTime) {
		Vector2D errorVector = output.getPosition().subtract(tracker.getPose2D()).toVector2D();

		Vector2D transformedTranslationVector = output.getTranslationVector();

		double errorVectorMagnitude = errorVector.getMagnitude();

		if (errorVectorMagnitude > 2) {
			errorDirectionOfTravelLimiter = mecanumMotionConstants.makeDirectionOfTravelLimiter(errorVector.getHeading());

			Vector2D errorFeedback = Vector2D.fromPolar(modifyTranslationError(errorVectorMagnitude, errorVectorMagnitude - previousTranslationError, loopTime) * errorDirectionOfTravelLimiter.getVelocity(), errorVector.getHeading());

			transformedTranslationVector = transformedTranslationVector.add(errorFeedback);

			MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = mecanumMotionConstants.makeDirectionOfTravelLimiter(transformedTranslationVector.getHeading());
			transformedTranslationVector = Vector2D.fromPolar(Math.min(directionOfTravelLimiter.getVelocity(), transformedTranslationVector.getMagnitude()), transformedTranslationVector.getHeading());
		}
		double transformedRotationalVelocity = output.getRotationalVelocity();
		double rotationalError = tracker.getPose2D().getTheta().findShortestDistance(output.getPosition().getTheta());

		if (Math.abs(rotationalError) > 0.035) {
			transformedRotationalVelocity += modifyRotationError(rotationalError, rotationalError - previousRotationError, loopTime) * mecanumMotionConstants.getMaxRotationalVelocity();

			transformedRotationalVelocity = Math.min(mecanumMotionConstants.getMaxRotationalVelocity(), Math.max(-mecanumMotionConstants.getMaxRotationalVelocity(), transformedRotationalVelocity));
		}

		inPosition = errorVectorMagnitude < 5 && rotationalError < 0.035;

		Followable.Output tranformedOutput = new Followable.Output(
				transformedTranslationVector,
				transformedRotationalVelocity,
				output.getCallbackTime(),
				output.getPosition(),
				output.getDestination()
		);
		waveFollower.followOutput(tranformedOutput, loopTime);

		previousRotationError = rotationalError;
		previousTranslationError = errorVectorMagnitude;
	}

	private double modifyTranslationError(double error, double deltaError, double loopTime) {
		double output = Math.sqrt(error / errorDirectionOfTravelLimiter.getVelocity());
		output += ((deltaError * -deltaError) / loopTime) / errorDirectionOfTravelLimiter.getVelocity();
		return Math.max(0, Math.min(output, 1));
	}

	private double modifyRotationError(double error, double deltaError, double loopTime) {
		double output = (Math.sqrt(Math.abs(error)) / rotationLimiter) * Math.signum(error);
		output += (deltaError / loopTime) / mecanumMotionConstants.getMaxRotationalVelocity();
		return Math.max(-1, Math.min(output, 1));
	}

	@Override
	public boolean isFinished() {
		return super.isFinished() && inPosition;
	}
}
