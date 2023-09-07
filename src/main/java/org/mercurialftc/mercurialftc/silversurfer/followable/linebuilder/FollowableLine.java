package org.mercurialftc.mercurialftc.silversurfer.followable.linebuilder;

import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.MarkerBuilder;

import java.util.ArrayList;

public class FollowableLine extends Followable {
	private final LineBuilder lineBuilder;

	protected FollowableLine(Output[] outputs, ArrayList<MarkerBuilder> unfinishedMarkers, LineBuilder lineBuilder) {
		setOutputs(outputs);

		Marker[] markers = new Marker[unfinishedMarkers.size()];

		for (int i = 0; i < unfinishedMarkers.size(); i++) {
			markers[i] = unfinishedMarkers.get(i).build(this);
		}

		setMarkers(markers);
		this.lineBuilder = lineBuilder;

	}

	public int getOutputIndexFromSegmentIndex(int index) {
		return lineBuilder.getOutputIndexFromSegmentIndex(index);
	}
}

