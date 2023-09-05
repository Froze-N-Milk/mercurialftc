package org.mercurialftc.mercurialftc.util.controlloop.pid;

public class PIDCoefficients {
	public double getkP() {
		return kP;
	}
	
	public double getkI() {
		return kI;
	}
	
	public double getkD() {
		return kD;
	}
	
	private final double kP, kI, kD;
	public PIDCoefficients(double kP, double kI, double kD) {
		this.kP = kP;
		this.kI = kI;
		this.kD = kD;
	}
	
	public static class PCoefficients extends PIDCoefficients {
		public PCoefficients(double kP) {
			super(kP, 0, 0);
		}
	}
	
	public static class PICoefficients extends PIDCoefficients {
		public PICoefficients(double kP, double kI) {
			super(kP, kI, 0);
		}
	}
	
	public static class PDCoefficients extends PIDCoefficients {
		public PDCoefficients(double kP, double kD) {
			super(kP, 0, kD);
		}
	}
}
