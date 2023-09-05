package org.mercurialftc.mercurialftc.silversurfer.tracker;

import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.EncoderTicksConverter;
import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;

public abstract class TrackerConstants {
	private final double lateralDistance;
	private final double forwardOffset;
	
	public double getLateralDistance() {
		return lateralDistance;
	}
	
	public double getForwardOffset() {
		return forwardOffset;
	}
	
	private final EncoderTicksConverter leftTicksConverter, rightTicksConverter, middleTicksConverter;
	
	public EncoderTicksConverter getLeftTicksConverter() {
		return leftTicksConverter;
	}
	
	public EncoderTicksConverter getRightTicksConverter() {
		return rightTicksConverter;
	}
	
	public EncoderTicksConverter getMiddleTicksConverter() {
		return middleTicksConverter;
	}
	
	
	
	/**
	 *
	 * @param lateralDistance the distance between the two parallel sensors
	 * @param forwardOffset the distance from the perpendicular sensor to the center of rotation // TODO: explain this better
	 * @param leftTicksConverter the encoder ticks converter for the left sensor
	 * @param rightTicksConverter the encoder ticks converter for the right sensor
	 * @param middleTicksConverter the encoder ticks converter for the perpendicular sensor
	 */
	private TrackerConstants(
			double lateralDistance,
			double forwardOffset,
			EncoderTicksConverter leftTicksConverter,
			EncoderTicksConverter rightTicksConverter,
			EncoderTicksConverter middleTicksConverter
	) {
		this.lateralDistance = lateralDistance;
		this.forwardOffset = forwardOffset;
		this.leftTicksConverter = leftTicksConverter;
		this.rightTicksConverter = rightTicksConverter;
		this.middleTicksConverter = middleTicksConverter;
	}
	
	public static class ThreeWheelTrackerConstants extends TrackerConstants {
		
		/**
		 * @param lateralDistance      the distance between the two parallel sensors
		 * @param forwardOffset        the distance from the perpendicular sensor to the center of rotation
		 * @param leftTicksConverter   the encoder ticks converter for the left sensor
		 * @param rightTicksConverter  the encoder ticks converter for the right sensor
		 * @param middleTicksConverter the encoder ticks converter for the perpendicular sensor
		 */
		public ThreeWheelTrackerConstants(double lateralDistance, double forwardOffset, EncoderTicksConverter leftTicksConverter, EncoderTicksConverter rightTicksConverter, EncoderTicksConverter middleTicksConverter) {
			super(lateralDistance, forwardOffset, leftTicksConverter, rightTicksConverter, middleTicksConverter);
		}
	}
	
	public static class TwoWheelTrackerConstants extends TrackerConstants {
		
		/**
		 * @param forwardOffset        the distance from the perpendicular sensor to the center of rotation
		 * @param leftTicksConverter   the encoder ticks converter for the left sensor
		 * @param middleTicksConverter the encoder ticks converter for the perpendicular sensor
		 */
		public TwoWheelTrackerConstants(double forwardOffset, EncoderTicksConverter leftTicksConverter, EncoderTicksConverter middleTicksConverter) {
			super(0, forwardOffset, leftTicksConverter, new EncoderTicksConverter(0, Units.MILLIMETER), middleTicksConverter);
		}
	}
}
