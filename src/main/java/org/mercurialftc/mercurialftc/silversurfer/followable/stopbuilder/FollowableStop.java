package org.mercurialftc.mercurialftc.silversurfer.followable.stopbuilder;

import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;

public class FollowableStop extends Followable {
	protected FollowableStop(Output[] outputs, Marker[] markers) {
		setOutputs(outputs);
		setMarkers(markers);
	}
}
