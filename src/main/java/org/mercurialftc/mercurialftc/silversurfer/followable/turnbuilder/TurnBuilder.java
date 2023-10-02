package org.mercurialftc.mercurialftc.silversurfer.followable.turnbuilder;

import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.FollowableBuilder;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.MarkerBuilder;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.AngleRadians;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

import java.util.ArrayList;

public class TurnBuilder extends FollowableBuilder {
	private final ArrayList<TurnSegment> segments;
	private final ArrayList<MarkerBuilder> unfinishedMarkers;
	private int[] segmentBreakpoints;
	private double[] segmentOutputSizes;

	public TurnBuilder(MecanumMotionConstants motionConstants) {
		super(motionConstants);
		unfinishedMarkers = new ArrayList<>();
		segments = new ArrayList<>();
	}

	private void generateNumberOfOutputs() {
		double targetOutputFrequency = Math.toRadians(0.2);
		int result = 0;

		segmentBreakpoints = new int[segments.size()];
		segmentOutputSizes = new double[segments.size()];

		for (int i = 0; i < segments.size(); i++) {
			double difference = segments.get(i).getDifference();

			double estimate = difference / targetOutputFrequency;
			int accurateResult = (int) Math.round(estimate);

			result += accurateResult;

			segmentOutputSizes[i] = difference / accurateResult;
			segmentBreakpoints[i] = result;
		}
	}

	public int getOutputIndexFromSegmentIndex(int index) {
		if (index >= segmentBreakpoints.length - 1) {
			return segmentBreakpoints[segmentBreakpoints.length - 1];
		} else if (index <= 0) {
			return 0;
		}
		return segmentBreakpoints[index];
	}

	public int getSegmentIndexFromOutputIndex(int index) {
		if (index >= segmentBreakpoints[segmentBreakpoints.length - 1]) {
			return segments.size() - 1;
		} else if (index <= 0) {
			return 0;
		}

		int result = 0;

		for (int i = 1; i < segments.size(); i++) {
			if (index < segmentBreakpoints[i]) {
				result = i - 1;
				break;
			}
		}

		return result;
	}

	private Followable.Output[] profile() {
		Followable.Output[] outputs = new Followable.Output[segmentBreakpoints[segmentBreakpoints.length - 1]];
		AngleRadians previousEstimatedRotationalPosition = segments.get(0).getPreviousPose().getTheta().toAngleRadians();
		double previousRotationalVelocity = 0;
		double previousDeltaT = 0;
		double time = 0;

		for (int i = 0; i < segmentBreakpoints[segmentBreakpoints.length - 1]; i++) {
			int segmentIndex = getSegmentIndexFromOutputIndex(i);
			TurnSegment segment = segments.get(segmentIndex);

			MecanumMotionConstants motionConstants = getMotionConstantsArray().get(segmentIndex);

			AngleRadians targetRotationalPosition = segment.getDestinationPose().getTheta().toAngleRadians();
			AngleRadians estimatedRotationalPosition = previousEstimatedRotationalPosition.add(previousRotationalVelocity * previousDeltaT).toAngleRadians();

			double rotationalError = estimatedRotationalPosition.findShortestDistance(targetRotationalPosition); //shortest distance from estimated current position to target position

			double rotationalBreakDistance = (previousRotationalVelocity * previousRotationalVelocity) / (2 * motionConstants.getMaxRotationalAcceleration());

			int rotationalBreakControl = (int) Math.signum(Math.abs(rotationalError) - rotationalBreakDistance);

			// todo should do for now, possibly need to implement some scaling for the acceleration to dampen or smth
			double rotationalVelocity = Math.sqrt((previousRotationalVelocity * previousRotationalVelocity) + 2 * motionConstants.getMaxRotationalAcceleration() * Math.signum(rotationalError) * rotationalBreakControl * segmentOutputSizes[segmentIndex]); // todo should do for now, possibly need to implement some scaling for the acceleration to dampen or smth
			rotationalVelocity = Math.min(rotationalVelocity, motionConstants.getMaxRotationalVelocity());

			// ∆t = 2∆s / (v_i + v_{i−1})
			double deltaT = (2 * segmentOutputSizes[segmentIndex]) / (previousRotationalVelocity + rotationalVelocity);

			time += deltaT;

			outputs[i] = new Followable.Output(
					new Vector2D(0, 0),
					rotationalVelocity,
					time,
					new Pose2D(segment.getDestinationPose().getX(), segment.getDestinationPose().getY(), estimatedRotationalPosition),
					segment.getDestinationPose()
			);

			previousRotationalVelocity = rotationalVelocity;
			previousDeltaT = deltaT;
		}

		return outputs;
	}

	@Override
	public Followable build() {
		generateNumberOfOutputs();
		return new FollowableTurn(
				profile(),
				unfinishedMarkers,
				this
		);
	}

	/**
	 * for internal use only
	 *
	 * @param previousPose    previous pose
	 * @param destinationPose destination pose, with the same x and y values as previous pose
	 */
	@Override
	protected void addSegment(Pose2D previousPose, Pose2D destinationPose) {
		segments.add(new TurnSegment(previousPose, destinationPose));
	}

	@Override
	protected void addOffsetCommandMarker(double offset, Marker.MarkerType markerType, Command markerReached) {
		unfinishedMarkers.add(new MarkerBuilder(
				markerReached,
				markerType,
				offset,
				segments.size() - 1
		));
	}

	private static class TurnSegment {
		private final Pose2D previousPose, destinationPose;

		public TurnSegment(Pose2D previousPose, Pose2D destinationPose) {
			this.previousPose = previousPose;
			this.destinationPose = destinationPose;
		}

		public double getDifference() {
			return getPreviousPose().getTheta().findShortestDistance(getDestinationPose().getTheta());
		}

		public Pose2D getDestinationPose() {
			return destinationPose;
		}

		public Pose2D getPreviousPose() {
			return previousPose;
		}
	}
}
