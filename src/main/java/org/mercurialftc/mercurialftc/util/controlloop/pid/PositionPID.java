package org.mercurialftc.mercurialftc.util.controlloop.pid;

import com.qualcomm.robotcore.hardware.DcMotor;

public class PositionPID extends PID {
	
	private final PositionLimitEnforcer positionLimitEnforcer;
	private final PIDCoefficients.PCoefficients PCoefficients;
	private final VelocityPID PIDVelocityControlLoop;
	protected int target;
	
	public final int getTarget() {
		return target;
	}
	
	/**
	 * Ensures that the input is within the set bounds
	 * @param target the target position
	 */
	public void setTarget(int target) {
		this.target = positionLimitEnforcer.rawInput(target);
	}
	
	/**
	 * Maps a position from the input percentile range onto the set upper and lower limits.
	 * <p>{@link #setTargetWeightedPercentileMapping(double)} for the weighted version</p>
	 *
	 * @param percentileInput input in the range [1.0, -1.0]
	 */
	public void setTargetPercentileMapping(double percentileInput) {
		this.target = positionLimitEnforcer.percentileMapping(target);
	}
	
	/**
	 * Maps a position from the input percentile range onto the set upper and lower limits.
	 * Weighted so that the input of 0.0 will always return 0. (i.e. the upper and lower mappings are distinct)
	 * <p>{@link #setTargetPercentileMapping(double)} for the unweighted version</p>
	 *
	 * @param percentileInput input in the range [1.0, -1.0]
	 */
	public void setTargetWeightedPercentileMapping(double percentileInput) {
		this.target = positionLimitEnforcer.weightedPercentileMapping(target);
	}
	
	/**
	 * Constructs a new PIDPositionControlLoop that uses a P controller to determine a target velocity to achieve the target position, and a PIDVelocityControlLoop to reach the target velocity.
	 *
	 * @param PCoefficients The P coefficient for the position control
	 * @param PIDCoefficients The PID coefficients for the internal velocity control
	 * @param motor The motor to be used
	 * @param positionLimitEnforcer The position limit enforcer to be used to reinforce range limitations
	 */
	public PositionPID(PIDCoefficients.PCoefficients PCoefficients, PIDCoefficients PIDCoefficients, DcMotor motor, PositionLimitEnforcer positionLimitEnforcer) {
		super(PIDCoefficients, motor);
		this.positionLimitEnforcer = positionLimitEnforcer;
		this.PCoefficients = PCoefficients;
		this.PIDVelocityControlLoop = new VelocityPID(
				PIDCoefficients,
				motor,
				new VelocityLimitEnforcer(Double.MAX_VALUE, Double.MIN_VALUE) // TODO: Test this
		);
	}
	
	@Override
	protected double internalGetOutput() {
		double target = PCoefficients.getkP() * getCurrentError();
		PIDVelocityControlLoop.setTarget(target);
		return PIDVelocityControlLoop.internalGetOutput();
	}
	
	@Override
	protected double getCurrentErrorInternal() {
		return target - encoder.getCurrentPosition();
	}
}
