package org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.obstacle;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

@SuppressWarnings("unused")
public interface Obstacle {
	/**
	 * @param position position of the robot
	 * @return the distance from the robot to the closest point of the obstacle
	 */
	Vector2D distance(Vector2D position);

	/**
	 * @param position position of the robot
	 * @return the distance from the robot to the closest point of the obstacle
	 */
	default Vector2D distance(@NotNull Pose2D position) {
		return distance(position.toVector2D());
	}
}
