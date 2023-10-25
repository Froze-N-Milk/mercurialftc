package org.mercurialftc.mercurialftc.silversurfer.followable.linebuilder;

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

public class LineBuilder extends FollowableBuilder {
	private final ArrayList<LineSegment> segments;
	private final ArrayList<MarkerBuilder> unfinishedMarkers;
	private final MecanumMotionConstants absoluteMotionConstants;
	private int[] segmentBreakpoints;
	private double[] segmentOutputSizes;

	public LineBuilder(MecanumMotionConstants motionConstants, MecanumMotionConstants absoluteMotionConstants) {
		super(motionConstants);
		this.absoluteMotionConstants = absoluteMotionConstants;
		unfinishedMarkers = new ArrayList<>();
		segments = new ArrayList<>();
	}

	private MecanumMotionConstants getAbsoluteMotionConstants() {
		return absoluteMotionConstants;
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

		for (int i = 1; i < segments.size(); i++) {
			if (index < segmentBreakpoints[i]) {
				return i - 1;
			}
		}

		return 0;
	}

	@NotNull
	private Followable.Output[] profile() {
		Followable.Output[] outputs = new Followable.Output[segmentBreakpoints[segmentBreakpoints.length - 1]];

		LineSegment previousSegment = segments.get(0);

		outputs[0] = new Followable.Output(
				Vector2D.fromPolar(0, previousSegment.getVector().getHeading()), // the velocity output
				0,
				0,
				previousSegment.getPreviousPose(),
				previousSegment.getDestinationPose()
		);

		double previousVelocity = 0;

		for (int i = 1; i < segmentBreakpoints[segmentBreakpoints.length - 1]; i++) {
			int segmentIndex = getSegmentIndexFromOutputIndex(i);
			LineSegment segment = segments.get(segmentIndex);

			MecanumMotionConstants motionConstants = getMotionConstantsArray().get(segmentIndex);

			MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = motionConstants.makeDirectionOfTravelLimiter(segment.getVector().getHeading());

			double vMax = directionOfTravelLimiter.getVelocity();

			double vAccelerationLimited = Math.sqrt(previousVelocity * previousVelocity + 2 * directionOfTravelLimiter.getAcceleration() * segmentOutputSizes[segmentIndex]);

			double finalVelocityConstraint = Math.min(vAccelerationLimited, vMax);

			int previousBreakPoint = 0;
			if (segmentIndex > 0) previousBreakPoint = segmentBreakpoints[segmentIndex - 1];
			int numberOfSubdivisions = i - previousBreakPoint;
			Vector2D positionVector = Vector2D.fromPolar(segmentOutputSizes[segmentIndex] * numberOfSubdivisions, segment.getVector().getHeading()).add(segment.getPreviousPose().toVector2D());

			outputs[i] = new Followable.Output(
					Vector2D.fromPolar(finalVelocityConstraint, segment.getVector().getHeading()), // the velocity output
					0,
					0,
					new Pose2D(positionVector.getX(), positionVector.getY(), 0),
					segment.getDestinationPose()
			);

			previousVelocity = finalVelocityConstraint;
		}

//		do a backward pass for accelerational constraints

//		set the final output to be back at 0
		outputs[outputs.length - 1] = new Followable.Output(
				Vector2D.fromPolar(0, segments.get(segments.size() - 1).getVector().getHeading()), // the velocity output
				0,
				0,
				segments.get(segments.size() - 1).getDestinationPose(),
				segments.get(segments.size() - 1).getDestinationPose()
		);

		previousVelocity = 0;

		for (int i = segmentBreakpoints[segmentBreakpoints.length - 1] - 2; i >= 0; i--) {
			int segmentIndex = getSegmentIndexFromOutputIndex(i);
			LineSegment segment = segments.get(segmentIndex);

			MecanumMotionConstants motionConstants = getMotionConstantsArray().get(segmentIndex);

			Vector2D translationVector = outputs[i].getTranslationVector();
			double translationalVelocity = translationVector.getMagnitude();

			MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = motionConstants.makeDirectionOfTravelLimiter(segment.getVector().getHeading());

			double vMaxAccelerationLimited = Math.sqrt(previousVelocity * previousVelocity + 2 * directionOfTravelLimiter.getAcceleration() * segmentOutputSizes[segmentIndex]);

			double finalVelocityConstraint = Math.min(translationalVelocity, vMaxAccelerationLimited);

			outputs[i] = new Followable.Output(
					Vector2D.fromPolar(finalVelocityConstraint, translationVector.getHeading()),
					outputs[i].getRotationalVelocity(),
					outputs[i].getCallbackTime(),
					outputs[i].getPosition(),
					outputs[i].getDestination()
			);

			previousVelocity = finalVelocityConstraint;
		}

		// forward pass to calculate times

		previousVelocity = outputs[0].getTranslationVector().getMagnitude();
		double time = 0;

		for (int i = 1; i < segmentBreakpoints[segmentBreakpoints.length - 1]; i++) {
			int segmentIndex = getSegmentIndexFromOutputIndex(i);

			double velocity = outputs[i].getTranslationVector().getMagnitude();

			// ∆t = 2∆s / (v_i + v_{i−1})
			double deltaT = (2 * segmentOutputSizes[segmentIndex]) / (velocity + previousVelocity);

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

		// forward pass rotation

		double previousRotationalVelocity = 0;
		AngleRadians previousEstimatedRotationalPosition = segments.get(0).previousPose.getTheta().toAngleRadians();

		for (int i = 1; i < segmentBreakpoints[segmentBreakpoints.length - 1]; i++) {
			int segmentIndex = getSegmentIndexFromOutputIndex(i);
			LineSegment segment = segments.get(segmentIndex);

			MecanumMotionConstants motionConstants = getMotionConstantsArray().get(segmentIndex);

			double deltaT = (outputs[i].getCallbackTime() - outputs[i - 1].getCallbackTime());

			AngleRadians targetRotationalPosition = segment.getDestinationPose().getTheta().toAngleRadians();

			double rotationalError = previousEstimatedRotationalPosition.findShortestDistance(targetRotationalPosition); //shortest distance from estimated current position to target position

			double rotationalBreakDistance = deltaT * Math.abs(previousRotationalVelocity) + (previousRotationalVelocity * previousRotationalVelocity) / (2 * motionConstants.getMaxRotationalAcceleration());

			int rotationalBreakControl = (int) (Math.signum(Math.abs(rotationalError) - rotationalBreakDistance)); // if negative, we should be slowing down

			double rotationalVelocity = previousRotationalVelocity + (deltaT * motionConstants.getMaxRotationalAcceleration() * Math.signum(rotationalError));

			int velocitySignum = (int) Math.signum(rotationalVelocity);

			double maxRotationalVelocityBreakLimited = Math.abs(Math.abs(previousRotationalVelocity) + (deltaT * motionConstants.getMaxRotationalAcceleration() * rotationalBreakControl));

			MecanumMotionConstants.DirectionOfTravelLimiter absoluteDirectionOfTravelLimiter = getAbsoluteMotionConstants().makeDirectionOfTravelLimiter(outputs[i].getTranslationVector().getHeading());

			double maxRotationalVelocityTranslationLimited = motionConstants.getMaxRotationalVelocity() * (outputs[i].getTranslationVector().getMagnitude() / absoluteDirectionOfTravelLimiter.getVelocity());
			double finalRotationalVelocityConstraint = Math.min(maxRotationalVelocityTranslationLimited, maxRotationalVelocityBreakLimited);
			finalRotationalVelocityConstraint = Math.min(finalRotationalVelocityConstraint, Math.abs(rotationalVelocity)) * velocitySignum;

			previousRotationalVelocity = finalRotationalVelocityConstraint;
			previousEstimatedRotationalPosition = previousEstimatedRotationalPosition.add(finalRotationalVelocityConstraint * deltaT + 0.5 * (previousRotationalVelocity - finalRotationalVelocityConstraint) * deltaT * 2).toAngleRadians();

			outputs[i] = new Followable.Output(
					outputs[i].getTranslationVector(),
					finalRotationalVelocityConstraint,
					outputs[i].getCallbackTime(),
					new Pose2D(outputs[i].getPosition().getX(), outputs[i].getPosition().getY(), previousEstimatedRotationalPosition),
					outputs[i].getDestination()
			);
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
		private final Vector2D vector;

		public LineSegment(@NotNull Pose2D previousPose, @NotNull Pose2D destinationPose) {
			this.previousPose = previousPose;
			this.destinationPose = destinationPose;
			this.vector = new Vector2D(destinationPose.getX() - previousPose.getX(), destinationPose.getY() - previousPose.getY());
		}

		@NotNull
		public Vector2D getVector() {
			return vector;
		}

		@NotNull
		public Pose2D getDestinationPose() {
			return destinationPose;
		}

		@NotNull
		public Pose2D getPreviousPose() {
			return previousPose;
		}
	}
}
