package org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter;

public enum Units {
	METER(1000),
	MILLIMETER(1),
	INCH(25.4),
	FOOT(304.8);

	private final double toMillimetersRatio;

	Units(double toMillimetersRatio) {
		this.toMillimetersRatio = toMillimetersRatio;
	}

	public double getToMillimetersRatio() {
		return toMillimetersRatio;
	}

	public double toMillimeters(double value) {
		return value * toMillimetersRatio;
	}

	public double fromMillimeters(double value) {
		return value / toMillimetersRatio;
	}
}
