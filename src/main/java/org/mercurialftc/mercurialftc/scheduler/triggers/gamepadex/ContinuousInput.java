package org.mercurialftc.mercurialftc.scheduler.triggers.gamepadex;

import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.scheduler.commands.CommandSignature;
import org.mercurialftc.mercurialftc.scheduler.triggers.Trigger;

import java.util.function.DoubleSupplier;

/**
 * allows deadzones and curving to be applied to a double supplier, designed for usages with the input ranges [0, 1] and [-1, 1]
 * used through {@link GamepadEX} to enhance the power of the sticks and triggers
 */
@SuppressWarnings("unused")
public class ContinuousInput {
	private final DoubleSupplier input;
	private double deadZone;
	private CurveSupplier curveSupplier;

	public ContinuousInput(DoubleSupplier input) {
		this.input = input;
		this.deadZone = 0;
		this.curveSupplier = (i) -> i; // default
	}

	public ContinuousInput(DoubleSupplier input, double deadZone, CurveSupplier curveSupplier) {
		this(input);
		this.deadZone = deadZone;
		this.curveSupplier = curveSupplier;
	}

	public double getValue() {
		double value = input.getAsDouble();
		if (Math.abs(value) <= deadZone) {
			value = 0;
		}
		value = curveSupplier.curve(value);
		return value;
	}

	/**
	 * sets the deadzone threshold, which gets applied before curving
	 * <p>all values with magnitude less than the threshold get moved to 0</p>
	 *
	 * @param deadzone the deadzone threshold, with domain [0 - 1]
	 */
	public void applyDeadZone(double deadzone) {
		if (deadzone < 0) {
			deadzone = 0;
		}
		if (deadzone > 1) {
			deadzone = 1;
		}
		this.deadZone = deadzone;
	}

	public CurveSupplier getCurveSupplier() {
		return curveSupplier;
	}

	/**
	 * set the method to run the input through a function
	 * the curve gets applied after deadzoning
	 *
	 * @param curveSupplier the new curve supplier to use
	 */
	public void setCurveSupplier(CurveSupplier curveSupplier) {
		this.curveSupplier = curveSupplier;
	}

	/**
	 * sets the curve supplier to be parabolic, see {@link #setCurveSupplier(CurveSupplier)}
	 */
	public void setParabolicCurve() {
		this.curveSupplier = input -> input * input * Math.signum(input);
	}

	/**
	 * cares only about the magnitude, not the sign, also see {@link #positiveThresholdTrigger(double, Command)} and {@link #negativeThresholdTrigger(double, Command)}
	 *
	 * @param threshold the magnitude at which the trigger should run
	 * @return a new trigger, that returns true when the magnitude of the continuous input is greater than or equal to threshold
	 */
	public Trigger thresholdTrigger(double threshold, Command toRun) {
		return new Trigger(() -> Math.abs(getValue()) >= threshold, toRun);
	}

	/**
	 * cares about the sign of the value, will run when value is above or equal to the threshold, also see {@link #negativeThresholdTrigger(double, Command)} and {@link #thresholdTrigger(double)}
	 *
	 * @param threshold the value at which this trigger should run
	 * @return a new trigger, that returns true when the value of the continuous input is greater than or equal to threshold
	 */
	public Trigger positiveThresholdTrigger(double threshold, Command toRun) {
		return new Trigger(() -> getValue() >= threshold, toRun);
	}

	/**
	 * cares about the sign of the value, will run when value is below or equal to the threshold, also see {@link #positiveThresholdTrigger(double, Command)} and {@link #thresholdTrigger(double)}
	 *
	 * @param threshold the value at which the trigger should run
	 * @return a new trigger, that returns true when the value of the continuous input is less than or equal to threshold
	 */
	public Trigger negativeThresholdTrigger(double threshold, Command toRun) {
		return new Trigger(() -> getValue() <= threshold, toRun);
	}

	/**
	 * inverts the axis
	 * <p>non-mutating</p>
	 *
	 * @return a new continuous input, with the input values inverted, carries all the features applied to the original
	 */
	public ContinuousInput invert() {
		return new ContinuousInput(() -> -input.getAsDouble(), deadZone, curveSupplier);
	}

	public interface CurveSupplier {
		double curve(double input);
	}
}
