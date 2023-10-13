package org.mercurialftc.mercurialftc.silversurfer.followable;

import org.mercurialftc.mercurialftc.scheduler.commands.CommandSignature;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;

import java.util.ArrayList;

@SuppressWarnings("unused")
public abstract class FollowableBuilder {
	private final ArrayList<MecanumMotionConstants> motionConstantsArray;
	private MecanumMotionConstants motionConstants; // the motion constants used to build all current segments

	protected FollowableBuilder(MecanumMotionConstants motionConstants) {
		this.motionConstantsArray = new ArrayList<>();
		this.motionConstants = motionConstants;
	}

	protected ArrayList<MecanumMotionConstants> getMotionConstantsArray() {
		return motionConstantsArray;
	}

	protected MecanumMotionConstants getMotionConstants() {
		return motionConstants;
	}

	public void setMotionConstants(MecanumMotionConstants motionConstants) {
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
	 * sets a callback command to occur with a timed offset reference to the end of the instruction before it.
	 *
	 * @param offset        a relative offset, in seconds
	 * @param markerReached the command to run
	 */
	protected abstract void addOffsetCommandMarker(double offset, Marker.MarkerType markerType, CommandSignature markerReached);
}
