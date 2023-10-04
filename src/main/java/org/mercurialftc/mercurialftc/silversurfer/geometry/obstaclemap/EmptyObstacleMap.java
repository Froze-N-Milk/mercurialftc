package org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap;

import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.obstacle.Obstacle;

import java.util.ArrayList;

public class EmptyObstacleMap implements ObstacleMap {
	private final ArrayList<Obstacle> additionalObstacles;

	public EmptyObstacleMap(ArrayList<Obstacle> additionalObstacles) {
		this.additionalObstacles = additionalObstacles;
	}

	public EmptyObstacleMap() {
		this(new ArrayList<>(0));
	}

	@Override
	public ArrayList<Obstacle> getAdditionalObstacles() {
		return additionalObstacles;
	}

	@Override
	public Obstacle[] getObstacles() {
		return new Obstacle[0];
	}
}
