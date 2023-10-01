package org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants;

import org.mercurialftc.mercurialftc.silversurfer.geometry.Angle;
import org.mercurialftc.mercurialftc.silversurfer.geometry.AngleRadians;

public class MecanumMotionConstants {
	private final double maxTranslationalYVelocity;
	private final double maxTranslationalXVelocity;
	private final double maxTranslationalAngledVelocity;
	private final double maxRotationalVelocity;

	private final double maxTranslationalYAcceleration;
	private final double maxTranslationalXAcceleration;
	private final double maxTranslationalAngledAcceleration;
	private final double maxRotationalAcceleration;
	private final double translationalVelocityMultiplier, rotationalVelocityMultiplier, translationalAccelerationMultiplier, rotationalAccelerationMultiplier;

	public MecanumMotionConstants(double maxTranslationalYVelocity, double maxTranslationalXVelocity, double maxTranslationalAngledVelocity, double maxRotationalVelocity, double maxTranslationalYAcceleration, double maxTranslationalXAcceleration, double maxTranslationalAngledAcceleration, double maxRotationalAcceleration) {
		this(1, 1, 1, 1, maxTranslationalYVelocity, maxTranslationalXVelocity, maxTranslationalAngledVelocity, maxRotationalVelocity, maxTranslationalYAcceleration, maxTranslationalXAcceleration, maxTranslationalAngledAcceleration, maxRotationalAcceleration);
	}

	public MecanumMotionConstants(double translationalVelocityMultiplier, double rotationalVelocityMultiplier, double translationalAccelerationMultiplier, double rotationalAccelerationMultiplier, double maxTranslationalYVelocity, double maxTranslationalXVelocity, double maxTranslationalAngledVelocity, double maxRotationalVelocity, double maxTranslationalYAcceleration, double maxTranslationalXAcceleration, double maxTranslationalAngledAcceleration, double maxRotationalAcceleration) {
		this.translationalVelocityMultiplier = Math.max(0, Math.min(1, translationalVelocityMultiplier));
		this.translationalAccelerationMultiplier = Math.max(0, Math.min(1, translationalAccelerationMultiplier));
		this.rotationalVelocityMultiplier = Math.max(0, Math.min(1, rotationalVelocityMultiplier));
		this.rotationalAccelerationMultiplier = Math.max(0, Math.min(1, rotationalAccelerationMultiplier));
		this.maxTranslationalYVelocity = Math.max(maxTranslationalYVelocity, 0);
		this.maxTranslationalXVelocity = Math.max(maxTranslationalXVelocity, 0);
		this.maxTranslationalAngledVelocity = Math.max(maxTranslationalAngledVelocity, 0);
		this.maxRotationalVelocity = Math.max(maxRotationalVelocity, 0);
		this.maxTranslationalYAcceleration = Math.max(maxTranslationalYAcceleration, 0);
		this.maxTranslationalXAcceleration = Math.max(maxTranslationalXAcceleration, 0);
		this.maxTranslationalAngledAcceleration = Math.max(maxTranslationalAngledAcceleration, 0);
		this.maxRotationalAcceleration = Math.max(maxRotationalAcceleration, 0);
	}

	public double getTranslationalVelocityMultiplier() {
		return translationalVelocityMultiplier;
	}

	public double getRotationalVelocityMultiplier() {
		return rotationalVelocityMultiplier;
	}

	public double getTranslationalAccelerationMultiplier() {
		return translationalAccelerationMultiplier;
	}

	public double getRotationalAccelerationMultiplier() {
		return rotationalAccelerationMultiplier;
	}

	public double getMaxTranslationalXVelocity() {
		return maxTranslationalXVelocity * translationalVelocityMultiplier;
	}

	public double getMaxTranslationalAngledVelocity() {
		return maxTranslationalAngledVelocity * translationalVelocityMultiplier;
	}

	public double getMaxTranslationalXAcceleration() {
		return maxTranslationalXAcceleration * translationalAccelerationMultiplier;
	}

	public double getMaxTranslationalAngledAcceleration() {
		return maxTranslationalAngledAcceleration * translationalAccelerationMultiplier;
	}

	public double getMaxTranslationalYVelocity() {
		return maxTranslationalYVelocity * translationalVelocityMultiplier;
	}

	public double getMaxRotationalVelocity() {
		return maxRotationalVelocity * rotationalVelocityMultiplier;
	}

	public double getMaxTranslationalYAcceleration() {
		return maxTranslationalYAcceleration * translationalAccelerationMultiplier;
	}

	public double getMaxRotationalAcceleration() {
		return maxRotationalAcceleration * rotationalAccelerationMultiplier;
	}

	public DirectionOfTravelLimiter makeDirectionOfTravelLimiter(Angle directionOfTravel) {
		return new DirectionOfTravelLimiter(directionOfTravel, this);
	}

	public static class DirectionOfTravelLimiter {
		private final double velocity, acceleration;

		private DirectionOfTravelLimiter(Angle directionOfTravel, MecanumMotionConstants mecanumMotionConstants) {
			double yTerm, xTerm, angledModifier;
			AngleRadians angleDoubled = directionOfTravel.add(directionOfTravel).toAngleRadians();
			double sin = Math.sin(directionOfTravel.getRadians());
			double cos = Math.cos(directionOfTravel.getRadians());
			double cos2 = Math.cos(angleDoubled.getRadians());
			yTerm = (sin * sin);
			xTerm = (cos * cos);
			angledModifier = (cos2 * cos2);

			this.velocity = mecanumMotionConstants.getMaxTranslationalAngledVelocity() + angledModifier * (yTerm * (mecanumMotionConstants.getMaxTranslationalYVelocity() - mecanumMotionConstants.getMaxTranslationalAngledVelocity()) + xTerm * (mecanumMotionConstants.getMaxTranslationalXVelocity() - mecanumMotionConstants.getMaxTranslationalAngledVelocity()));
			this.acceleration = mecanumMotionConstants.getMaxTranslationalAngledAcceleration() + angledModifier * (yTerm * (mecanumMotionConstants.getMaxTranslationalYAcceleration() - mecanumMotionConstants.getMaxTranslationalAngledAcceleration()) + xTerm * (mecanumMotionConstants.getMaxTranslationalXAcceleration() - mecanumMotionConstants.getMaxTranslationalAngledAcceleration()));
		}

		public double getVelocity() {
			return velocity;
		}

		public double getAcceleration() {
			return acceleration;
		}
	}
}
