package org.mercurialftc.mercurialftc.util.controlloop.pid;

import org.mercurialftc.mercurialftc.util.hardware.Encoder;
import com.qualcomm.robotcore.hardware.DcMotor;

public abstract class PID {

	protected double P, I, D;
	
	public final double getI_MAX() {
		return I_MAX;
	}
	
	public final void setI_MAX(double i_MAX) {
		I_MAX = i_MAX;
	}
	
	public final double getI_MIN() {
		return I_MIN;
	}
	
	public final void setI_MIN(double i_MIN) {
		I_MIN = i_MIN;
	}
	
	protected double I_MAX;
	protected double I_MIN;
	protected final Encoder encoder;
	protected final DcMotor motor;
	protected final PIDCoefficients PIDCoefficients;
	private double previousTime;
	private double deltaTime;
	private double deltaError;
	
	private double currentError;
	private double previousError;
	
	protected PID(PIDCoefficients PIDCoefficients, DcMotor motor) {
		this.PIDCoefficients = PIDCoefficients;
		
		this.motor = motor;
		this.encoder = new Encoder(motor);
		
		P = I = D = 0.0;
	}
	
	public final Encoder getEncoder() {
		return encoder;
	}
	
	public final DcMotor getMotor() {
		return motor;
	}
	
	public final PIDCoefficients getPIDFCoefficients() {
		return PIDCoefficients;
	}
	
	
	public final void update() {
		double currentTime = System.nanoTime() / 1e9;
		deltaTime = currentTime - previousTime;
		
		currentError = getCurrentErrorInternal();
		deltaError = currentError - previousError;
		
		motor.setPower(internalGetOutput());
		
		previousTime = currentTime;
		previousError = currentError;
	}
	
	protected abstract double internalGetOutput();
	protected abstract double getCurrentErrorInternal();
	
	
	protected final double getDeltaTime() {
		return deltaTime;
	}
	
	protected final double getCurrentError() {
		return currentError;
	}
	
	protected final double getPreviousError() {
		return previousError;
	}
	
	protected final double getDeltaError() {
		return deltaError;
	}
	
}
