package org.mercurialftc.mercurialftc.util.controlloop.pid;

public class PositionLimitEnforcer extends LimitEnforcer<Integer>{
	public PositionLimitEnforcer(Integer upperLimit, Integer lowerLimit) {
		super(upperLimit, lowerLimit);
	}
	
	public PositionLimitEnforcer(Integer upperLimit) {
		this(upperLimit, 0);
	}
	
	/**
	 * Returns a position mapped from the input percentile range onto the set upper and lower limits.
	 * Weighted so that the input of 0.0 will always return 0. (i.e. the upper and lower mappings are distinct)
	 * <p>{@link #percentileMapping(double)} for the unweighted version</p>
	 *
	 * @param percentileInput input in the range [1.0, -1.0]
	 * @return the input mapped onto the limits. May be inaccurate due to the mapping of a double to an int.
	 */
	public Integer weightedPercentileMapping(double percentileInput) {
		percentileInput = Math.max(-1.0, Math.min(1.0, percentileInput));
		if(percentileInput > 0) {
			return (int) percentileInput * upperLimit;
		}
		return (int) percentileInput * lowerLimit;
	}
	
	/**
	 * Returns a position mapped from the input percentile range onto the set upper and lower limits.
	 * <p>{@link #weightedPercentileMapping(double)} for the weighted version</p>
	 *
	 * @param percentileInput input in the range [1.0, -1.0]
	 * @return the input mapped onto the limits. May be inaccurate due to the mapping of a double to an int.
	 */
	public Integer percentileMapping(double percentileInput) {
		percentileInput = Math.max(-1.0, Math.min(1.0, percentileInput));
		return Math.round((float) percentileInput * (upperLimit - lowerLimit)) + lowerLimit;
	}
	
	/**
	 * ensures that the input is within the set bounds
	 * @param input the target position
	 * @return the target position ensured to fit within the bounds
	 */
	public Integer rawInput(Integer input) {
		return Math.max(lowerLimit, Math.min(upperLimit, input));
	}
}
