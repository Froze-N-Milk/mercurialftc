package org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants;

public class MecanumMotionConstants {
	private final double maxTranslationalVelocity;
	private final double maxRotationalVelocity;

	private final double maxTranslationalAcceleration;
	private final double maxRotationalAcceleration;

	public MecanumMotionConstants(double maxTranslationalVelocity, double maxRotationalVelocity, double maxTranslationalAcceleration, double maxRotationalAcceleration) {
		this.maxTranslationalVelocity = Math.max(maxTranslationalVelocity, 0);
		this.maxRotationalVelocity = Math.max(maxRotationalVelocity, 0);
		this.maxTranslationalAcceleration = Math.max(maxTranslationalAcceleration, 0);
		this.maxRotationalAcceleration = Math.max(maxRotationalAcceleration, 0);
	}

	public double getMaxTranslationalVelocity() {
		return maxTranslationalVelocity;
	}

	public double getMaxRotationalVelocity() {
		return maxRotationalVelocity;
	}

	public double getMaxTranslationalAcceleration() {
		return maxTranslationalAcceleration;
	}

	public double getMaxRotationalAcceleration() {
		return maxRotationalAcceleration;
	}

}
