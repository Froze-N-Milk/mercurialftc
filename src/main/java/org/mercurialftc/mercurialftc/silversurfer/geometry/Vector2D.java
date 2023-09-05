package org.mercurialftc.mercurialftc.silversurfer.geometry;

public class Vector2D {
	private double x, y;
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Angle getHeading() {
		return new AngleRadians(Math.atan2(y, x));
	}
	
	public double getMagnitude() {
		return Math.hypot(x, y);
	}
	
	public Vector2D set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	/**
	 * non-mutating
	 * @param x
	 * @param y
	 * @return a new vector with the desired operation applied
	 */
	public Vector2D add(double x, double y) {
		return new Vector2D(this.x + x, this.y + y);
	}
	
	/**
	 * non-mutating
	 * @param x
	 * @param y
	 * @return a new vector with the desired operation applied
	 */
	public Vector2D subtract(double x, double y) {
		return new Vector2D(this.x - x, this.y - y);
		
	}
	
	/**
	 * non-mutating
	 * @param other
	 * @return a new vector with the desired operation applied
	 */
	public Vector2D add(Vector2D other) {
		return this.add(other.x, other.y);
	}
	
	/**
	 * non-mutating
	 * @param other
	 * @return a new vector with the desired operation applied
	 */
	public Vector2D subtract(Vector2D other) {
		return this.subtract(other.x, other.y);
	}
	
	/**
	 * non-mutating
	 * @param factor the scalar multiplication
	 * @return a new vector with the desired operation applied
	 */
	public Vector2D scalarMultiply(double factor) {
		return new Vector2D(factor * this.getX(), factor * this.getY());
	}
	
	
	public double dot(Vector2D other) {
		return this.getX() * other.getX() + this.getY() * other.getY();
	}
	
	/**
	 * The polar constructor for vectors, uses radians
	 *
	 * @param r magnitude
	 * @param t theta in radians
	 * @return a Vector2D with the above values assigned to x and y coordinates
	 */
	public static Vector2D fromPolar(double r, double t) {
		return fromPolar(r, new AngleRadians(t));
	}
	
	/**
	 * The polar constructor for vectors
	 *
	 * @param r magnitude
	 * @param t theta
	 * @return a Vector2D with the above values assigned to x and y coordinates
	 */
	public static Vector2D fromPolar(double r, Angle t) {
		return new Vector2D(r * Math.cos(t.getRadians()), r * Math.sin(t.getRadians()));
	}
}
