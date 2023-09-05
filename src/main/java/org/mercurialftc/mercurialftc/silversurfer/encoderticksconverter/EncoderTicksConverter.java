package org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter;

/**
 * uses millimeters under the hood, for use in pose tracking
 */
public class EncoderTicksConverter {
	private final double ticksPerMillimeter;
	
	public EncoderTicksConverter(double ticksPerUnit, Units unit) {
		this.ticksPerMillimeter = unit.toMillimeters(ticksPerUnit);
	}
	
	public double toUnits(double ticks, Units unit) {
		return unit.fromMillimeters(ticks / ticksPerMillimeter);
	}
	
	public double toTicks(double units, Units unit) {
		return ticksPerMillimeter * unit.toMillimeters(units);
	}
}
