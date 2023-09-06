package org.mercurialftc.mercurialftc.silversurfer.followable;

import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;

import java.util.ArrayList;

public abstract class FollowableBuilder {
	private final ArrayList<MotionConstants> motionConstantsArray;
	private MotionConstants motionConstants; // the motion constants used to build all current segments

	protected ArrayList<MotionConstants> getMotionConstantsArray() {
		return motionConstantsArray;
	}

	public void setMotionConstants(MotionConstants motionConstants) {
		this.motionConstants = motionConstants;
	}

	protected MotionConstants getMotionConstants() {
		return motionConstants;
	}

	protected FollowableBuilder(MotionConstants motionConstants) {
		this.motionConstantsArray = new ArrayList<>();
		this.motionConstants = motionConstants;
	}

	public abstract Followable build();

	/**
	 * called every time that a segment is added
	 */
	private void updateMotionConstantsArray() {
		motionConstantsArray.add(motionConstants);
	}

	/**
	 * for internal use only
	 *
	 * @param previousPose
	 * @param destinationPose
	 */
	protected abstract void addSegment(Pose2D previousPose, Pose2D destinationPose);

	/**
	 * adds a segment to the followable, maintains current motion constants set.
	 *
	 * @param previousPose
	 * @param destinationPose
	 * @return
	 */
	public final FollowableBuilder addFollowableSegment(Pose2D previousPose, Pose2D destinationPose) {
		addSegment(previousPose, destinationPose);
		updateMotionConstantsArray();
		return this;
	}

	/**
	 * todo fill in
	 *
	 * @param offset
	 * @param markerReached
	 */
	protected abstract void addOffsetCommandMarker(double offset, Marker.MarkerType markerType, Command markerReached);
}
