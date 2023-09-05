package org.mercurialftc.mercurialftc.util.controlloop.feedforward;

public class FeedForwardConstants {
	private final double kV, kA;
	
	public double getkV() {
		return kV;
	}
	
	public double getkA() {
		return kA;
	}
	
	
	public FeedForwardConstants(double kV, double kA) {
		this.kV = kV;
		this.kA = kA;
	}
}
