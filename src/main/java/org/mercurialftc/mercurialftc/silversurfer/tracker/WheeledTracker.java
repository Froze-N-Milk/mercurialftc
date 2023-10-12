package org.mercurialftc.mercurialftc.silversurfer.tracker;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.AngleRadians;
import org.mercurialftc.mercurialftc.silversurfer.geometry.matrix.SimpleMatrix;

/**
 * tracks robot position in millimeters and radians
 */
@SuppressWarnings("unused")
public abstract class WheeledTracker implements Tracker {
	private final Pose2D initialPose2D;
	private final WheeledTrackerConstants trackerConstants;
	private Pose2D pose2D, previousPose2D;
	private Vector2D deltaPositionVector;
	private int insistIndex, insistFrequency;

	public WheeledTracker(@NotNull Pose2D initialPose, WheeledTrackerConstants trackerConstants) {
		this.pose2D = initialPose;
		this.initialPose2D = initialPose;

		this.trackerConstants = trackerConstants;

		insistFrequency = 0;
		insistIndex = 0;

		this.previousPose2D = pose2D;
		this.deltaPositionVector = new Vector2D();
	}

	public Vector2D getDeltaPositionVector() {
		return deltaPositionVector;
	}

	public Pose2D getInitialPose2D() {
		return initialPose2D;
	}

	/**
	 * {@link #updatePose()} must be called frequently for this value to be accurate
	 *
	 * @return the current pose of the robot as estimated by the tracker
	 */
	public Pose2D getPose2D() {
		return pose2D;
	}

	public final void setPose2D(Pose2D pose2D) {
		this.pose2D = pose2D;
	}

	public Pose2D getPreviousPose2D() {
		return previousPose2D;
	}

	/**
	 * must be called frequently for the pose to be accurate
	 */
	public void updatePose() {
		deltaPositionVector = pose2D.toVector2D().subtract(previousPose2D.toVector2D());
		previousPose2D = pose2D;
		updateValues();

		double cos = Math.cos(pose2D.getTheta().getRadians());
		double sin = Math.sin(pose2D.getTheta().getRadians());
		SimpleMatrix rotationMatrix = new SimpleMatrix(
				new double[][]{
						{cos, -sin, 0},
						{sin, cos, 0},
						{0, 0, 1}
				}
		);

		double dt = findDeltaTheta();
		double dc = findDeltaXc();
		double dp = findDeltaXp();

		double term0 = (Math.sin(dt) / dt); // approaches 1 as dt approaches 0
		double term1 = (1 - Math.cos(dt)) / dt; // approaches 0 as dt approaches 0

		if (dt == 0) {
			term0 = 1; // approaches 1 as dt approaches 0
			term1 = 0; // approaches 0 as dt approaches 0
		}

		SimpleMatrix twistMatrix = new SimpleMatrix(
				new double[][]{
						{term0, -term1, 0},
						{term1, term0, 0},
						{0, 0, 1}
				}
		);

		SimpleMatrix inputMatrix = new SimpleMatrix(
				new double[][]{
						{dp},
						{dc},
						{dt}
				}
		);

		/*
		{
			{(cos * term0 + (- sin) * term1), (cos * term2 + (- sin) * term0)},
			{(sin * term0 + cos * term1), (sin * term2 + cos * term0)}
		}

		{
			dc * (cos * term0 + (- sin) * term1) + dp * (cos * term2 + (- sin) * term0),
			dc * (sin * term0 + cos * term1) + dp * (sin * term2 + cos * term0)}
		}
		 */

//		double d//		double deltaY = dp * (sin * term0 + cos * term1) + dc * (sin * term2 + cos * term0);eltaX = dp * (cos * term0 - sin * term1) + dc * (cos * term2 - sin * term0);

		SimpleMatrix twistResult = rotationMatrix.multiply(twistMatrix).multiply(inputMatrix);

		pose2D = pose2D.add(twistResult.getItem(0, 0) * trackerConstants.getXMult(), twistResult.getItem(1, 0) * trackerConstants.getYMult(), new AngleRadians(twistResult.getItem(2, 0)));

		if (insistFrequency > 0) {
			if (insistIndex == 0) {
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

	public WheeledTrackerConstants getTrackerConstants() {
		return trackerConstants;
	}

	/**
	 * enforce certain measurements, if an external measurement can be relied upon, gets automatically run every {@link #insistFrequency} cycles
	 */
	protected abstract void insist();

	protected final void setInsistFrequency(int frequency) {
		this.insistFrequency = frequency;
	}
}
