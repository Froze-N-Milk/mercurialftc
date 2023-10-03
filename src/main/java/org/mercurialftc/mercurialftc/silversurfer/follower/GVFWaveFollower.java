package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.tracker.Tracker;

@SuppressWarnings("unused")
public class GVFWaveFollower extends WaveFollower {
	private final double translationFullOutputDistance, rotationFullOutputDistance;
	private final ArbFollower arbFollower;
	private final Tracker tracker;
	private double translationFunctionModifier, rotationFunctionModifier;
	private boolean inPosition;

	public GVFWaveFollower(@NotNull ArbFollower arbFollower, Tracker tracker) {
		super(arbFollower.getMotionConstants());
		this.arbFollower = arbFollower;
		this.tracker = tracker;

		this.translationFullOutputDistance = 400; // 400 mm
		this.rotationFullOutputDistance = Math.PI / 2; // 90 degrees

		this.translationFunctionModifier = 1;
		this.rotationFunctionModifier = 1;

		this.translationFunctionModifier = 1 / modifyTranslationError(translationFullOutputDistance);
		this.rotationFunctionModifier = 1 / modifyRotationError(rotationFullOutputDistance);
	}

	@Override
	protected void followOutput(@NotNull Followable.Output output, double loopTime) {
		Vector2D errorVector = output.getPosition().subtract(tracker.getPose2D()).toVector2D();

		Vector2D transformedTranslationVector = output.getTranslationVector();

		MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = arbFollower.getMotionConstants().makeDirectionOfTravelLimiter(errorVector.getHeading());

		Vector2D errorFeedback = Vector2D.fromPolar(modifyTranslationError(errorVector.getMagnitude()) * directionOfTravelLimiter.getVelocity(), errorVector.getHeading());

		directionOfTravelLimiter = arbFollower.getMotionConstants().makeDirectionOfTravelLimiter(transformedTranslationVector.getHeading());

		transformedTranslationVector = transformedTranslationVector.add(errorFeedback);
		transformedTranslationVector = Vector2D.fromPolar(Math.min(directionOfTravelLimiter.getVelocity(), transformedTranslationVector.getMagnitude()), transformedTranslationVector.getHeading());

		double transformedRotationalVelocity = output.getRotationalVelocity();
		double rotationalError = tracker.getPose2D().getTheta().findShortestDistance(output.getPosition().getTheta());

		transformedRotationalVelocity += modifyRotationError(rotationalError) * getMotionConstants().getMaxRotationalVelocity();

		transformedRotationalVelocity = Math.min(getMotionConstants().getMaxRotationalVelocity(), Math.max(-getMotionConstants().getMaxRotationalVelocity(), transformedRotationalVelocity));

		inPosition = errorVector.getMagnitude() < 5 && rotationalError < 0.025;

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
		return Math.max(0, Math.min((error / (Math.E * Math.sqrt(error) + translationFullOutputDistance)) * translationFunctionModifier, 1));
	}

	private double modifyRotationError(double error) {
		return Math.max(-1, Math.min((error / (Math.E * Math.sqrt(Math.abs(error)) + rotationFullOutputDistance)) * rotationFunctionModifier, 1));
	}

	@Override
	public boolean isFinished() {
		return super.isFinished() && inPosition;
	}
}
