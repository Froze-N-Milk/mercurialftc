package org.mercurialftc.mercurialftc.silversurfer.geometry;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class AngleRadians extends Angle {
	private static final double ROTATION = 2.0 * Math.PI;

	public AngleRadians(double theta) {
		super(theta);
	}

	@Override
	public double getDegrees() {
		return Math.toDegrees(this.getTheta());
	}

	@Override
	public double getRadians() {
		return this.getTheta();
	}

	@Override
	protected double absolute(double theta) {
		theta %= (ROTATION);
		if (theta < 0.0) {
			theta += ROTATION;
		}
		return theta;
	}

	@Override
	public Angle setTheta(double x, double y) {
		this.setTheta(Math.atan2(x, y));
		return this;
	}

	@Override
	public Angle add(double theta2) {
		return new AngleRadians(this.getTheta() + theta2);
	}

	@Override
	public Angle add(Angle other) {
		return this.add(other.getRadians());
	}

	@Override
	public Angle subtract(double theta2) {
		return new AngleRadians(this.getTheta() - theta2);
	}

	@Override
	public Angle subtract(Angle other) {
		return this.subtract(other.getRadians());
	}

	@Override
	public double findShortestDistance(Angle other) {
		double difference = other.getRadians() - this.getRadians();
		if (difference > Math.PI) {
			return (-(ROTATION)) + difference;
		} else if (difference < -Math.PI) {
			return (ROTATION) + difference;
		}
		return difference;
	}

	@Override
	public final AngleDegrees toAngleDegrees() {
		return new AngleDegrees(this.getDegrees());
	}

	@Override
	public final AngleRadians toAngleRadians() {
		return this;
	}

	@NonNull
	@NotNull
	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "type: RADIANS, theta: %f", getTheta());
	}
}
