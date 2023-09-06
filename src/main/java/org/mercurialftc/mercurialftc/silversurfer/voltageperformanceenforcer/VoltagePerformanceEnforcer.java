package org.mercurialftc.mercurialftc.silversurfer.voltageperformanceenforcer;

/**
 * modifies max motor outputs to account for changes in battery voltage
 */
public class VoltagePerformanceEnforcer {

	private final double recordedVoltageConstant;
	private final double recordedCurrent;

	public VoltagePerformanceEnforcer(double recordedVoltage, double recordedCurrent, double recordedVelocity) {
		double stallCurrent = (0.835) * recordedVoltage + (0.99); // estimates the stall current for voltages over 12 volts
		double resistance = recordedVoltage / stallCurrent;
		this.recordedVoltageConstant = recordedVelocity / (recordedVoltage - recordedCurrent * resistance);
		this.recordedCurrent = recordedCurrent;
	}

	public double transformVelocity(double voltage) {
		double stallCurrent = (0.835) * voltage + (0.99); // estimates the stall current for voltages over 12 volts
		double resistance = voltage / stallCurrent;
		return recordedVoltageConstant * (voltage - recordedCurrent * resistance);
	}
}
