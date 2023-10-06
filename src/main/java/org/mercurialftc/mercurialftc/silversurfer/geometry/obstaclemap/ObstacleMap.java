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
		if(result != null) {
			result = result.add(Vector2D.fromPolar(getRobotSize(), result.getHeading()));
		}
		return result;
	}

	default Vector2D closestObstacleVector(@NotNull Pose2D position) {
		return closestObstacleVector(position.toVector2D());
	}
}
