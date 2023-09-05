package org.mercurialftc.mercurialftc.silversurfer.followable.turnbuilder;

import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.MarkerBuilder;

import java.util.ArrayList;

public class FollowableTurn extends Followable {
	private final TurnBuilder turnBuilder;
	protected FollowableTurn(Output[] outputs, ArrayList<MarkerBuilder> unfinishedMarkers, TurnBuilder turnBuilder) {
		setOutputs(outputs);
		
		Marker[] markers = new Marker[unfinishedMarkers.size()];
		
		for (int i = 0; i < unfinishedMarkers.size(); i++) {
			markers[i] = unfinishedMarkers.get(i).build(this);
		}
		
		setMarkers(markers);
		this.turnBuilder = turnBuilder;
	}
	
	public int getOutputIndexFromSegmentIndex(int index) {
		return turnBuilder.getOutputIndexFromSegmentIndex(index);
	}
}
