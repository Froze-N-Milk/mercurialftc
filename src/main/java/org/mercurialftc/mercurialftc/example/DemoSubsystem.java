package org.mercurialftc.mercurialftc.example;

import org.mercurialftc.mercurialftc.util.hardware.cachinghardwaredevice.CachingDcMotorEX;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.subsystems.Subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class DemoSubsystem extends Subsystem {
	CachingDcMotorEX demoCachingMotorEX;
	private static final int TARGET_TPS = 200; // target ticks per second
	
	public DemoSubsystem(OpModeEX opModeEX) {
		super(opModeEX);
	}
	
	/**
	 * The code to be run when the OpMode is initialised.
	 */
	@Override
	public void init() {
		//initialising the caching motor
		demoCachingMotorEX = new CachingDcMotorEX(opModeEX.hardwareMap.get(DcMotorEx.class, "motor"));
		
		// resetting the encoder and then enabling run without encoder mode, so we have better control over it.
		demoCachingMotorEX.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		demoCachingMotorEX.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		
		// initialising the variables
		previousPosition = demoCachingMotorEX.getCurrentPosition();
		previousTime = opModeEX.getElapsedTime().seconds();
	}
	
	private double previousPosition;
	private double previousTime;
	private double previousOutput;
	
	/**
	 * The method that is ran at the start of every loop to facilitate encoder reads
	 * and any other calculations that need to be ran every loop regardless of the command
	 */
	@Override
	public void periodic() { //todo fix this up
		// These calculations should be run every loop, regardless of if the default command is on or off
		
		// reading the encoder at the start of the OpModeLoop for the bulk caching
		demoCachingMotorEX.getCurrentPosition();
		
		double currentTPS = (demoCachingMotorEX.getCurrentPosition() - previousPosition) / (opModeEX.getElapsedTime().seconds() - previousTime);
		double output = previousOutput * (TARGET_TPS / currentTPS);
		
		output = Math.max(-1.0, Math.min(1.0, output));
		
		previousPosition = demoCachingMotorEX.getCurrentPosition();
		previousTime = opModeEX.getElapsedTime().seconds();
		previousOutput = output;
	}
	
	/**
	 * The default command run by a subsystem
	 */
	@Override
	public void defaultCommandExecute() {
		demoCachingMotorEX.setPower(previousOutput); // this will have the value of output by the time it gets run
	}
	
	/**
	 * methods to be run when the subsystem is no longer used,
	 * for instance when the option to close the subsystem is implemented at the end of an OpMode,
	 * or when a new scheduler instance is forced.
	 */
	@Override
	public void close() {
	
	}
}
