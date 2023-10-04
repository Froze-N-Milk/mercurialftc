package org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;
import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.obstacle.Obstacle;
import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.obstacle.RectangularObstacle;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class CenterStageObstacleMap implements ObstacleMap {
	private final ArrayList<Obstacle> additionalObstacles;
	private final Obstacle[] obstacles;

	public CenterStageObstacleMap(@NotNull Units unit, double tileSize, ArrayList<Obstacle> additionalObstacles) {
		this.additionalObstacles = additionalObstacles;
		tileSize = unit.toMillimeters(tileSize);
		this.obstacles = new Obstacle[]{
				// trusses

				// blue
				// furthest
				new RectangularObstacle(Units.MILLIMETER, -1 * tileSize - Units.INCH.toMillimeters(1), 3 * tileSize - Units.INCH.toMillimeters(1), Units.INCH.toMillimeters(1), 3 * tileSize),
				// middle
				new RectangularObstacle(Units.MILLIMETER, -1 * tileSize - Units.INCH.toMillimeters(1), 2 * tileSize - Units.INCH.toMillimeters(0.5), Units.INCH.toMillimeters(1), 2 * tileSize + Units.INCH.toMillimeters(0.5)),
				// closest
				new RectangularObstacle(Units.MILLIMETER, -1 * tileSize - Units.INCH.toMillimeters(1), 1 * tileSize - Units.INCH.toMillimeters(0.5), Units.INCH.toMillimeters(1), 1 * tileSize + Units.INCH.toMillimeters(0.5)),

				// red
				// closest
				new RectangularObstacle(Units.MILLIMETER, -1 * tileSize - Units.INCH.toMillimeters(1), -1 * tileSize - Units.INCH.toMillimeters(0.5), Units.INCH.toMillimeters(1), -1 * tileSize + Units.INCH.toMillimeters(0.5)),
				// middle
				new RectangularObstacle(Units.MILLIMETER, -1 * tileSize - Units.INCH.toMillimeters(1), -2 * tileSize - Units.INCH.toMillimeters(0.5), Units.INCH.toMillimeters(1), -2 * tileSize + Units.INCH.toMillimeters(0.5)),
				// furthest
				new RectangularObstacle(Units.MILLIMETER, -1 * tileSize - Units.INCH.toMillimeters(1), -3 * tileSize, Units.INCH.toMillimeters(1), -3 * tileSize + Units.INCH.toMillimeters(1)),


				// backdrops

				// blue
				new RectangularObstacle(Units.MILLIMETER, 2.5 * tileSize, 1 * tileSize, 3 * tileSize, 2 * tileSize),
				// red
				new RectangularObstacle(Units.MILLIMETER, 2.5 * tileSize, -1 * tileSize, 3 * tileSize, -2 * tileSize),
		};
	}

	public CenterStageObstacleMap(Units unit, double tileSize) {
		this(unit, tileSize, new ArrayList<>(0));
	}


	@Override
	public ArrayList<Obstacle> getAdditionalObstacles() {
		return additionalObstacles;
	}

	@Override
	public Obstacle[] getObstacles() {
		return obstacles;
	}
}
