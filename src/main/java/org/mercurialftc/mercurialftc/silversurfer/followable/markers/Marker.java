package org.mercurialftc.mercurialftc.silversurfer.followable.markers;

import org.mercurialftc.mercurialftc.scheduler.commands.CommandSignature;

public class Marker {
	private final CommandSignature markerReached;
	private final double callbackTime;
	private final MarkerType markerType;
	private double accumulatedTime;

	public Marker(CommandSignature command, MarkerType markerType, double callbackTime) {
		this.markerReached = command;
		this.callbackTime = callbackTime;
		this.accumulatedTime = 0;
		this.markerType = markerType;
	}

	public void setAccumulatedTime(double accumulatedTime) {
		this.accumulatedTime = accumulatedTime;
	}

	public CommandSignature getMarkerReached() {
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
