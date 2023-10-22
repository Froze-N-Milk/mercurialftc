package org.mercurialftc.mercurialftc.silversurfer.followable;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * describes an entire followable path, including several different sections of curves, straight lines, turns, pauses, and actions
 */
@SuppressWarnings("unused")
public class Wave {
	private final ArrayList<Followable.Output> outputs;
	private final ArrayList<Marker> markers;
	private Followable.Output currentOutput;

	private int i, j; // tracks outputs and markers respectively

	protected Wave(@NotNull ArrayList<Followable.Output> outputs, ArrayList<Marker> markers) {
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
	 *
	 * @param currentTime time since start of path following
	 * @return returns true when no more outputs or actions are to be done
	 */
	public boolean update(double currentTime) {
		boolean finished = true;

		if (i < outputs.size()) {
			while (i < outputs.size() && currentTime >= outputs.get(i).getCallbackTime()) {
				this.currentOutput = outputs.get(i);
				i++;
			}
			finished = false;
		} else { //set out 0s for end of instructions
			this.currentOutput = new Followable.Output(
					new Vector2D(0, 0),
					0, currentTime,
					outputs.get(outputs.size() - 1).getDestination(),
					outputs.get(outputs.size() - 1).getDestination()
			);
		}

		if (j < markers.size()) {
			while (j < markers.size() && currentTime >= markers.get(j).getCallbackTime()) {
				Marker marker = markers.get(j);
				if (marker.getMarkerType() == Marker.MarkerType.COMMAND) {
					marker.getMarkerReached().queue();
				} else {
					marker.getMarkerReached().initialise();
				}
				j++;
			}

			finished = false;
		}

		return finished;
	}

	/**
	 * non-mutating
	 * <p>concatenates two waves together, runs the second one after the first</p>
	 * <p>WARNING: does no safety checking during the operation, ensure that the other wave has the same start point as this one</p>
	 *
	 * @param other the wave to join to the end of this
	 * @return a new wave containing the outputs and markers of the initial wave and the other wave
	 */
	public Wave concat(@NotNull Wave other) {
		ArrayList<Followable.Output> newOutputs = new ArrayList<>(this.outputs.size() + other.outputs.size());
		newOutputs.addAll(this.outputs);
		newOutputs.addAll(other.outputs);

		ArrayList<Marker> newMarkers = new ArrayList<>(this.markers.size() + other.markers.size());
		newMarkers.addAll(this.markers);
		newMarkers.addAll(other.markers);

		double additionalAccumulatedTime = this.outputs.get(this.outputs.size() - 1).getCallbackTime();
		IntStream.range(this.outputs.size(), newOutputs.size()).mapToObj(newOutputs::get).forEach(output -> output.setAccumulatedTime(output.getAccumulatedTime() + additionalAccumulatedTime));
		IntStream.range(this.markers.size(), newMarkers.size()).mapToObj(newMarkers::get).forEach(marker -> marker.setAccumulatedTime(marker.getAccumulatedTime() + additionalAccumulatedTime));

		return new Wave(
				newOutputs,
				newMarkers
		);
	}
}
