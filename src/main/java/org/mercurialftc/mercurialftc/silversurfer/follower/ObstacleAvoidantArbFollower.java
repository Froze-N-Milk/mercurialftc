package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.Angle;
import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.ObstacleMap;
import org.mercurialftc.mercurialftc.silversurfer.tracker.Tracker;

@SuppressWarnings("unused")
public class ObstacleAvoidantArbFollower extends ArbFollower {
	private final ArbFollower arbFollower;
	private final Tracker tracker;
	private final ObstacleMap obstacleMap;

	public ObstacleAvoidantArbFollower(@NotNull ArbFollower arbFollower, Tracker tracker, ObstacleMap obstacleMap) {
		super(arbFollower.getMotionConstants());
		this.arbFollower = arbFollower;
		this.tracker = tracker;
		this.obstacleMap = obstacleMap;
	}

	@Override
	public void followOutput(@NotNull Followable.Output output, double loopTime) {
		Vector2D obstacleVector = obstacleMap.closestObstacleVector(tracker.getPose2D());
		Vector2D translationVector = tracker.getTranslationVector();

		Vector2D transformedVector = output.getTranslationVector();

		if (obstacleVector != null) {
			Angle obstacleHeading = obstacleVector.getHeading();

			Vector2D vectorTowardsObstacle = Vector2D.fromPolar(translationVector.scalarMultiply(1 / loopTime).dot(obstacleVector.getUnitVector()), obstacleHeading);

			MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = getMotionConstants().makeDirectionOfTravelLimiter(obstacleHeading);

			double allowableVelocity = Math.sqrt(2 * directionOfTravelLimiter.getAcceleration() * obstacleVector.getMagnitude());

			double correctionMagnitude = Math.min(0, allowableVelocity - vectorTowardsObstacle.getMagnitude());

			transformedVector = transformedVector.add(Vector2D.fromPolar(correctionMagnitude, obstacleHeading));
		}

		arbFollower.followOutput(
				new Followable.Output(
						transformedVector,
						output.getRotationalVelocity(),
						output.getCallbackTime(),
						output.getPosition(),
						output.getDestination()
				),
				loopTime
		);
	}

	@Override
	public void follow(Vector2D translationVector, double rotationalVelocity) {
		Vector2D obstacleVector = obstacleMap.closestObstacleVector(tracker.getPose2D());

		if (obstacleVector != null) {
			Angle obstacleHeading = obstacleVector.getHeading();

			MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = getMotionConstants().makeDirectionOfTravelLimiter(obstacleHeading);

			Vector2D vectorTowardsObstacle = Vector2D.fromPolar(translationVector.scalarMultiply(directionOfTravelLimiter.getVelocity()).dot(obstacleVector.getUnitVector()), obstacleHeading);

			double allowableVelocity = Math.sqrt(2 * directionOfTravelLimiter.getAcceleration() * obstacleVector.getMagnitude());

			double correctionMagnitude = Math.min(0, allowableVelocity - vectorTowardsObstacle.getMagnitude());

			correctionMagnitude /= directionOfTravelLimiter.getVelocity();

			translationVector = translationVector.add(Vector2D.fromPolar(correctionMagnitude, obstacleHeading));
		}

		arbFollower.follow(translationVector, rotationalVelocity);
	}

}
