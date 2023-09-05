package org.mercurialftc.mercurialftc.util.controlloop.ramsete;

public class RamseteConstraints {
	private final double beta;
	private final double zeta;
	
	public double getBeta() {
		return beta;
	}
	
	public double getZeta() {
		return zeta;
	}
	
	/**
	 * sets the ramsete constrains beta and zeta
	 * @param beta the beta (proportional) constraint, must be greater than 0
	 * @param zeta the zeta (dampening) constraint in the interval [0.0, 1.0]
	 */
	public RamseteConstraints(double beta, double zeta) {
		this.beta = Math.max(beta, Math.nextUp(0.0));
		this.zeta = Math.max(0.0, Math.min(1.0, zeta));
	}
}
