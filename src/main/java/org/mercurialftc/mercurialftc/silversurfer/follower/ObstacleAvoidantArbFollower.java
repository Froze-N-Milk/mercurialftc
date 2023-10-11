package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.ObstacleMap;
import org.mercurialftc.mercurialftc.silversurfer.tracker.Tracker;

@SuppressWarnings("unused")
public class ObstacleAvoidantArbFollower extends ArbFollower {
	private final ArbFollower arbFollower;
	private final Tracker tracker;
	private final ObstacleMap obstacleMap;
	private MecanumMotionConstants.DirectionOfTravelLimiter obstacleAvoidanceDirectionOfTravelLimiter;
	private double previousObstacleAvoidanceVectorMagnitude;

	public ObstacleAvoidantArbFollower(@NotNull ArbFollower arbFollower, Tracker tracker, ObstacleMap obstacleMap) {
		super(arbFollower.getMotionConstants());
		this.arbFollower = arbFollower;
		this.tracker = tracker;
		this.obstacleMap = obstacleMap;
	}

	@Override
	protected void followOutput(@NotNull Followable.Output output, double loopTime) {
		Vector2D obstacleAvoidanceVector = obstacleMap.obstacleAvoidanceVector(tracker.getPose2D());

		Vector2D transformedTranslationVector = output.getTranslationVector();

		if (obstacleAvoidanceVector != null) {
			obstacleAvoidanceDirectionOfTravelLimiter = arbFollower.getMotionConstants().makeDirectionOfTravelLimiter(obstacleAvoidanceVector.getHeading());

			double obstacleAvoidanceVectorMagnitude = obstacleAvoidanceVector.getMagnitude();

			Vector2D obstacleFeedback = Vector2D.fromPolar(modifyObstacleAvoidance(obstacleAvoidanceVectorMagnitude, obstacleAvoidanceVectorMagnitude - previousObstacleAvoidanceVectorMagnitude, loopTime) * obstacleAvoidanceDirectionOfTravelLimiter.getVelocity(), obstacleAvoidanceVector.getHeading());

			transformedTranslationVector = transformedTranslationVector.add(obstacleFeedback);

			MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = arbFollower.getMotionConstants().makeDirectionOfTravelLimiter(transformedTranslationVector.getHeading());
			transformedTranslationVector = Vector2D.fromPolar(Math.min(directionOfTravelLimiter.getVelocity(), transformedTranslationVector.getMagnitude()), transformedTranslationVector.getHeading());

			previousObstacleAvoidanceVectorMagnitude = obstacleAvoidanceVectorMagnitude;
		}

		arbFollower.followOutput(
				new Followable.Output(
						transformedTranslationVector,
						output.getRotationalVelocity(),
						output.getCallbackTime(),
						output.getPosition(),
						output.getDestination()
				),
				loopTime
		);
	}

	private double modifyObstacleAvoidance(double obstacleDistance, double deltaObstacleDistance, double loopTime) {
		double output = Math.sqrt(obstacleDistance / obstacleAvoidanceDirectionOfTravelLimiter.getVelocity());
		output += (deltaObstacleDistance / loopTime) / obstacleAvoidanceDirectionOfTravelLimiter.getVelocity();
		return Math.max(0, Math.min(output, 1));
	}

	@Override
	public void follow(Vector2D translationVector, double rotationalVelocity, double loopTime) {
		Vector2D obstacleAvoidanceVector = obstacleMap.obstacleAvoidanceVector(tracker.getPose2D());

		if (obstacleAvoidanceVector != null) {
			obstacleAvoidanceDirectionOfTravelLimiter = arbFollower.getMotionConstants().makeDirectionOfTravelLimiter(obstacleAvoidanceVector.getHeading());

			double obstacleAvoidanceVectorMagnitude = obstacleAvoidanceVector.getMagnitude();

			Vector2D obstacleFeedback = Vector2D.fromPolar(modifyObstacleAvoidance(obstacleAvoidanceVectorMagnitude, obstacleAvoidanceVectorMagnitude - previousObstacleAvoidanceVectorMagnitude, loopTime), obstacleAvoidanceVector.getHeading());

			translationVector = translationVector.add(obstacleFeedback);

			MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = arbFollower.getMotionConstants().makeDirectionOfTravelLimiter(translationVector.getHeading());
			translationVector = Vector2D.fromPolar(Math.min(directionOfTravelLimiter.getVelocity(), translationVector.getMagnitude()), translationVector.getHeading());

			previousObstacleAvoidanceVectorMagnitude = obstacleAvoidanceVectorMagnitude;
		}

		arbFollower.follow(translationVector, rotationalVelocity, loopTime);
	}

}
