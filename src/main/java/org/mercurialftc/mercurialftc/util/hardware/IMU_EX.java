package org.mercurialftc.mercurialftc.util.hardware;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.Angle;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.AngleDegrees;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.AngleRadians;
import org.mercurialftc.mercurialftc.silversurfer.tracker.HeadingSupplier;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

/**
 * <h3>An IMU wrapper</h3>
 * Overwrites {@link #getRobotYawPitchRollAngles} and allows the easy resetting of each angle,
 * with a quick access to each angle also available.
 * <p>Designed to be used in {@link ScheduledIMU_EX} but also can be used separately</p>
 */
public class IMU_EX implements IMU, HeadingSupplier {
	private final Angle offsetPitch;
	private final Angle offsetRoll;
	private final Angle offsetYaw;
	private final AngleUnit angleUnit;
	private final IMU imu;
	private final Angle pitch;
	private final Angle roll;
	private final Angle yaw;
	private long acquisitionTime;

	/**
	 * @param angleUnit the angle unit that this class should return all values in by default
	 */
	public IMU_EX(IMU imu, AngleUnit angleUnit) {
		this.imu = imu;
		this.angleUnit = angleUnit;
		if (angleUnit == AngleUnit.DEGREES) {
			yaw = new AngleDegrees(0);
			pitch = new AngleDegrees(0);
			roll = new AngleDegrees(0);

			offsetYaw = new AngleDegrees(0);
			offsetPitch = new AngleDegrees(0);
			offsetRoll = new AngleDegrees(0);
		} else {
			yaw = new AngleRadians(0);
			pitch = new AngleRadians(0);
			roll = new AngleRadians(0);

			offsetYaw = new AngleRadians(0);
			offsetPitch = new AngleRadians(0);
			offsetRoll = new AngleRadians(0);
		}
	}

	public Angle getPitch() {
		return pitch.subtract(offsetPitch);
	}

	public Angle getRoll() {
		return roll.subtract(offsetRoll);
	}

	public Angle getYaw() {
		return yaw.subtract(offsetYaw);
	}

	/**
	 * resets roll, pitch and yaw
	 */
	public void resetIMU() {
		resetRoll();
		resetPitch();
		resetYaw();
	}

	/**
	 * updates the offset value
	 */
	public void resetPitch() {
		offsetPitch.setTheta(pitch.getTheta());
	}

	/**
	 * resets the yaw using {@link IMU#resetYaw()} and by updating the offset
	 */
	public void resetYaw() {
		imu.resetYaw();
		offsetYaw.setTheta(yaw.getTheta());
	}

	/**
	 * updates the offset value
	 */
	public void resetRoll() {
		offsetRoll.setTheta(roll.getTheta());
	}

	/**
	 * Initializes the IMU with non-default settings.
	 *
	 * @param parameters
	 * @return Whether initialization succeeded.
	 */
	@Override
	public boolean initialize(Parameters parameters) {
		return imu.initialize(parameters);
	}

	/**
	 * for bulk reads, called automatically in the {@link ScheduledIMU_EX#periodic()}
	 */
	public void readIMU() {
		YawPitchRollAngles angles = imu.getRobotYawPitchRollAngles();
		yaw.setTheta(angles.getYaw(angleUnit));
		pitch.setTheta(angles.getPitch(angleUnit));
		roll.setTheta(angles.getRoll(angleUnit));
		acquisitionTime = angles.getAcquisitionTime();
	}

	/**
	 * for the imu use cases that this doesn't cover
	 *
	 * @return the imu that IMU_EX wraps around
	 */
	public IMU getImu() {
		return imu;
	}

