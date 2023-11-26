package org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter;

/**
 * uses millimeters under the hood, for use in pose tracking
 */
public class EncoderTicksConverter {
	private final double ticksPerMillimeter;

	/**
	 * @param ticksPerUnit number of ticks from a tracking encoder to one of the specified unit
	 * @param unit         the unit type you used to measure this ratio
	 */
	public EncoderTicksConverter(double ticksPerUnit, Units unit) {
		this.ticksPerMillimeter = unit.fromMillimeters(ticksPerUnit);
	}

	/**
	 * @param ticks number of ticks
	 * @param unit  output unit type
	 * @return number of units equal to that number of ticks
	 */
	public double toUnits(double ticks, Units unit) {
		return unit.fromMillimeters(ticks / ticksPerMillimeter);
	}

	/**
	 * @param units number of units
	 * @param unit  type of unit
	 * @return the number of ticks equal to that many of that unit
	 */
	public double toTicks(double units, Units unit) {
		return ticksPerMillimeter * unit.toMillimeters(units);
	}
}
