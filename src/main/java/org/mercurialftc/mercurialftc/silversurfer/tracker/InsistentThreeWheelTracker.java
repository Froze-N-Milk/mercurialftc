package org.mercurialftc.mercurialftc.silversurfer.tracker;

import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.Angle;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.util.hardware.Encoder;
import org.mercurialftc.mercurialftc.util.hardware.IMU_EX;
import org.mercurialftc.mercurialftc.util.hardware.ScheduledIMU_EX;

@SuppressWarnings("unused")
public class InsistentThreeWheelTracker extends ThreeWheelTracker {
	private final HeadingSupplier headingSupplier;

	/**
	 * @param headingSupplier Either {@link IMU_EX} (recommended for if the IMU isn't being used for anything else, will save on loop time) or {@link ScheduledIMU_EX} (will read from the IMU every loop)
	 * @param insistFrequency how often the heading supplier is cross-checked to keep the heading accurate
	 */
	public InsistentThreeWheelTracker(Pose2D initialPose, TrackerConstants.ThreeWheelTrackerConstants trackerConstants, Encoder left, Encoder right, Encoder middle, HeadingSupplier headingSupplier, int insistFrequency) {
		super(initialPose, trackerConstants, left, right, middle);
		setInsistFrequency(insistFrequency);
		this.headingSupplier = headingSupplier;
		// sets the imu heading to the initial pose heading
		resetHeading(initialPose.getTheta());
	}

	/**
	 * constructs an InsistentThreeWheelTracker with an insist frequency of 10
	 *
	 * @param headingSupplier Either {@link IMU_EX} (recommended for if the IMU isn't being used for anything else, will save on loop time) or {@link ScheduledIMU_EX} (will read from the IMU every loop)
	 */
	public InsistentThreeWheelTracker(Pose2D initialPose, TrackerConstants.ThreeWheelTrackerConstants trackerConstants, Encoder left, Encoder right, Encoder middle, HeadingSupplier headingSupplier) {
		this(initialPose, trackerConstants, left, right, middle, headingSupplier, 10);
	}

	/**
	 * called once per cycle, to prevent making too many calls to an encoder, etc
	 */
	@Override
	protected void updateValues() {
		headingSupplier.updateHeading();
		super.updateValues();
	}

	@Override
	public void reset() {
		super.reset();
		resetHeading(super.getInitialPose2D().getTheta());
	}

	@Override
	public void resetHeading() {
		headingSupplier.resetHeading();
	}

	@Override
	public void resetHeading(Angle heading) {
		headingSupplier.resetHeading(heading);
	}

	/**
	 * enforce certain measurements, if an external measurement can be relied upon, gets automatically run every insist frequency cycles
	 */
	@Override
	protected void insist() {
		Pose2D currentPose = getPose2D();
		Angle currentTheta = headingSupplier.getHeading();
		if (currentPose.getTheta().getRadians() != currentTheta.getRadians()) {
			setPose2D(new Pose2D(currentPose.getX(), currentPose.getY(), currentTheta));
		}
	}
}
