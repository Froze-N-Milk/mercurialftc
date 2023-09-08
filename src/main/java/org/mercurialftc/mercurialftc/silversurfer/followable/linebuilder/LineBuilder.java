package org.mercurialftc.mercurialftc.silversurfer.followable.linebuilder;

import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.FollowableBuilder;
import org.mercurialftc.mercurialftc.silversurfer.followable.MotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.MarkerBuilder;
import org.mercurialftc.mercurialftc.silversurfer.geometry.AngleRadians;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

import java.util.ArrayList;

public class LineBuilder extends FollowableBuilder {
	private ArrayList<LineSegment> segments;
	private ArrayList<MarkerBuilder> unfinishedMarkers;
	private int[] segmentBreakpoints;
	private double[] segmentOutputSizes;

	public LineBuilder(MotionConstants motionConstants) {
		super(motionConstants);
		unfinishedMarkers = new ArrayList<>();
		segments = new ArrayList<>();
	}

	private void generateNumberOfOutputs() {
		double targetOutputFrequency = 2.5; //2.5 millimeters!
		int result = 0;

		segmentBreakpoints = new int[segments.size()];
		segmentOutputSizes = new double[segments.size()];

		for (int i = 0; i < segments.size(); i++) {
			double distance = segments.get(i).getVector().getMagnitude();

			double estimate = distance / targetOutputFrequency;
			int accurateResult = (int) Math.round(estimate);

			result += accurateResult;

			segmentOutputSizes[i] = distance / accurateResult;
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
		Pose2D previousEstimatedPose2D = segments.get(0).getPreviousPose();
		Vector2D previousVectorOutput = new Vector2D(0, 0);
		double previousVelocityConstraint = 0;
		double previousRotationalVelocity = 0;
		double previousDeltaT = 0;
		double time = 0;

		for (int i = 0; i < segmentBreakpoints[segmentBreakpoints.length - 1]; i++) {
			int segmentIndex = getSegmentIndexFromOutputIndex(i);
			LineSegment segment = segments.get(segmentIndex);

			MotionConstants motionConstants = getMotionConstantsArray().get(segmentIndex);

			Pose2D targetPose = segment.getDestinationPose();
			Pose2D estimatedPose = previousEstimatedPose2D.add(previousVectorOutput.getX() * previousDeltaT, previousVectorOutput.getY() * previousDeltaT, new AngleRadians(previousRotationalVelocity * previousDeltaT));

			AngleRadians targetRotationalPosition = segment.getDestinationPose().getTheta();
			AngleRadians estimatedRotationalPosition = estimatedPose.getTheta();

			double rotationalError = estimatedRotationalPosition.findShortestDistance(targetRotationalPosition); //shortest distance from estimated current position to target position

			double rotationalBreakDistance = (previousRotationalVelocity * previousRotationalVelocity) / (2 * motionConstants.getMaxRotationalAcceleration());

			int rotationalBreakControl = (int) Math.signum(Math.abs(rotationalError) - rotationalBreakDistance);

			double rotationDistance = previousEstimatedPose2D.getTheta().findShortestDistance(estimatedRotationalPosition); // distance of last planned point, should be aight though

			// todo should do for now, possibly need to implement some scaling for the acceleration to dampen or smth
			double rotationalVelocity = Math.sqrt((previousRotationalVelocity * previousRotationalVelocity) + 2 * motionConstants.getMaxRotationalAcceleration() * Math.signum(rotationalError) * rotationalBreakControl * rotationDistance); // todo should do for now, possibly need to implement some scaling for the acceleration to dampen or smth
			rotationalVelocity = Math.min(rotationalVelocity, motionConstants.getMaxRotationalVelocity());

			double vMax = motionConstants.getMaxTranslationalVelocity();

			// just used previous rotational velocity bc otherwise things are too circular, may fix up later
			double vMaxRotation = motionConstants.getMaxRotationalVelocity() / rotationalVelocity;
			// todo make these optional
			// todo add distance to nearest object

			double translationalError = new Vector2D(targetPose.getX() - estimatedPose.getX(), targetPose.getY() - estimatedPose.getY()).getMagnitude();

			double translationalBreakDistance = (previousVelocityConstraint * previousVelocityConstraint) / (2 * motionConstants.getMaxTranslationalVelocity());

			int translationalBreakControl = (int) Math.signum(Math.abs(translationalError) - translationalBreakDistance);

			double vMaxStop = Math.sqrt((previousVelocityConstraint * previousVelocityConstraint) + 2 * motionConstants.getMaxTranslationalAcceleration() * translationalBreakControl * segmentOutputSizes[segmentIndex]);
			;

			double finalVelocityConstraint = Math.min(vMaxStop, Math.min(vMax, vMaxRotation));


			// ∆t = 2∆s / (v_i + v_{i−1})
			double deltaT = (2 * segmentOutputSizes[segmentIndex]) / (previousVelocityConstraint + finalVelocityConstraint);

			time += deltaT;

			previousVectorOutput = Vector2D.fromPolar(finalVelocityConstraint, segment.getVector().getHeading());

			outputs[i] = new Followable.Output(
					previousVectorOutput,
					rotationalVelocity,
					time,
					estimatedPose,
					segment.destinationPose
			);

			previousEstimatedPose2D = estimatedPose;
			previousVelocityConstraint = finalVelocityConstraint;
			previousRotationalVelocity = rotationalVelocity;
			previousDeltaT = deltaT;
		}

		return outputs;
	}

	@Override
	public Followable build() {
		generateNumberOfOutputs();
		return new FollowableLine(
				profile(),
				unfinishedMarkers,
				this
		);
	}

	/**
	 * for internal use only
	 *
	 * @param previousPose
	 * @param destinationPose
	 */
	@Override
	protected void addSegment(Pose2D previousPose, Pose2D destinationPose) {
		segments.add(new LineSegment(previousPose, destinationPose));
	}

	@Override
	protected void addOffsetCommandMarker(double offset, Marker.MarkerType markerType, Command markerReached) {
		unfinishedMarkers.add(new MarkerBuilder(markerReached, markerType, offset, segments.size() - 1));
	}

	private static class LineSegment {
		private final Pose2D previousPose, destinationPose;

		public LineSegment(Pose2D previousPose, Pose2D destinationPose) {
			this.previousPose = previousPose;
			this.destinationPose = destinationPose;
		}

		public Vector2D getVector() {
			return new Vector2D(destinationPose.getX() - previousPose.getX(), destinationPose.getY() - previousPose.getY());
		}

		public Pose2D getDestinationPose() {
			return destinationPose;
		}

		public Pose2D getPreviousPose() {
			return previousPose;
		}
	}
}
