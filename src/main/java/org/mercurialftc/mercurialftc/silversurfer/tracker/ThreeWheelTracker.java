package org.mercurialftc.mercurialftc.silversurfer.tracker;

import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.util.hardware.Encoder;

public class ThreeWheelTracker extends WheeledTracker {
	private final Encoder left, right, middle;
	private double deltaLeft, deltaRight, deltaMiddle;

	public ThreeWheelTracker(Pose2D initialPose, WheeledTrackerConstants.ThreeWheeledTrackerConstants trackerConstants, Encoder left, Encoder right, Encoder middle) {
		super(initialPose, trackerConstants);
		this.left = left;
		this.right = right;
		this.middle = middle;
	}

	/**
	 * called once per cycle, to prevent making too many calls to an encoder, etc
	 */
	@Override
	protected void updateValues() {
		left.updateVelocity();
		right.updateVelocity();
		middle.updateVelocity();

		WheeledTrackerConstants trackerConstants = getTrackerConstants();

		deltaLeft = trackerConstants.getLeftTicksConverter().toUnits(left.getVelocityDataPacket().getDeltaPosition(), Units.MILLIMETER);
		deltaRight = trackerConstants.getRightTicksConverter().toUnits(right.getVelocityDataPacket().getDeltaPosition(), Units.MILLIMETER);
		deltaMiddle = trackerConstants.getMiddleTicksConverter().toUnits(middle.getVelocityDataPacket().getDeltaPosition(), Units.MILLIMETER);
	}

	/**
	 * @return the change in center displacement in millimeters
	 */
	@Override
	protected double findDeltaY() {
		return (deltaLeft + deltaRight) / 2;
	}

	/**
	 * @return the change in horizontal displacement with correction for forward offset in millimeters
	 */
	@Override
	protected double findDeltaX() {
		return deltaMiddle + (getTrackerConstants().getCenterOfRotationOffset().getX() * findDeltaTheta());
	}

	/**
	 * @return the change in heading in radians
	 */
	@Override
	protected double findDeltaTheta() {
		return (deltaRight - deltaLeft) / ((WheeledTrackerConstants.ThreeWheeledTrackerConstants) getTrackerConstants()).getTrackWidth();
	}

	@Override
	public void reset() {
		super.reset();
		left.reset();
		right.reset();
		middle.reset();
	}

	/**
	 * enforce certain measurements, if an external measurement can be relied upon, gets automatically run every insist frequency cycles
	 */
	@Override
	protected void insist() {

	}
}
