package org.mercurialftc.mercurialftc.silversurfer.tracker;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.EncoderTicksConverter;
import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;

public abstract class TrackerConstants {
	private final double lateralDistance;
	private final double forwardOffset;

	private final double xMult, yMult;

	public double getLateralDistance() {
		return lateralDistance;
	}

	public double getForwardOffset() {
		return forwardOffset;
	}

	public double getYMult() {
		return yMult;
	}

	public double getXMult() {
		return xMult;
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
	 * @param units                the units used for {@link #lateralDistance} and {@link #forwardOffset}
	 * @param lateralDistance      the distance between the two parallel sensors
	 * @param forwardOffset        the distance from the perpendicular sensor to the parallel line that passes through the robot's center of rotation
	 * @param xMult                the multiplier applied to measuring changes in the x-axis
	 * @param yMult                the multiplier applied to measuring changes in the y-axis
	 * @param leftTicksConverter   the encoder ticks converter for the left sensor
	 * @param rightTicksConverter  the encoder ticks converter for the right sensor
	 * @param middleTicksConverter the encoder ticks converter for the perpendicular sensor
	 */
	private TrackerConstants(
			@NotNull Units units,
			double lateralDistance,
			double forwardOffset,
			double xMult, double yMult, EncoderTicksConverter leftTicksConverter,
			EncoderTicksConverter rightTicksConverter,
			EncoderTicksConverter middleTicksConverter
	) {
		this.xMult = xMult;
		this.yMult = yMult;
		this.lateralDistance = units.toMillimeters(lateralDistance);
		this.forwardOffset = units.toMillimeters(forwardOffset);
		this.leftTicksConverter = leftTicksConverter;
		this.rightTicksConverter = rightTicksConverter;
		this.middleTicksConverter = middleTicksConverter;
	}

	public static class ThreeWheelTrackerConstants extends TrackerConstants {

		/**
		 * @param units                the units used for {@link #lateralDistance} and {@link #forwardOffset}
		 * @param lateralDistance      the distance between the two parallel sensors
		 * @param forwardOffset        the distance from the perpendicular sensor to the center of rotation
		 * @param leftTicksConverter   the encoder ticks converter for the left sensor
		 * @param rightTicksConverter  the encoder ticks converter for the right sensor
		 * @param middleTicksConverter the encoder ticks converter for the perpendicular sensor
		 */
		public ThreeWheelTrackerConstants(Units units, double lateralDistance, double forwardOffset, double xMult, double yMult, EncoderTicksConverter leftTicksConverter, EncoderTicksConverter rightTicksConverter, EncoderTicksConverter middleTicksConverter) {
			super(units, lateralDistance, forwardOffset, xMult, yMult, leftTicksConverter, rightTicksConverter, middleTicksConverter);
		}
	}

	public static class TwoWheelTrackerConstants extends TrackerConstants {

		/**
		 * @param units                the units used for {@link #forwardOffset}
		 * @param forwardOffset        the distance from the perpendicular sensor to the center of rotation
		 * @param leftTicksConverter   the encoder ticks converter for the left sensor
		 * @param middleTicksConverter the encoder ticks converter for the perpendicular sensor
		 */
		public TwoWheelTrackerConstants(Units units, double forwardOffset, double xMult, double yMult, EncoderTicksConverter leftTicksConverter, EncoderTicksConverter middleTicksConverter) {
			super(units, 0, forwardOffset, xMult, yMult, leftTicksConverter, new EncoderTicksConverter(0, Units.MILLIMETER), middleTicksConverter);
		}
	}
}
