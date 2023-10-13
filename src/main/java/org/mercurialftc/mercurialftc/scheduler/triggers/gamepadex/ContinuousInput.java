package org.mercurialftc.mercurialftc.scheduler.triggers.gamepadex;

import org.mercurialftc.mercurialftc.scheduler.triggers.gamepadex.domainbindingbuilder.DomainBindingBuilder;

import java.util.function.DoubleSupplier;

/**
 * allows deadzones and curving to be applied to a double supplier, designed for usages with the input ranges [0, 1] and [-1, 1]
 * used through {@link GamepadEX} to enhance the power of the sticks and triggers
 */
@SuppressWarnings("unused")
public class ContinuousInput implements DoubleSupplier {
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

	public double getAsDouble() {
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
	 * begin the domain binding process to bind a command to this
	 *
	 * @return
	 */
	public DomainBindingBuilder<ContinuousInput> buildBinding() {
		return new DomainBindingBuilder<>(this);
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
