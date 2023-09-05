package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.tracker.Tracker;
import org.mercurialftc.mercurialftc.util.controlloop.ramsete.RamseteController;
import org.mercurialftc.mercurialftc.util.hardware.cachinghardwaredevice.CachingDcMotor;

// todo remove
public class MecanumDriveBase { // todo update this
	private final RamseteController ramseteController; // todo change
	private final CachingDcMotor frontLeft, frontRight, backRight, backLeft;
	private final Tracker tracker;
	private final DriveConstants driveConstants;
	
	public MecanumDriveBase(RamseteController ramseteController, CachingDcMotor frontLeft, CachingDcMotor frontRight, CachingDcMotor backRight, CachingDcMotor backLeft, Tracker tracker, DriveConstants driveConstants) {
		this.ramseteController = ramseteController;
		this.frontLeft = frontLeft;
		this.frontRight = frontRight;
		this.backRight = backRight;
		this.backLeft = backLeft;
		this.tracker = tracker;
		this.driveConstants = driveConstants;
	}
	
	public void followPath() {
		Pose2D targetPose = new Pose2D();
		
		tracker.updatePose();
		Pose2D currentPose = tracker.getPose2D();
		
		double xError = targetPose.getX() - currentPose.getX();
		double yError = targetPose.getY() - currentPose.getY();
		double thetaError = targetPose.getTheta().getRadians() - currentPose.getTheta().getRadians();
		
		// TODO: maybe don't use this?? i cannot tell but it does seem right
		ramseteController.update(
				null,
				xError,
				yError,
				thetaError,
				currentPose.getTheta().getRadians()
		);
		
		double velocity = ramseteController.getV();
		double angularVelocity = ramseteController.getW();
		
		double currentHeading = currentPose.getTheta().getRadians();
		double cosCurrentHeading = Math.cos(currentHeading);
		double sinCurrentHeading = Math.sin(currentHeading);
		
		double strafeVelocity = cosCurrentHeading * velocity;
		double forwardVelocity = sinCurrentHeading * velocity;
		
		double trackWidthCompensatedAngularVelocity = driveConstants.getTrackWidth() * angularVelocity;
		
		double maxVelocity = driveConstants.getMaxVelocity();
		
		frontLeft.setPower((forwardVelocity - strafeVelocity - trackWidthCompensatedAngularVelocity) / maxVelocity);
		backLeft.setPower((forwardVelocity + strafeVelocity - trackWidthCompensatedAngularVelocity) / maxVelocity);
		
		backRight.setPower((forwardVelocity - strafeVelocity + trackWidthCompensatedAngularVelocity) / maxVelocity);
		frontRight.setPower((forwardVelocity + strafeVelocity + trackWidthCompensatedAngularVelocity) / maxVelocity);
	}
}
