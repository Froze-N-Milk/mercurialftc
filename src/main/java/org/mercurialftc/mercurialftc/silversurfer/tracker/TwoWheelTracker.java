package org.mercurialftc.mercurialftc.silversurfer.tracker;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.Angle;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.util.hardware.Encoder;

@SuppressWarnings("unused")
public class TwoWheelTracker extends WheeledTracker {
	private final Encoder left, middle;
	private final HeadingSupplier headingSupplier;
	private Angle currentTheta;
	private double deltaLeft, deltaMiddle, deltaTheta, previousTheta;

	@SuppressWarnings("unused")
	public TwoWheelTracker(Pose2D initialPose, WheeledTrackerConstants.TwoWheeledTrackerConstants trackerConstants, Encoder left, Encoder middle, @NotNull HeadingSupplier headingSupplier) {
		super(initialPose, trackerConstants);
		this.left = left;
		this.middle = middle;
		this.headingSupplier = headingSupplier;
		this.previousTheta = initialPose.getTheta().getRadians();
		setInsistFrequency(1);
		// sets the imu heading to the initial pose heading
		resetHeading(initialPose.getTheta());
	}

	/**
	 * called once per cycle, to prevent making too many calls to an encoder, etc
	 */
	@Override
	protected void updateValues() {
		left.updateVelocity();
		middle.updateVelocity();
		headingSupplier.updateHeading();

		WheeledTrackerConstants trackerConstants = getTrackerConstants();

		currentTheta = headingSupplier.getHeading();

		deltaLeft = trackerConstants.getLeftTicksConverter().toUnits(left.getVelocityDataPacket().getDeltaPosition(), Units.MILLIMETER);
		deltaMiddle = trackerConstants.getMiddleTicksConverter().toUnits(middle.getVelocityDataPacket().getDeltaPosition(), Units.MILLIMETER);
		deltaTheta = currentTheta.getRadians() - previousTheta;

		previousTheta = headingSupplier.getHeading().getRadians();
	}

	/**
	 * @return the change in center displacement in millimeters
	 */
	@Override
	protected double findDeltaXc() {
		return deltaLeft;
	}

	/**
	 * @return the change in horizontal displacement with correction for forward offset in millimeters
	 */
	@Override
	protected double findDeltaXp() {
		return deltaMiddle - (getTrackerConstants().getForwardOffset() * findDeltaTheta());
	}

	@Override
	public void reset() {
		super.reset();
		left.reset();
		middle.reset();
		resetHeading(super.getInitialPose2D().getTheta());
	}

	/**
	 * @return the change in heading in radians
	 */
	@Override
	protected double findDeltaTheta() {
		return deltaTheta;
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
		if (currentPose.getTheta().getRadians() != currentTheta.getRadians()) {
			setPose2D(new Pose2D(currentPose.getX(), currentPose.getY(), currentTheta));
		}
	}
}
