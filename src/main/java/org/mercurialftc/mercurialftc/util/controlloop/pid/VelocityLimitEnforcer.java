package org.mercurialftc.mercurialftc.util.controlloop.pid;

public class VelocityLimitEnforcer extends LimitEnforcer<Double>{
	
	public VelocityLimitEnforcer(Double upperLimit, Double lowerLimit) {
		super(upperLimit, lowerLimit);
	}

	
	/**
	 * Returns a position mapped from the input percentile range onto the set upper and lower limits.
	 * Weighted so that the input of 0.0 will always return 0. (i.e. the upper and lower mappings are distinct)
	 * <p>{@link #percentileMapping(double)} for the unweighted version</p>
	 *
	 * @param percentileInput input in the range [1.0, -1.0]
	 * @return the input mapped onto the limits
	 */
	public Double weightedPercentileMapping(double percentileInput) {
		percentileInput = Math.max(-1.0, Math.min(1.0, percentileInput));
		if(percentileInput > 0) {
			return percentileInput * upperLimit;
		}
		return percentileInput * lowerLimit;
	}
	
	/**
	 * Returns a position mapped from the input percentile range onto the set upper and lower limits.
	 * <p>{@link #weightedPercentileMapping(double)} for the weighted version</p>
	 *
	 * @param percentileInput input in the range [1.0, -1.0]
	 * @return the input mapped onto the limits
	 */
	public Double percentileMapping(double percentileInput) {
		percentileInput = Math.max(-1.0, Math.min(1.0, percentileInput));
		return (percentileInput * (upperLimit - lowerLimit)) + lowerLimit;
	}
	
	/**
	 * ensures that the input is within the set bounds
	 * @param input the target velocity
	 * @return the target velocity ensured to fit within the bounds
	 */
	public Double rawInput(Double input) {
		return Math.max(lowerLimit, Math.min(upperLimit, input));
	}
}
