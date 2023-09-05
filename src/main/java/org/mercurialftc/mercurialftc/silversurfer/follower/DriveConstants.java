package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;

public class DriveConstants {
	private final double trackWidth;
	private final double maxVelocity;
	private final double maxAcceleration;
	
	private DriveConstants(double trackWidth, double maxVelocity, double maxAcceleration) {
		this.trackWidth = trackWidth;
		this.maxVelocity = maxVelocity;
		this.maxAcceleration = maxAcceleration;
	}
	
	/**
	 * used to ensure that DriveConstants are always in millimeters
	 */
	public static class DriveConstantsBuilder {
		private double trackWidth;
		private double maxVelocity;
		private double maxAcceleration;
		public DriveConstantsBuilder(){}
		
		/**
		 * @param value the average distance between any 2 same-side wheels of the robot
		 * @param unit the unit type you are using
		 */
		public void setTrackWidth(double value, Units unit) {
			this.trackWidth = unit.toMillimeters(value);
		}
		
		/**
		 * @param value the maximum number of units per second that the robot can drive at
		 * @param unit the unit type you are using
		 */
		public void setMaxVelocity(double value, Units unit) {
			this.maxVelocity = unit.toMillimeters(value);
		}
		
		/**
		 * @param value the maximum number of units per second squared that the robot can accelerate at
		 * @param unit the unit type you are using
		 */
		public void setMaxAcceleration(double value, Units unit) {
			this.maxAcceleration = unit.toMillimeters(value);
		}
		
		/**
		 * @return the drive constants object
		 */
		public DriveConstants build() {
			return new DriveConstants(trackWidth, maxVelocity, maxAcceleration);
		}
	}
	
	public double getTrackWidth() {
		return trackWidth;
	}
	
	public double getMaxVelocity() {
		return maxVelocity;
	}
	
	public double getMaxAcceleration() {
		return maxAcceleration;
	}
}
