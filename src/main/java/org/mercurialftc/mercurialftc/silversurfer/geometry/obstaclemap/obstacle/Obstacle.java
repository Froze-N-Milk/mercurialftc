package org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.obstacle;

import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

@SuppressWarnings("unused")
public interface Obstacle {
	Vector2D distance(Vector2D position);
}
