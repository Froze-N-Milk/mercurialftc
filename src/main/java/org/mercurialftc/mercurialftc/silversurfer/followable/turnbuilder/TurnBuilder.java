package org.mercurialftc.mercurialftc.silversurfer.followable.turnbuilder;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.FollowableBuilder;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.MarkerBuilder;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.AngleRadians;

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
			double difference = Math.abs(segments.get(i).getDifference());

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

	@NotNull
	private Followable.Output[] profile() {
		Followable.Output[] outputs = new Followable.Output[segmentBreakpoints[segmentBreakpoints.length - 1]];

		TurnSegment previousSegment = segments.get(0);

		outputs[0] = new Followable.Output(
				Vector2D.fromPolar(0, 0), // the velocity output
				0,
				0,
				previousSegment.getPreviousPose(),
				previousSegment.getDestinationPose()
		);

		double previousVelocity = 0;

		for (int i = 1; i < segmentBreakpoints[segmentBreakpoints.length - 1]; i++) {
			int segmentIndex = getSegmentIndexFromOutputIndex(i);
			TurnSegment segment = segments.get(segmentIndex);

			MecanumMotionConstants motionConstants = getMotionConstantsArray().get(segmentIndex);

			double vMax = motionConstants.getMaxRotationalVelocity();

			double vAccelerationLimited = Math.sqrt(previousVelocity * previousVelocity + 2 * motionConstants.getMaxRotationalAcceleration() * segmentOutputSizes[segmentIndex]);

			double finalVelocityConstraint = Math.min(vAccelerationLimited, vMax);

//			double errorSignum = Math.signum(segment.getDifference());

			int previousBreakPoint = 0;
			if (segmentIndex > 0) previousBreakPoint = segmentBreakpoints[segmentIndex - 1];
			int numberOfSubdivisions = i - previousBreakPoint;

			Pose2D position = segment.getPreviousPose().add(0, 0, new AngleRadians(segmentOutputSizes[segmentIndex] * numberOfSubdivisions));

			outputs[i] = new Followable.Output(
					Vector2D.fromPolar(0, 0), // the velocity output
					finalVelocityConstraint,
					0,
					position,
					segment.getDestinationPose()
			);

			previousVelocity = finalVelocityConstraint;
		}

//		do a backward pass for accelerational constraints

//		set the final output to be back at 0
		outputs[outputs.length - 1] = new Followable.Output(
				new Vector2D(), // the velocity output
				0,
				0,
				segments.get(segments.size() - 1).getDestinationPose(),
				segments.get(segments.size() - 1).getDestinationPose()
		);

		previousVelocity = 0;

		for (int i = segmentBreakpoints[segmentBreakpoints.length - 1] - 2; i >= 0; i--) {
			int segmentIndex = getSegmentIndexFromOutputIndex(i);
			TurnSegment segment = segments.get(segmentIndex);

			MecanumMotionConstants motionConstants = getMotionConstantsArray().get(segmentIndex);

			double rotationalVelocity = outputs[i].getRotationalVelocity();

			double vMaxAccelerationLimited = Math.sqrt(previousVelocity * previousVelocity + 2 * motionConstants.getMaxRotationalVelocity() * segmentOutputSizes[segmentIndex]);

			double finalVelocityConstraint = Math.min(rotationalVelocity, vMaxAccelerationLimited);

			double errorSignum = Math.signum(segment.getDifference());

			outputs[i] = new Followable.Output(
					outputs[i].getTranslationVector(),
					finalVelocityConstraint * errorSignum,
					outputs[i].getCallbackTime(),
					outputs[i].getPosition(),
					outputs[i].getDestination()
			);

			previousVelocity = finalVelocityConstraint;
		}

		// forward pass to calculate times

		previousVelocity = outputs[0].getRotationalVelocity();
		double time = 0;

		for (int i = 1; i < segmentBreakpoints[segmentBreakpoints.length - 1]; i++) {
			int segmentIndex = getSegmentIndexFromOutputIndex(i);

			double velocity = outputs[i].getRotationalVelocity();

			// ∆t = 2∆s / (v_i + v_{i−1})
			double deltaT = (2 * segmentOutputSizes[segmentIndex]) / (Math.abs(velocity) + Math.abs(previousVelocity));

			time += deltaT;

			outputs[i] = new Followable.Output(
					outputs[i].getTranslationVector(),
					outputs[i].getRotationalVelocity(),
					time,
					outputs[i].getPosition(),
					outputs[i].getDestination()
			);

			previousVelocity = velocity;
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
