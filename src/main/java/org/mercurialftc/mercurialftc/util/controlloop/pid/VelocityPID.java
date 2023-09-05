package org.mercurialftc.mercurialftc.util.controlloop.pid;

import com.qualcomm.robotcore.hardware.DcMotor;

public class VelocityPID extends PID {
	private final VelocityLimitEnforcer velocityLimitEnforcer;
	protected double target;
	
	public final double getTarget() {
		return target;
	}
	
	/**
	 * Ensures that the input is within the set bounds
	 * @param target the target position
	 */
	public void setTarget(double target) {
		this.target = velocityLimitEnforcer.rawInput(target);
	}
	
	/**
	 * Maps a position from the input percentile range onto the set upper and lower limits.
	 * <p>{@link #setTargetWeightedPercentileMapping(double)} for the weighted version</p>
	 *
	 * @param percentileInput input in the range [1.0, -1.0]
	 */
	public void setTargetPercentileMapping(double percentileInput) {
		this.target = velocityLimitEnforcer.percentileMapping(target);
	}
	
	/**
	 * Maps a position from the input percentile range onto the set upper and lower limits.
	 * Weighted so that the input of 0.0 will always return 0. (i.e. the upper and lower mappings are distinct)
	 * <p>{@link #setTargetPercentileMapping(double)} for the unweighted version</p>
	 *
	 * @param percentileInput input in the range [1.0, -1.0]
	 */
	public void setTargetWeightedPercentileMapping(double percentileInput) {
		this.target = velocityLimitEnforcer.weightedPercentileMapping(target);
	}
	
	public VelocityPID(PIDCoefficients pidCoefficients, DcMotor motor, VelocityLimitEnforcer velocityLimitEnforcer) {
		super(pidCoefficients, motor);
		this.velocityLimitEnforcer = velocityLimitEnforcer;
	}
	
	@Override
	protected double internalGetOutput() {
		double currentError = getCurrentError();
		double deltaTime = getDeltaTime();
		
		P = getPIDFCoefficients().getkP() * currentError;
		I += getPIDFCoefficients().getkI() * (currentError * deltaTime);
		I = Math.max(I_MAX, Math.min(I_MIN, I));
		D = getPIDFCoefficients().getkD() * (getDeltaError() / deltaTime);
		
		return P + I + D;
	}
	
	@Override
	protected double getCurrentErrorInternal() {
		return target - encoder.getVelocityDataPacket().getVelocity();
	}
	
}
