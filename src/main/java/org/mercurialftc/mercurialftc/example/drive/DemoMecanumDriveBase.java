package org.mercurialftc.mercurialftc.example.drive;

import com.qualcomm.robotcore.hardware.*;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.triggers.gamepadex.ContinuousInput;
import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.EncoderTicksConverter;
import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;
import org.mercurialftc.mercurialftc.silversurfer.followable.MotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.follower.FFMecanumWaveFollower;
import org.mercurialftc.mercurialftc.silversurfer.follower.GVFWaveFollower;
import org.mercurialftc.mercurialftc.silversurfer.follower.MecanumDriveBase;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.tracker.InsistentThreeWheelTracker;
import org.mercurialftc.mercurialftc.silversurfer.tracker.TrackerConstants;
import org.mercurialftc.mercurialftc.silversurfer.tracker.TwoWheelTracker;
import org.mercurialftc.mercurialftc.silversurfer.voltageperformanceenforcer.VoltagePerformanceEnforcer;
import org.mercurialftc.mercurialftc.util.hardware.Encoder;
import org.mercurialftc.mercurialftc.util.hardware.IMU_EX;
import org.mercurialftc.mercurialftc.util.hardware.cachinghardwaredevice.CachingDcMotorEX;

/**
 * A demonstration of a mecanum drive base implementation that will work in both auto and teleop,
 * with comments to show what needs to be replaced with a measured constant.
 * <p>This demo shows a field centric fully holonomic mecanum drivebase implementation,
 * with an x input, y input and an angular velocity input</p>
 * <p>For auto this shows a guided vector field implementation</p>
 */
public class DemoMecanumDriveBase extends MecanumDriveBase {
	public DemoMecanumDriveBase(OpModeEX opModeEX, Pose2D startPose, ContinuousInput x, ContinuousInput y, ContinuousInput t) {
		super(opModeEX, startPose, x, y, t);
	}

	@Override
	public void initialiseConstants() {
		// change the names of the motors as required
		fl = new CachingDcMotorEX(opModeEX.hardwareMap.get(DcMotorEx.class, "fl"));
		fl.setDirection(DcMotorSimple.Direction.REVERSE); // set the required motors to reverse
		bl = new CachingDcMotorEX(opModeEX.hardwareMap.get(DcMotorEx.class, "bl"));
		bl.setDirection(DcMotorSimple.Direction.REVERSE);
		br = new CachingDcMotorEX(opModeEX.hardwareMap.get(DcMotorEx.class, "br"));
		fr = new CachingDcMotorEX(opModeEX.hardwareMap.get(DcMotorEx.class, "fr"));

		VoltageSensor voltageSensor = opModeEX.hardwareMap.getAll(VoltageSensor.class).iterator().next();

		// replace these values
		VoltagePerformanceEnforcer translationalEnforcer = new VoltagePerformanceEnforcer(
				12.8,
				1.3,
				10
		);

		// replace these values
		VoltagePerformanceEnforcer angularEnforcer = new VoltagePerformanceEnforcer(
				12.8,
				1.3,
				10
		);

		// replace these values
		VoltagePerformanceEnforcer rotationalEnforcer = new VoltagePerformanceEnforcer(
				12.8,
				1.3,
				10
		);

		double currentVoltage = voltageSensor.getVoltage();

		// replace accelerations
		MotionConstants motionConstants = new MotionConstants(
				translationalEnforcer.transformVelocity(currentVoltage),
				angularEnforcer.transformVelocity(currentVoltage),
				rotationalEnforcer.transformVelocity(currentVoltage),
				10,
				10,
				10
		);

		// replace all, select the tracker which works best for you


		// insistent three wheeled tracker

//		tracker = new InsistentThreeWheelTracker(
//				startPose, // needs to be set to the starting pose, which should be the same pose as set for the wave builder
//				new TrackerConstants.ThreeWheelTrackerConstants(
//						Units.MILLIMETER,
//						377.26535034179676, // replace with your own measured constants
//						-172.5, // replace with your own measured constants,
//						1, // tune
//						1, // tune
//						new EncoderTicksConverter(8192/(Math.PI * 35), Units.MILLIMETER), // replace with your own measured constants
//						new EncoderTicksConverter(8192/(Math.PI * 35), Units.MILLIMETER), // replace with your own measured constants
//						new EncoderTicksConverter(8192/(Math.PI * 35), Units.MILLIMETER) // replace with your own measured constants
//				),
//				new Encoder(br).setDirection(Encoder.Direction.FORWARD), // check that each encoder increases in the positive direction, if not change their directions!
//				new Encoder(fr).setDirection(Encoder.Direction.REVERSE),
//				new Encoder(fl).setDirection(Encoder.Direction.FORWARD),
//				new IMU_EX(opModeEX.hardwareMap.get(IMU.class, "imu"), AngleUnit.RADIANS)
//		);

		// two wheeled tracker

		tracker = new TwoWheelTracker(
				startPose, // needs to be set to the starting pose, which should be the same pose as set for the wave builder
				new TrackerConstants.TwoWheelTrackerConstants(
						Units.MILLIMETER,
						-172.5, // replace with your own measured constants,
						1, // tune
						1, // tune
						new EncoderTicksConverter(8192 / (Math.PI * 35), Units.MILLIMETER), // replace with your own measured constants
						new EncoderTicksConverter(8192 / (Math.PI * 35), Units.MILLIMETER) // replace with your own measured constants
				),
				new Encoder(br).setDirection(Encoder.Direction.FORWARD), // check that each encoder increases in the positive direction, if not change their directions!
				new Encoder(fr).setDirection(Encoder.Direction.REVERSE),
				new IMU_EX(opModeEX.hardwareMap.get(IMU.class, "imu"), AngleUnit.RADIANS)
		);

		// these need to be used in other places, so need to be like this set, unlike most other constants
		// look at FFMecanumWaveFollower to see what these values represent
		// use the Units enum to use your desired units, and convert them to millimeters
		trackwidth = Units.MILLIMETER.toMillimeters(415); // replace
		wheelbase = Units.MILLIMETER.toMillimeters(330); // replace

		waveFollower = new GVFWaveFollower(
				tracker,
				new FFMecanumWaveFollower(
						motionConstants,
						true, // the GVFWaveFollower will pre rotate for us
						trackwidth,
						wheelbase,
						fl, bl, br, fr
				)
		);
	}
}
