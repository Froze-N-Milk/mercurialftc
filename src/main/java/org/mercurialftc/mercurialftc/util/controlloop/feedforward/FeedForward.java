package org.mercurialftc.mercurialftc.util.controlloop.feedforward;

import com.qualcomm.robotcore.hardware.DcMotor;

public class FeedForward {
	public DcMotor getMotor() {
		return motor;
	}
	
	public FeedForwardConstants getFeedForwardConstants() {
		return feedForwardConstants;
	}
	
	private final DcMotor motor;
	private final FeedForwardConstants feedForwardConstants;
	private double targetVelocity;
	private double targetAcceleration;
	
	public FeedForward(FeedForwardConstants feedForwardConstants, DcMotor motor) {
		this.feedForwardConstants = feedForwardConstants;
		this.motor = motor;
		
		targetVelocity = 0.0;
		targetAcceleration = 0.0;
	}
	
	public void update() {
		motor.setPower(targetVelocity * feedForwardConstants.getkV() + targetAcceleration * feedForwardConstants.getkA());
	}
	
	public double getTargetVelocity() {
		return targetVelocity;
	}
	
	public void setTargetVelocity(double targetVelocity) {
		this.targetVelocity = targetVelocity;
	}
	
	public double getTargetAcceleration() {
		return targetAcceleration;
	}
	
	public void setTargetAcceleration(double targetAcceleration) {
		this.targetAcceleration = targetAcceleration;
	}
}
