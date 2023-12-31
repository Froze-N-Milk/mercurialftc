package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.ObstacleMap;
import org.mercurialftc.mercurialftc.silversurfer.tracker.Tracker;

@SuppressWarnings("unused")
public class ObstacleAvoidantFollower extends AbstractWaveFollower implements ArbFollower {
	public static final double MAX_OUTPUT_DISTANCE = 10;
	private final ArbFollower arbFollower;
	private final WaveFollower waveFollower;
	private final Tracker tracker;
	private final ObstacleMap obstacleMap;
	private final MecanumMotionConstants mecanumMotionConstants;
	private MecanumMotionConstants.DirectionOfTravelLimiter obstacleAvoidanceDirectionOfTravelLimiter;
	private double previousObstacleAvoidanceVectorMagnitude;

	public ObstacleAvoidantFollower(@NotNull ArbFollower arbFollower, @NotNull WaveFollower waveFollower, MecanumMotionConstants mecanumMotionConstants, Tracker tracker, ObstacleMap obstacleMap) {
		this.mecanumMotionConstants = mecanumMotionConstants;
		this.waveFollower = waveFollower;
		this.arbFollower = arbFollower;
		this.tracker = tracker;
		this.obstacleMap = obstacleMap;
	}

	@Override
	public void followOutput(@NotNull Followable.Output output, double loopTime) {
		Vector2D obstacleDistanceVector = obstacleMap.closestObstacleVector(tracker.getPose2D());
		Vector2D obstacleAvoidanceVector = obstacleMap.obstacleAvoidanceVector(tracker.getPose2D());

		Vector2D transformedTranslationVector = output.getTranslationVector();

		if (obstacleDistanceVector != null) {
			obstacleAvoidanceDirectionOfTravelLimiter = mecanumMotionConstants.makeDirectionOfTravelLimiter(obstacleDistanceVector.getHeading());

			double obstacleDistanceVectorMagnitude = obstacleDistanceVector.getMagnitude();

			double obstacleAvoidanceMultiplier = Math.min(1, obstacleAvoidanceVector.getMagnitude() / (MAX_OUTPUT_DISTANCE * MAX_OUTPUT_DISTANCE));

			Vector2D obstacleFeedback = Vector2D.fromPolar(obstacleAvoidanceMultiplier * modifyObstacleAvoidance(obstacleDistanceVectorMagnitude, obstacleDistanceVectorMagnitude - previousObstacleAvoidanceVectorMagnitude, loopTime) * obstacleAvoidanceDirectionOfTravelLimiter.getVelocity(), obstacleDistanceVector.getHeading());

			transformedTranslationVector = transformedTranslationVector.add(obstacleFeedback);

			MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = mecanumMotionConstants.makeDirectionOfTravelLimiter(transformedTranslationVector.getHeading());
			transformedTranslationVector = Vector2D.fromPolar(Math.min(directionOfTravelLimiter.getVelocity(), transformedTranslationVector.getMagnitude()), transformedTranslationVector.getHeading());

			previousObstacleAvoidanceVectorMagnitude = obstacleDistanceVectorMagnitude;
		}

		waveFollower.followOutput(
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
		obstacleDistance = Math.max(0.001, obstacleDistance);
		double output = MAX_OUTPUT_DISTANCE / -obstacleDistance; // the obstacle vector points us towards the obstacle, so we need to return a negative velocity to move us away
		output += (deltaObstacleDistance / loopTime) / obstacleAvoidanceDirectionOfTravelLimiter.getVelocity(); // dampening
		return Math.max(-1, Math.min(output, 0));
	}

	@Override
	public void follow(Vector2D translationVector, double rotationalVelocity, double loopTime) {
		Vector2D obstacleDistanceVector = obstacleMap.closestObstacleVector(tracker.getPose2D());

		if (obstacleDistanceVector != null) {
			obstacleAvoidanceDirectionOfTravelLimiter = mecanumMotionConstants.makeDirectionOfTravelLimiter(obstacleDistanceVector.getHeading());

			double obstacleDistanceVectorMagnitude = obstacleDistanceVector.getMagnitude();

			Vector2D obstacleFeedback = Vector2D.fromPolar(modifyObstacleAvoidance(obstacleDistanceVectorMagnitude, obstacleDistanceVectorMagnitude - previousObstacleAvoidanceVectorMagnitude, loopTime), obstacleDistanceVector.getHeading());

			translationVector = translationVector.add(obstacleFeedback);

			MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = mecanumMotionConstants.makeDirectionOfTravelLimiter(translationVector.getHeading());
			translationVector = Vector2D.fromPolar(Math.min(directionOfTravelLimiter.getVelocity(), translationVector.getMagnitude()), translationVector.getHeading());

			previousObstacleAvoidanceVectorMagnitude = obstacleDistanceVectorMagnitude;
		}

		arbFollower.follow(translationVector, rotationalVelocity, loopTime);
	}

}
