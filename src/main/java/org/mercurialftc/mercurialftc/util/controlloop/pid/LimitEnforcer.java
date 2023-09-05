package org.mercurialftc.mercurialftc.util.controlloop.pid;

public abstract class LimitEnforcer<N extends Number> {
	protected N upperLimit;
	protected N lowerLimit;
	
	
	public final void setUpperLimit(N upperLimit) {
		this.upperLimit = upperLimit;
	}
	
	public final void setLowerLimit(N lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	
	protected LimitEnforcer(N upperLimit, N lowerLimit) {
		this.upperLimit = upperLimit;
		this.lowerLimit = lowerLimit;
	}
	
	public final N getLowerLimit() {
		return lowerLimit;
	}
	
	public final N getUpperLimit() {
		return upperLimit;
	}
	
	/**
	 * Returns a position mapped from the input percentile range onto the set upper and lower limits.
	 * Weighted so that the input of 0.0 will always return 0. (i.e. the upper and lower mappings are distinct)
	 * <p>{@link #percentileMapping(double)} for the unweighted version</p>
	 *
	 * @param percentileInput input in the range [1.0, -1.0]
	 * @return the input mapped onto the limits.
	 */
	public abstract N weightedPercentileMapping(double percentileInput);
	
	/**
	 * Returns a position mapped from the input percentile range onto the set upper and lower limits.
	 * <p>{@link #weightedPercentileMapping(double)} for the weighted version</p>
	 *
	 * @param percentileInput input in the range [1.0, -1.0]
	 * @return the input mapped onto the limits.
	 */
	public abstract N percentileMapping(double percentileInput);
	
	/**
	 * ensures that the input is within the set bounds
	 * @param input the target position
	 * @return the target position ensured to fit within the bounds
	 */
	public abstract N rawInput(N input);
}
