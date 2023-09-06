package org.mercurialftc.mercurialftc.silversurfer.followable.stopbuilder;

import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.FollowableBuilder;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

import java.util.ArrayList;

public class StopBuilder extends FollowableBuilder {
	private final ArrayList<Marker> markers;
	private final ArrayList<Followable.Output> outputs;
	double previousWait;
	
	public StopBuilder() {
		super(null);
		markers = new ArrayList<>();
		outputs = new ArrayList<>();
		previousWait = 0;
	}
	
	@Override
	public Followable build() {
		return new FollowableStop(
				outputs.toArray(new Followable.Output[0]),
				markers.toArray(new Marker[0])
		);
	}
	
	public final void addWait(Pose2D position, double seconds) {
		seconds = Math.max(seconds, 0);
		
		outputs.add(
				new Followable.Output(
						new Vector2D(0, 0),
						0,
						previousWait + seconds,
						position,
						position
				)
		);
		previousWait += seconds;
	}
	
	/**
	 * <h1>DO NOT USE<h1/>
	 * {@link StopBuilder} is special and so has no segments
	 *
	 * @param previousPose
	 * @param destinationPose
	 */
	@Override
	protected void addSegment(Pose2D previousPose, Pose2D destinationPose) {
		// does nothing
	}
	
	@Override
	protected void addOffsetCommandMarker(double offset, Marker.MarkerType markerType, Command markerReached) {
		markers.add(new Marker(
				markerReached, markerType,
				previousWait + offset
		));
	}
}
