package org.mercurialftc.mercurialftc.silversurfer.followable.markers;

import org.mercurialftc.mercurialftc.scheduler.commands.Command;

public class Marker {
	private final Command markerReached;
	private final double callbackTime;
	private final MarkerType markerType;
	private double accumulatedTime;

	public Marker(Command command, MarkerType markerType, double callbackTime) {
		this.markerReached = command;
		this.callbackTime = callbackTime;
		this.accumulatedTime = 0;
		this.markerType = markerType;
	}

	public void setAccumulatedTime(double accumulatedTime) {
		this.accumulatedTime = accumulatedTime;
	}

	public Command getMarkerReached() {
		return markerReached;
	}

	public MarkerType getMarkerType() {
		return markerType;
	}

	public double getCallbackTime() {
		return accumulatedTime + callbackTime;
	}

	public enum MarkerType {
		COMMAND,
		LAMBDA
	}
}
