package org.mercurialftc.mercurialftc.silversurfer.tracker;

import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Angle;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.util.hardware.Encoder;

public class TwoWheelTracker extends Tracker {
	private final Encoder left, middle;
	private final HeadingSupplier headingSupplier;
	private Angle currentTheta;
	
	public TwoWheelTracker(Pose2D initialPose, TrackerConstants.TwoWheelTrackerConstants trackerConstants, Encoder left, Encoder middle, HeadingSupplier headingSupplier) {
		super(initialPose, trackerConstants);
		this.left = left;
		this.middle = middle;
		this.headingSupplier = headingSupplier;
		previousTheta = headingSupplier.getHeading().getRadians();
	}
	
	private double deltaLeft, deltaMiddle, deltaTheta, previousTheta;
	
	
	/**
	 * called once per cycle, to prevent making too many calls to an encoder, etc
	 */
	@Override
	protected void updateValues() {
		left.updateVelocity();
		middle.updateVelocity();
		headingSupplier.updateHeading();
		
		TrackerConstants trackerConstants = getTrackerConstants();
		
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
	
	/**
	 * @return the change in heading in radians
	 */
	@Override
	protected double findDeltaTheta() {
		return deltaTheta;
	}
	
	/**
	 * enforce certain measurements, if an external measurement can be relied upon, gets automatically run every
	 */
	@Override
	protected void insist() {
		Pose2D currentPose = getPose2D();
		if(currentPose.getTheta().getRadians() != currentTheta.getRadians()) {
			setPose2D(new Pose2D(currentPose.getX(), currentPose.getY(), currentTheta));
		}
	}
}
