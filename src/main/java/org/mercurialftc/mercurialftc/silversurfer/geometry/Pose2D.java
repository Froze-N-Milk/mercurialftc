package org.mercurialftc.mercurialftc.silversurfer.geometry;

import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;

/**
 * represents a robot position through a global x and y coordinate (always millimeters internally) and an angle (always radians internally) angles are represented as starting at the positive x axis and increasing in the anticlockwise direction
 */
public class Pose2D {
	private final double x;
	private final double y;
	private final AngleRadians theta;
	
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public AngleRadians getTheta() {
		return theta;
	}
	
	
	/**
	 * constructs a pose
	 * @param x the robot's x position
	 * @param y the robot's y position
	 * @param theta the robot's angle
	 * @param units the units used for x and y
	 */
	public Pose2D(double x, double y, Angle theta, Units units) {
		this.x = units.toMillimeters(x);
		this.y = units.toMillimeters(y);
		this.theta = theta.toAngleRadians();
	}
	
	/**
	 * constructs a pose, assumes the use of degrees
	 * @param x the robot's x position
	 * @param y the robot's y position
	 * @param theta the robot's angle, in degrees
	 * @param units the units used for x and y
	 */
	public Pose2D(double x, double y, double theta, Units units) {
		this(x, y, new AngleDegrees(theta), units);
	}
	
	/**
	 * constructs a pose, assumes the use of millimeters
	 * @param x the robot's x position
	 * @param y the robot's y position
	 * @param theta the robot's angle
	 */
	public Pose2D(double x, double y, Angle theta) {
		this(x, y, theta, Units.MILLIMETER);
	}
	
	/**
	 * constructs a pose, assumes the use of millimeters and degrees
	 * @param x the robot's x position
	 * @param y the robot's y position
	 * @param theta the robot's angle, in degrees
	 */
	public Pose2D(double x, double y, double theta) {
		this(x, y, new AngleDegrees(theta), Units.MILLIMETER);
	}
	
	/**
	 * a default constructor that sets everything to 0
	 */
	public Pose2D() {
		this(0, 0, 0, Units.MILLIMETER);
	}
	
	/**
	 * non mutating
	 * @param other
	 * @return
	 */
	public Pose2D add(Pose2D other) {
		return new Pose2D(x + other.getX(), y + other.getY(), theta.add(other.getTheta()));
	}
	
	/**
	 * non mutating
	 * @param x
	 * @param y
	 * @param theta
	 * @return
	 */
	public Pose2D add(double x, double y, Angle theta) {
		return new Pose2D(this.x + x, this.y + y, this.theta.add(theta));
	}
	
	/**
	 * non mutating
	 * @param other
	 * @return
	 */
	public Pose2D subtract(Pose2D other) {
		return new Pose2D(x - other.getX(), y - other.getY(), theta.subtract(other.getTheta()));
	}
	
	public Pose2D subtract(double x, double y, Angle theta) {
		return new Pose2D(this.x - x, this.y - y, this.theta.subtract(theta));
	}
}
