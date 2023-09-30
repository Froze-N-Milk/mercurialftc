package org.mercurialftc.mercurialftc.silversurfer.geometry;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class AngleDegrees extends Angle {

	public AngleDegrees(double theta) {
		super(theta);
	}

	@Override
	public double getDegrees() {
		return this.getTheta();
	}

	@Override
	public double getRadians() {
		return new AngleRadians(Math.toRadians(this.getTheta())).getRadians();
	}

	@Override
	protected double absolute(double theta) {
		theta %= 360.0;
		if (theta < 0.0) {
			theta += 360.0;
		}
		return theta;
	}

	@Override
	public Angle setTheta(double x, double y) {
		this.setTheta(Math.toDegrees(Math.atan2(x, y)));
		return this;
	}

	@Override
	public Angle add(double theta2) {
		return new AngleDegrees(this.getTheta() + theta2);
	}

	@Override
	public Angle add(Angle other) {
		return this.add(other.getDegrees());
	}

	@Override
	public Angle subtract(double theta2) {
		return new AngleDegrees(this.getTheta() - theta2);
	}

	@Override
	public Angle subtract(Angle other) {
		return this.subtract(other.getDegrees());
	}

	@Override
	public double findShortestDistance(Angle other) {
		double difference = other.getDegrees() - this.getDegrees();
		if (difference > 180) {
			return (-360) + difference;
		} else if (difference < -180) {
			return 360 + difference;
		}
		return difference;
	}

	@Override
	public final AngleRadians toAngleRadians() {
		return new AngleRadians(this.getRadians());
	}

	@Override
	public final AngleDegrees toAngleDegrees() {
		return this;
	}

	@NonNull
	@NotNull
	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "type: DEGREES, theta: %f", getTheta());
	}
}
