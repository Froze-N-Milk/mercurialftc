package org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap;


import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.obstacle.Obstacle;

import java.util.ArrayList;

@SuppressWarnings("unused")
public interface ObstacleMap {
	double getRobotSize();

	ArrayList<Obstacle> getAdditionalObstacles();

	Obstacle[] getObstacles();

	default Vector2D closestObstacleVector(@NotNull Vector2D position) {
		Vector2D result = null;
		double shortestDistance = Double.POSITIVE_INFINITY;
		for (Obstacle obstacle : getAdditionalObstacles()) {
			Vector2D distanceVector = obstacle.distance(position);
			double distance = distanceVector.getMagnitude();
			if (distance < shortestDistance) {
				result = distanceVector;
				shortestDistance = distance;
			}
		}
		for (Obstacle obstacle : getObstacles()) {
			Vector2D distanceVector = obstacle.distance(position);
			double distance = distanceVector.getMagnitude();
			if (distance < shortestDistance) {
				result = distanceVector;
				shortestDistance = distance;
			}
		}
		if (result != null) {
			result = Vector2D.fromPolar(Math.max(0, result.getMagnitude() - getRobotSize()), result.getHeading());
		}
		return result;
	}

	default Vector2D closestObstacleVector(@NotNull Pose2D position) {
		return closestObstacleVector(position.toVector2D());
	}

	default Vector2D obstacleAvoidanceVector(@NotNull Vector2D position) {
		Vector2D one = null;
		Vector2D two = null;
		double shortestDistanceOne = Double.POSITIVE_INFINITY;
		double shortestDistanceTwo = Double.POSITIVE_INFINITY;
		for (Obstacle obstacle : getAdditionalObstacles()) {
			Vector2D distanceVector = obstacle.distance(position);
			double distance = distanceVector.getMagnitude();
			if (distance < shortestDistanceOne) {
				two = one;
				one = distanceVector;
				shortestDistanceTwo = shortestDistanceOne;
				shortestDistanceOne = distance;
			} else if (distance < shortestDistanceTwo) {
				two = distanceVector;
				shortestDistanceTwo = distance;
			}
		}
		for (Obstacle obstacle : getObstacles()) {
			Vector2D distanceVector = obstacle.distance(position);
			double distance = distanceVector.getMagnitude();
			if (distance < shortestDistanceOne) {
				two = one;
				one = distanceVector;
				shortestDistanceTwo = shortestDistanceOne;
				shortestDistanceOne = distance;
			} else if (distance < shortestDistanceTwo) {
				two = distanceVector;
				shortestDistanceTwo = distance;
			}
		}
		if (one != null) {
			one = Vector2D.fromPolar(Math.max(0, one.getMagnitude() - getRobotSize()), one.getHeading());
		}
		if (two != null) {
			two = Vector2D.fromPolar(Math.max(0, two.getMagnitude() - getRobotSize()), two.getHeading());
		}
		Vector2D result = null;
		if (one != null) result = one;
		if (two != null) result = result.add(two);
		return result;
	}

	default Vector2D obstacleAvoidanceVector(@NotNull Pose2D position) {
		return obstacleAvoidanceVector(position.toVector2D());
	}
}
