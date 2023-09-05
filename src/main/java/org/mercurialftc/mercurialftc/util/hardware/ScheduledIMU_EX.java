package org.mercurialftc.mercurialftc.util.hardware;

import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.subsystems.Subsystem;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Angle;
import org.mercurialftc.mercurialftc.silversurfer.tracker.HeadingSupplier;
import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.silversurfer.geometry.AngleRadians;

/**
 * <h3>A {@link Subsystem} implementation of {@link IMU_EX}</h3>
 * Designed to work well with the {@link Scheduler} used in
 * {@link OpModeEX} op modes.
 * <p>Implementation into any {@link OpModeEX} should be quick and seamless, with full performant integration.</p>
 */
public class ScheduledIMU_EX extends Subsystem implements HeadingSupplier {
	public IMU_EX getImuEX() {
		return imuEX;
	}
	
	private IMU_EX imuEX;
	private final IMU.Parameters parameters;
	private final AngleUnit angleUnit;
	private final String imuName;
	
	public ScheduledIMU_EX(OpModeEX opModeEX, String imuName, IMU.Parameters parameters, AngleUnit angleUnit) {
		super(opModeEX);
		this.parameters = parameters;
		this.angleUnit = angleUnit;
		this.imuName = imuName;
	}
	
	public ScheduledIMU_EX(OpModeEX opModeEX, IMU.Parameters parameters, AngleUnit angleUnit) {
		this(opModeEX, "imu", parameters, angleUnit);
	}
	
	/**
	 * The code to be run when the OpMode is initialised.
	 */
	@Override
	public void init() {
		imuEX = new IMU_EX(opModeEX.hardwareMap.get(IMU.class, imuName), angleUnit);
		imuEX.initialize(parameters);
		imuEX.readIMU();
		imuEX.resetIMU();
	}
	
	/**
	 * The method that is ran at the start of every loop to facilitate encoder reads
	 * and any other calculations that need to be ran every loop regardless of the command
	 */
	@Override
	public void periodic() {
		imuEX.readIMU();
	}
	
	/**
	 * The default command run by a subsystem
	 */
	@Override
	public void defaultCommandExecute() {
	
	}
	
	/**
	 * methods to be run when the subsystem is no longer used,
	 * for instance when the option to close the subsystem is implemented at the end of an OpMode,
	 * or when a new scheduler instance is forced.
	 */
	@Override
	public void close() {
		imuEX.close();
	}
	
	/**
	 * implementations are recommended to supply a {@link AngleRadians} if possible
	 *
	 * @return the current heading of the robot
	 */
	@Override
	public Angle getHeading() {
		return imuEX.getYaw();
	}
	
	/**
	 * does nothing, as this is self updating
	 */
	@Override
	public void updateHeading() {
		// no need
	}
}
