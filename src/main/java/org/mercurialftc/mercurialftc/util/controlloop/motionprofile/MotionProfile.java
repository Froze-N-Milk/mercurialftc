package org.mercurialftc.mercurialftc.util.controlloop.motionprofile;

public abstract class MotionProfile {
	private final MotionProfileConstraints motionProfileConstraints;
	
	protected MotionProfile(MotionProfileConstraints motionProfileConstraints) {
		this.motionProfileConstraints = motionProfileConstraints;
	}
	
	public MotionProfileConstraints getMotionProfileConstraints() {
		return motionProfileConstraints;
	}
	
	public abstract double getTargetVelocity();
	public abstract double getTargetAngularVelocity();
}
