package org.mercurialftc.mercurialftc.util.controlloop.motionprofile;

public class MotionProfileConstraints {
	private final double maxVelocity, minVelocity;
	private final double maxAcceleration;
	
	public MotionProfileConstraints(double maxVelocity, double minVelocity, double maxAcceleration) {
		this.maxVelocity = maxVelocity;
		this.minVelocity = minVelocity;
		this.maxAcceleration = maxAcceleration;
	}
	
	public double getMaxVelocity() {
		return maxVelocity;
	}
	
	public double getMinVelocity() {
		return minVelocity;
	}
	
	public double getMaxAcceleration() {
		return maxAcceleration;
	}
}
