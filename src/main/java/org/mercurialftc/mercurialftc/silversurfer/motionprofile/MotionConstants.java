package org.mercurialftc.mercurialftc.silversurfer.motionprofile;

public class MotionConstants {
	private final double maxTranslationalVelocity;
	private final double maxAngularVelocity; // angular translational change
	private final double maxRotationalVelocity;
	
	private final double maxTranslationalAcceleration;
	private final double maxAngularAcceleration; // change in angular translational change
	private final double maxRotationalAcceleration;
	
	public double getMaxTranslationalVelocity() {
		return maxTranslationalVelocity;
	}
	
	public double getMaxAngularVelocity() {
		return maxAngularVelocity;
	}
	
	public double getMaxRotationalVelocity() {
		return maxRotationalVelocity;
	}
	
	public double getMaxTranslationalAcceleration() {
		return maxTranslationalAcceleration;
	}
	
	public double getMaxAngularAcceleration() {
		return maxAngularAcceleration;
	}
	
	public double getMaxRotationalAcceleration() {
		return maxRotationalAcceleration;
	}
	
	// todo bring in a voltage for these or smth
	public MotionConstants(double maxTranslationalVelocity, double maxAngularVelocity, double maxRotationalVelocity, double maxTranslationalAcceleration, double maxAngularAcceleration, double maxRotationalAcceleration) {
		this.maxTranslationalVelocity = Math.max(maxTranslationalVelocity, 0);
		this.maxAngularVelocity = Math.max(maxAngularVelocity, 0);
		this.maxRotationalVelocity = Math.max(maxRotationalVelocity, 0);
		this.maxTranslationalAcceleration = Math.max(maxTranslationalAcceleration, 0);
		this.maxAngularAcceleration = Math.max(maxAngularAcceleration, 0);
		this.maxRotationalAcceleration = Math.max(maxRotationalAcceleration, 0);
	}
	
	public static class VoltageConstantRelationship {
		private final double velocity;
		private final double acceleration;
		
		private final double recordedVoltage;
		
		private double currentVoltage;
		
		public VoltageConstantRelationship(double recordedVoltage, double velocity, double acceleration) {
			this.velocity = velocity;
			this.acceleration = acceleration;
			this.recordedVoltage = recordedVoltage;
		}
		
		public void setCurrentVoltage(double currentVoltage) {
			this.currentVoltage = currentVoltage;
		}
		
		public double getVelocity() {
			return (velocity / recordedVoltage) * currentVoltage;
		}
		
		public double getAcceleration() {
			return (acceleration / recordedVoltage) * currentVoltage;
		}
		
		public double getRecordedVoltage() {
			return recordedVoltage;
		}
	}
}
