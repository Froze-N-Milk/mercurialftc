package org.mercurialftc.mercurialftc.silversurfer.tracker;

import org.mercurialftc.mercurialftc.silversurfer.geometry.AngleRadians;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.util.matrix.SimpleMatrix;

/**
 * tracks robot position in millimeters and radians
 */
public abstract class Tracker {
	/**
	 * {@link #updatePose()} must be called frequently for this value to be accurate
	 * @return the current pose of the robot as estimated by the tracker
	 */
	public Pose2D getPose2D() {
		return pose2D;
	}
	
	private Pose2D pose2D;
	private final Pose2D initialPose2D;
	
	private final TrackerConstants trackerConstants;
	private final SimpleMatrix initialRotationMatrix;
	private int insistIndex, insistFrequency;
	public Tracker(Pose2D initialPose, TrackerConstants trackerConstants) {
		this.pose2D = initialPose;
		this.initialPose2D = initialPose;
		double cosInitial = Math.cos(initialPose.getTheta().getRadians());
		double sinInitial = Math.sin(initialPose.getTheta().getRadians());
		initialRotationMatrix = new SimpleMatrix(
				new double[][] {
						{cosInitial, -sinInitial, 0},
						{sinInitial, cosInitial, 0},
						{0, 0, 1}
				}
		);
		this.trackerConstants = trackerConstants;
		
		insistFrequency = 0;
		insistIndex = 0;
	}
	
	/**
	 * must be called frequently for it to be accurate
	 */
	public void updatePose() {
		updateValues();
		
		double dt = findDeltaTheta();
		double dc = findDeltaXc();
		double dp = findDeltaXp();
		
		double term0 = (Math.sin(dt) / dt); // approaches 1 as dt approaches 0
		double term1 = (1 - Math.cos(dt)) / dt; // approaches 0 as dt approaches 0
		double term2 = (Math.cos(dt) - 1) / dt; // approaches 0 as dt approaches 0
		
		if(dt == 0) {
			term0 = 1; // approaches 1 as dt approaches 0
			term1 = 0; // approaches 0 as dt approaches 0
			term2 = 0; // approaches 0 as dt approaches 0
		}
		
		SimpleMatrix twistMatrix = new SimpleMatrix(
				new double[][]{
						{term0, term2, 0},
						{term1, term0, 0},
						{0, 0, 1}
				}
		);
		
		SimpleMatrix inputMatrix = new SimpleMatrix(
				new double[][] {
						{dp},
						{dc},
						{dt}
				}
		);
		
		/*
		{
			{(cosInitial * term0 + (- sinInitial) * term1), (cosInitial * term2 + (- sinInitial) * term0)},
			{(sinInitial * term0 + cosInitial * term1), (sinInitial * term2 + cosInitial * term0)}
		}
		
		{
			dc * (cosInitial * term0 + (- sinInitial) * term1) + dp * (cosInitial * term2 + (- sinInitial) * term0),
			dc * (sinInitial * term0 + cosInitial * term1) + dp * (sinInitial * term2 + cosInitial * term0)}
		}
		 */
		
//		double d//		double deltaY = dp * (sinInitial * term0 + cosInitial * term1) + dc * (sinInitial * term2 + cosInitial * term0);eltaX = dp * (cosInitial * term0 - sinInitial * term1) + dc * (cosInitial * term2 - sinInitial * term0);
		
		SimpleMatrix twistResult = initialRotationMatrix.multiply(twistMatrix).multiply(inputMatrix);
		
		pose2D = pose2D.add(twistResult.getItem(0, 0), twistResult.getItem(1, 0), new AngleRadians(twistResult.getItem(2, 0)));
		
		if(insistFrequency > 0) {
			if(insistIndex == 0){
				insist();
			}
			insistIndex++;
			insistIndex %= insistFrequency;
		}
	}
	
	/**
	 * called once per cycle, to prevent making too many calls to an encoder, etc
	 */
	protected abstract void updateValues();
	
	/**
	 * @return the change in center displacement in millimeters
	 */
	protected abstract double findDeltaXc();
	/**
	 * @return the change in horizontal displacement with correction for forward offset in millimeters
	 */
	protected abstract double findDeltaXp();
	/**
	 * @return the change in heading in radians
	 */
	protected abstract double findDeltaTheta();
	
	public TrackerConstants getTrackerConstants() {
		return trackerConstants;
	}
	
	public void reset() {
		pose2D = initialPose2D;
	}
	
	protected final void setPose2D(Pose2D pose2D) {
		this.pose2D = pose2D;
	}
	
	/**
	 * enforce certain measurements, if an external measurement can be relied upon, gets automatically run every
	 */
	protected abstract void insist();
	
	protected final void setInsistFrequency(int frequency) {
		this.insistFrequency = frequency;
	}
}
