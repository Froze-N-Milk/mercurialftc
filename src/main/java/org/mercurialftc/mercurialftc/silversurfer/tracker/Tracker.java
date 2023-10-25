package org.mercurialftc.mercurialftc.silversurfer.tracker;

import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.Angle;

@SuppressWarnings("unused")
public interface Tracker {

	Vector2D getDeltaPositionVector();

	/**
	 * @return the initial Pose2D of the tracker
	 */
	Pose2D getInitialPose2D();

	/**
	 * @return the current Pose2D measured by the tracker
	 */
	Pose2D getPose2D();

	/**
	 * sets the internal, for overriding purposes
	 *
	 * @param pose2D the new Pose2D
	 */
	void setPose2D(Pose2D pose2D);

	Pose2D getPreviousPose2D();

	/**
	 * runs the calculations to update the pose2D
	 */
	void updatePose();

	/**
	 * resets the current pose to be the initial pose
	 */
	default void reset() {
		setPose2D(getInitialPose2D());
	}

	/**
	 * resets the heading to 0 at this point
	 */
	default void resetHeading() {
		setPose2D(new Pose2D(getPose2D().getX(), getPose2D().getY(), 0));
	}

	/**
	 * resets the heading to the supplied Angle at this point
	 */
	default void resetHeading(Angle heading) {
		setPose2D(new Pose2D(getPose2D().getX(), getPose2D().getY(), heading));
	}

}
