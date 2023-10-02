package org.mercurialftc.mercurialftc.silversurfer.geometry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.Angle;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.AngleRadians;

import java.util.Locale;

public class Vector2D {
	private double x, y;

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
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
	 * @return a Vector2D with the supplied properties
	 */
	public static Vector2D fromPolar(double r, Angle t) {
		return new Vector2D(r * Math.cos(t.getRadians()), r * Math.sin(t.getRadians()));
	}

	public double getX() {
		return x;
	}

	/**
	 * mutates state
	 *
	 * @param x
	 */
	@SuppressWarnings("unused")
	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	/**
	 * mutates state
	 *
	 * @param y
	 */
	@SuppressWarnings("unused")
	public void setY(double y) {
		this.y = y;
	}

	public Angle getHeading() {
		return new AngleRadians(Math.atan2(y, x));
	}

	@SuppressWarnings("unused")
	public double getMagnitude() {
		return Math.hypot(x, y);
	}

	/**
	 * mutates state
	 *
	 * @param x
	 * @param y
	 * @return self
	 */
	@SuppressWarnings("unused")
	public Vector2D set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * non-mutating
	 *
	 * @param x
	 * @param y
	 * @return a new vector with the desired operation applied
	 */
	@SuppressWarnings("unused")
	public Vector2D add(double x, double y) {
		return new Vector2D(this.x + x, this.y + y);
	}

	/**
	 * non-mutating
	 *
	 * @param x
	 * @param y
	 * @return a new vector with the desired operation applied
	 */
	@SuppressWarnings("unused")
	public Vector2D subtract(double x, double y) {
		return new Vector2D(this.x - x, this.y - y);
	}

	/**
	 * non-mutating
	 *
	 * @param other
	 * @return a new vector with the desired operation applied
	 */
	@SuppressWarnings("unused")
	public Vector2D add(@NotNull Vector2D other) {
		return this.add(other.x, other.y);
	}

	/**
	 * non-mutating
	 *
	 * @param other
	 * @return a new vector with the desired operation applied
	 */
	@SuppressWarnings("unused")
	public Vector2D subtract(@NotNull Vector2D other) {
		return this.subtract(other.x, other.y);
	}

	/**
	 * non-mutating
	 *
	 * @param factor the scalar multiplication
	 * @return a new vector with the desired operation applied
	 */
	@SuppressWarnings("unused")
	public Vector2D scalarMultiply(double factor) {
		return fromPolar(getMagnitude() * factor, getHeading());
	}

	/**
	 * dot product
	 *
	 * @param other
	 * @return
	 */
	@SuppressWarnings("unused")
	public double dot(@NotNull Vector2D other) {
		return this.getX() * other.getX() + this.getY() * other.getY();
	}

	/**
	 * rotates anti-clockwise
	 *
	 * @param angle
	 * @return a new vector with the desired operation applied
	 */
	@SuppressWarnings("unused")
	public Vector2D rotate(@NotNull Angle angle) {
		double cos = Math.cos(angle.getRadians());
		double sin = Math.sin(angle.getRadians());
		return new Vector2D(cos * getX() - sin * getY(), sin * getX() + cos * getY());
	}

	@Override
	public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
		if (!(obj instanceof Vector2D)) return false;
		Vector2D other = (Vector2D) obj;
		return this.getX() == other.getX() && this.getY() == other.getY();
	}

	@NonNull
	@NotNull
	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "x: %f, y: %f", getX(), getY());
	}
}
