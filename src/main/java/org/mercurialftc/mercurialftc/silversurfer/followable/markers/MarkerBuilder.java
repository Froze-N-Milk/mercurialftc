package org.mercurialftc.mercurialftc.silversurfer.followable.markers;

import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.FollowableCurve;
import org.mercurialftc.mercurialftc.silversurfer.followable.linebuilder.FollowableLine;
import org.mercurialftc.mercurialftc.silversurfer.followable.turnbuilder.FollowableTurn;

public class MarkerBuilder {
	public MarkerBuilder(Command markerReached, Marker.MarkerType markerType, double offset, int referenceIndex) {
		this.markerReached = markerReached;
		this.markerType = markerType;
		this.offset = offset;
		this.referenceIndex = referenceIndex;
	}

	private final Marker.MarkerType markerType;
	private final Command markerReached;
	private final double offset;
	private final int referenceIndex;

	public Marker build(FollowableCurve followableCurve) {
		double callbackTime = followableCurve.getOutputFromIndex(referenceIndex).getCallbackTime() + offset;
		return new Marker(markerReached, markerType, callbackTime);
	}

	public Marker build(FollowableTurn followableTurn) {
		double callbackTime = followableTurn.getOutputs()[followableTurn.getOutputIndexFromSegmentIndex(referenceIndex)].getCallbackTime() + offset;
		return new Marker(markerReached, markerType, callbackTime);
	}

	public Marker build(FollowableLine followableLine) {
		double callbackTime = followableLine.getOutputs()[followableLine.getOutputIndexFromSegmentIndex(referenceIndex)].getCallbackTime() + offset;
		return new Marker(markerReached, markerType, callbackTime);
	}
}
