package org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.obstacle;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

@SuppressWarnings("unused")
public class CircularObstacle implements Obstacle {
	private final double r;
	private final Vector2D center;

	public CircularObstacle(@NotNull Units unit, double x, double y, double r) {
		this.center = new Vector2D(unit.toMillimeters(x), unit.toMillimeters(y));
		this.r = unit.toMillimeters(r);
	}

	@Override
	public Vector2D distance(@NotNull Vector2D position) {
		Vector2D differenceVector = this.center.subtract(position);
		double magnitude = Math.max(0, differenceVector.getMagnitude() - r);
		return Vector2D.fromPolar(magnitude, differenceVector.getHeading());
	}
}