	/**
	 * @return A {@link YawPitchRollAngles} object representing the current orientation of the robot
	 * last updated when {@link #readIMU()} was last called.
	 */
	@Override
	public YawPitchRollAngles getRobotYawPitchRollAngles() {
		if (angleUnit == AngleUnit.DEGREES) {
			return new YawPitchRollAngles(angleUnit, getYaw().getDegrees(), getPitch().getDegrees(), getRoll().getDegrees(), acquisitionTime);
		}
		return new YawPitchRollAngles(angleUnit, getYaw().getRadians(), getPitch().getRadians(), getRoll().getRadians(), acquisitionTime);
	}

	/**
	 * @param reference
	 * @param order
	 * @param angleUnit
	 * @return An {@link Orientation} object representing the current orientation of the robot
	 * relative to the robot's position the last time that {@link #resetYaw()} was called,
	 * as if the robot was perfectly level at that time.
	 * <p><p>
	 * The {@link Orientation} class provides many ways to represent the robot's orientation,
	 * which is helpful for advanced use cases. Most teams should use {@link #getRobotYawPitchRollAngles()}.
	 * <p>
	 * NOTE: this has not been modified by this IMU_EX wrapper
	 */
	@Override
	public Orientation getRobotOrientation(AxesReference reference, AxesOrder order, AngleUnit angleUnit) {
		return imu.getRobotOrientation(reference, order, angleUnit);
	}

	/**
	 * @return A {@link Quaternion} object representing the current orientation of the robot
	 * relative to the robot's position the last time that {@link #resetYaw()} was called,
	 * as if the robot was perfectly level at that time.
	 * <p><p>
	 * Quaternions provide an advanced way to access orientation data that will work well
	 * for any orientation of the robot, even where other types of orientation data would
	 * encounter issues such as gimbal lock.
	 */
	@Override
	public Quaternion getRobotOrientationAsQuaternion() {
		return imu.getRobotOrientationAsQuaternion();
	}

	/**
	 * @param angleUnit
	 * @return The angular velocity of the robot (how fast it's turning around the three axes).
	 */
	@Override
	public AngularVelocity getRobotAngularVelocity(AngleUnit angleUnit) {
		return imu.getRobotAngularVelocity(angleUnit);
	}

	/**
	 * Returns an indication of the manufacturer of this device.
	 *
	 * @return the device's manufacturer
	 */
	@Override
	public Manufacturer getManufacturer() {
		return imu.getManufacturer();
	}

	/**
	 * Returns a string suitable for display to the user as to the type of device.
	 * Note that this is a device-type-specific name; it has nothing to do with the
	 * name by which a user might have configured the device in a robot configuration.
	 *
	 * @return device manufacturer and name
	 */
	@Override
	public String getDeviceName() {
		return imu.getDeviceName();
	}

	/**
	 * Get connection information about this device in a human readable format
	 *
	 * @return connection info
	 */
	@Override
	public String getConnectionInfo() {
		return imu.getConnectionInfo();
	}

	/**
	 * Version
	 *
	 * @return get the version of this device
	 */
	@Override
	public int getVersion() {
		return imu.getVersion();
	}

	/**
	 * Resets the device's configuration to that which is expected at the beginning of an OpMode.
	 * For example, motors will reset the direction to 'forward'.
	 */
	@Override
	public void resetDeviceConfigurationForOpMode() {
		imu.resetDeviceConfigurationForOpMode();
	}

	/**
	 * Closes this device
	 */
	@Override
	public void close() {
		imu.close();
	}

	/**
	 * implementations are recommended to supply a {@link AngleRadians} if possible
	 *
	 * @return the current heading of the robot
	 */
	@Override
	public Angle getHeading() {
		return getYaw();
	}

	/**
	 * <p>can be called by consumers that need to ensure that an update is run regardless of if the implementation is self updating or not</p>
	 * may be left blank by implementation if updates are not required, or are handled differently.
	 */
	@Override
	public void updateHeading() {
		readIMU();
	}

	@Override
	public void resetHeading() {
		resetYaw();
	}

	@Override
	public void resetHeading(@NotNull Angle heading) {
		resetHeading();
		offsetYaw.setTheta(offsetYaw.getTheta() - heading.getRadians());
	}
}