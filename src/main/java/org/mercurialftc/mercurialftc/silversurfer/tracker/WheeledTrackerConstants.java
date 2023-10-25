package org.mercurialftc.mercurialftc.silversurfer.tracker;

import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.EncoderTicksConverter;
import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

public abstract class WheeledTrackerConstants {
	private final Vector2D centerOfRotationOffset;
	private final double xMult, yMult;
	private final EncoderTicksConverter leftTicksConverter, rightTicksConverter, middleTicksConverter;

	/**
	 * @param centerOfRotationOffset the vector to move the center of rotation
	 * @param xMult                  the multiplier applied to measuring changes in the x-axis
	 * @param yMult                  the multiplier applied to measuring changes in the y-axis
	 * @param leftTicksConverter     the encoder ticks converter for the left sensor
	 * @param rightTicksConverter    the encoder ticks converter for the right sensor
	 * @param middleTicksConverter   the encoder ticks converter for the perpendicular sensor
	 */
	private WheeledTrackerConstants(
			Vector2D centerOfRotationOffset,
			double xMult, double yMult, EncoderTicksConverter leftTicksConverter,
			EncoderTicksConverter rightTicksConverter,
			EncoderTicksConverter middleTicksConverter
	) {
		this.xMult = xMult;
		this.yMult = yMult;
		this.centerOfRotationOffset = centerOfRotationOffset;
		this.leftTicksConverter = leftTicksConverter;
		this.rightTicksConverter = rightTicksConverter;
		this.middleTicksConverter = middleTicksConverter;
	}

	public double getYMult() {
		return yMult;
	}

	public double getXMult() {
		return xMult;
	}

	public EncoderTicksConverter getLeftTicksConverter() {
		return leftTicksConverter;
	}

	public EncoderTicksConverter getRightTicksConverter() {
		return rightTicksConverter;
	}

	public EncoderTicksConverter getMiddleTicksConverter() {
		return middleTicksConverter;
	}

	public Vector2D getCenterOfRotationOffset() {
		return centerOfRotationOffset;
	}

	public static class ThreeWheeledTrackerConstants extends WheeledTrackerConstants {
		private final double trackWidth;

		/**
		 * @param leftTicksConverter   the encoder ticks converter for the left sensor
		 * @param rightTicksConverter  the encoder ticks converter for the right sensor
		 * @param middleTicksConverter the encoder ticks converter for the perpendicular sensor
		 * @param trackWidth
		 */
		public ThreeWheeledTrackerConstants(Vector2D centerOfRotationOffset, double xMult, double yMult, EncoderTicksConverter leftTicksConverter, EncoderTicksConverter rightTicksConverter, EncoderTicksConverter middleTicksConverter, double trackWidth) {
			super(centerOfRotationOffset, xMult, yMult, leftTicksConverter, rightTicksConverter, middleTicksConverter);
			this.trackWidth = trackWidth;
		}

		public double getTrackWidth() {
			return trackWidth;
		}
	}

	public static class TwoWheeledTrackerConstants extends WheeledTrackerConstants {

		/**
		 * @param leftTicksConverter   the encoder ticks converter for the left sensor
		 * @param middleTicksConverter the encoder ticks converter for the perpendicular sensor
		 */
		public TwoWheeledTrackerConstants(Vector2D centerOfRotationOffset, double xMult, double yMult, EncoderTicksConverter leftTicksConverter, EncoderTicksConverter middleTicksConverter) {
			super(centerOfRotationOffset, xMult, yMult, leftTicksConverter, new EncoderTicksConverter(0, Units.MILLIMETER), middleTicksConverter);
		}
	}
}
