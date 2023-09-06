package org.mercurialftc.mercurialftc.silversurfer.followable;

import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

import java.util.ArrayList;

/**
 * describes an entire followable path, including several different sections of curves, straight lines, turns, pauses, and actions
 */
public class Wave {
	private final ArrayList<Followable.Output> outputs;
	private final ArrayList<Marker> markers;
	private Followable.Output currentOutput;
	
	private int i, j; // tracks
	protected Wave(ArrayList<Followable.Output> outputs, ArrayList<Marker> markers) {
		this.outputs = outputs;
		this.markers = markers;
		this.currentOutput = new Followable.Output(
				new Vector2D(0, 0),
				0, 0,
				outputs.get(0).getPosition(),
				outputs.get(0).getDestination()
		);
		i = j = 0;
	}
	
	public Followable.Output getOutput() {
		return currentOutput;
	}
	
	/**
	 * runs markers and updates the current {@link #getOutput()} of this wave, to be read by a follower
	 * @param currentTime time since start of path following
	 * @return returns true when no more outputs or actions are to be done
	 */
	public boolean update(double currentTime) {
		boolean finished = true;

		if (i < outputs.size()) {
			while(currentTime >= outputs.get(i).getCallbackTime()) {
				this.currentOutput = outputs.get(i);
				i++;
			}
			finished = false;
		}
		else { //set out 0s for end of instructions
			this.currentOutput = new Followable.Output(
					new Vector2D(0, 0),
					0, currentTime,
					outputs.get(outputs.size() - 1).getPosition(),
					outputs.get(outputs.size() - 1).getDestination()
			);
		}
		
		if (j < markers.size()) {
			while(currentTime >= markers.get(j).getCallbackTime()) {
				Marker marker = markers.get(j);
				if(marker.getMarkerType() == Marker.MarkerType.COMMAND) {
					marker.getMarkerReached().queue();
				}
				else {
					marker.getMarkerReached().initialise();
				}
				j++;
			}

			finished = false;
		}

		return finished;
	}
}
