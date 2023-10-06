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
	private final double robotSize;

	/**
	 * @param unit                the units for your tile size
	 * @param tileSize            the size length of one tile, this allows this map to adapt to different tile sizes at your own field or at competitions
	 * @param additionalObstacles a list of additional obstacles that you can manipulate in order to add your own realtime obstacle avoidance and detection
	 * @param robotSize           the radius of the robot, you should test with setting this to either 1/2 the width of the robot (representative of the robot driving forwards and backwards) or the longest radius you can measure (to fully prevent running into an obstacle). picking the latter option may affect the willingness of your robot to go through tight spaces during auto
	 */
	public CenterStageObstacleMap(@NotNull Units unit, double tileSize, ArrayList<Obstacle> additionalObstacles, double robotSize) {
		this.additionalObstacles = additionalObstacles;
		tileSize = unit.toMillimeters(tileSize);
		this.robotSize = robotSize;
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

	/**
	 * @param unit      the units for your tile size
	 * @param tileSize  the size length of one tile, this allows this map to adapt to different tile sizes at your own field or at competitions
	 * @param robotSize the radius of the robot, you should test with setting this to either 1/2 the width of the robot (representative of the robot driving forwards and backwards) or the longest radius you can measure (to fully prevent running into an obstacle). picking the latter option may affect the willingness of your robot to go through tight spaces during auto
	 */
	public CenterStageObstacleMap(Units unit, double tileSize, double robotSize) {
		this(unit, tileSize, new ArrayList<>(0), robotSize);
	}


	@Override
	public double getRobotSize() {
		return robotSize;
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
