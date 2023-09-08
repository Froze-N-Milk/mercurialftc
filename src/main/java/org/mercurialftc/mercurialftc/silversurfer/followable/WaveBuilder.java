package org.mercurialftc.mercurialftc.silversurfer.followable;

import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;
import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;
import org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.CurveBuilder;
import org.mercurialftc.mercurialftc.silversurfer.followable.linebuilder.LineBuilder;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.followable.stopbuilder.StopBuilder;
import org.mercurialftc.mercurialftc.silversurfer.followable.turnbuilder.TurnBuilder;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Angle;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;

import java.util.ArrayList;
import java.util.Arrays;

public class WaveBuilder {

	private final Units units;
	private final MotionConstants motionConstants;
	private final ArrayList<Followable> followables;
	private Pose2D previousPose;
	private FollowableBuilder builder;
	private BuildState buildState;
	private MotionConstants buildingMotionConstants;

	public WaveBuilder(Pose2D startPose, Units units, MotionConstants motionConstants) {
		this.previousPose = startPose;
		this.units = units;
		this.motionConstants = motionConstants;
		this.buildingMotionConstants = motionConstants;
		followables = new ArrayList<>();
		buildState = BuildState.IDLE;
	}

	/**
	 * sets the max translational velocity for all subsequent build instructions see {@link #resetVelocity} to reset the translational velocity
	 *
	 * @param translationalVelocity new velocity value. will be coerced into being between 0 and the initial max translational velocity value set.
	 */
	public WaveBuilder setVelocity(double translationalVelocity) {
		buildingMotionConstants = new MotionConstants(
				Math.min(translationalVelocity, motionConstants.getMaxTranslationalVelocity()),
				buildingMotionConstants.getMaxAngularVelocity(),
				buildingMotionConstants.getMaxRotationalVelocity(),
				buildingMotionConstants.getMaxTranslationalAcceleration(),
				buildingMotionConstants.getMaxAngularAcceleration(),
				buildingMotionConstants.getMaxRotationalAcceleration());
		builder.setMotionConstants(buildingMotionConstants);
		return this;
	}

	public WaveBuilder resetVelocity() {
		buildingMotionConstants = new MotionConstants(
				motionConstants.getMaxTranslationalVelocity(),
				buildingMotionConstants.getMaxAngularVelocity(),
				buildingMotionConstants.getMaxRotationalVelocity(),
				buildingMotionConstants.getMaxTranslationalAcceleration(),
				buildingMotionConstants.getMaxAngularAcceleration(),
				buildingMotionConstants.getMaxRotationalAcceleration());
		builder.setMotionConstants(buildingMotionConstants);
		return this;
	}

	/**
	 * sets the max translational acceleration for all subsequent build instructions see {@link #resetAcceleration} to reset the translational acceleration
	 *
	 * @param translationalAcceleration new acceleration value. will be coerced into being between 0 and the initial max translational acceleration value set.
	 */
	public WaveBuilder setAcceleration(double translationalAcceleration) {
		buildingMotionConstants = new MotionConstants(
				buildingMotionConstants.getMaxTranslationalVelocity(),
				buildingMotionConstants.getMaxAngularVelocity(),
				buildingMotionConstants.getMaxRotationalVelocity(),
				Math.min(translationalAcceleration, motionConstants.getMaxTranslationalAcceleration()),
				buildingMotionConstants.getMaxAngularAcceleration(),
				buildingMotionConstants.getMaxRotationalAcceleration());
		builder.setMotionConstants(buildingMotionConstants);
		return this;
	}

	public WaveBuilder resetAcceleration() {
		buildingMotionConstants = new MotionConstants(
				buildingMotionConstants.getMaxTranslationalVelocity(),
				buildingMotionConstants.getMaxAngularVelocity(),
				buildingMotionConstants.getMaxRotationalVelocity(),
				motionConstants.getMaxTranslationalAcceleration(),
				buildingMotionConstants.getMaxAngularAcceleration(),
				buildingMotionConstants.getMaxRotationalAcceleration());
		builder.setMotionConstants(buildingMotionConstants);
		return this;
	}

	public WaveBuilder splineTo(double x, double y, Angle theta) {
		handleState(BuildState.CURVE);
		addSegment(units.toMillimeters(x), units.toMillimeters(y), theta);
		return this;
	}

	/**
	 * instructs the robot to wait in place
	 *
	 * @param seconds the time of the wait (in seconds)
	 * @return
	 */
	public WaveBuilder waitFor(double seconds) {
		handleState(BuildState.STOP);
		StopBuilder stopBuilder = (StopBuilder) builder;
		stopBuilder.addWait(previousPose, seconds);
		return this;
	}

	/**
	 * turns to the provided angle
	 *
	 * @param theta the angle to turn to
	 * @return
	 */
	public WaveBuilder turnTo(Angle theta) {
		handleState(BuildState.TURN);
		addSegment(previousPose.getX(), previousPose.getY(), theta);
		return this;
	}

	/**
	 * turns the provided angle, relative to previous angle
	 *
	 * @param theta the angle of the turn to be done
	 * @return
	 */
	public WaveBuilder turn(Angle theta) {
		handleState(BuildState.TURN);
		addSegment(previousPose.getX(), previousPose.getY(), previousPose.getTheta().add(theta));
		return this;
	}

	public WaveBuilder lineTo(double x, double y, Angle theta) {
		handleState(BuildState.LINE);
		addSegment(x, y, theta);
		return this;
	}

	private void addSegment(double x, double y, Angle theta) {
		Pose2D destination = new Pose2D(x, y, theta, units);
		builder.addFollowableSegment(previousPose, destination);
		previousPose = destination;
	}

	/**
	 * sets a callback action to occur with a timed offset reference to the end of the instruction before it.
	 *
	 * @param offset
	 * @param markerReached
	 * @return
	 */
	public WaveBuilder addOffsetActionMarker(double offset, Command markerReached) {
		builder.addOffsetCommandMarker(offset, Marker.MarkerType.COMMAND, markerReached);
		return this;
	}

	/**
	 * sets a callback action to occur with a timed offset reference to the end of the instruction before it.
	 *
	 * @param offset
	 * @param markerReached
	 * @return
	 */
	public WaveBuilder addOffsetActionMarker(double offset, Runnable markerReached) {
		builder.addOffsetCommandMarker(offset, Marker.MarkerType.LAMBDA, new LambdaCommand().init(markerReached));
		return this;
	}

	/**
	 * handles bundling sequential moves of the same type together, ships a built curve when the state finishes..
	 *
	 * @param newState the new input of state to the handler.
	 */
	private void handleState(BuildState newState) {
		if (newState == buildState) {
			return;
		}
		if (buildState != BuildState.IDLE) {
			followables.add(builder.build());
		}
		buildState = newState;
		switch (newState) {
			case CURVE:
				builder = new CurveBuilder(buildingMotionConstants);
				break;
			case LINE:
				builder = new LineBuilder(buildingMotionConstants);
				break;
			case STOP:
				builder = new StopBuilder();
				break;
			case TURN:
				builder = new TurnBuilder(buildingMotionConstants);
				break;
			case IDLE:
				builder = null;
				break;
		}
	}

	public Wave build() {
		handleState(BuildState.IDLE);

		ArrayList<Followable.Output> outputs = new ArrayList<>();
		ArrayList<Marker> markers = new ArrayList<>();

		double accumulatedTime = 0;

		for (Followable followable : followables) {
			for (Followable.Output output : followable.getOutputs()) {
				output.setAccumulatedTime(accumulatedTime);
			}

			for (Marker marker : followable.getMarkers()) {
				marker.setAccumulatedTime(accumulatedTime);
			}

			outputs.addAll(Arrays.asList(followable.getOutputs()));
			markers.addAll(Arrays.asList(followable.getMarkers()));

			accumulatedTime = followable.getOutputs()[followable.getOutputs().length - 1].getCallbackTime();
		}

		return new Wave(outputs, markers);
	}

	private enum BuildState {
		CURVE,
		LINE,
		STOP,
		TURN,
		IDLE; // entered at initialisation, and then re entered when the whole process is finished. Will not cause a build on exit, will cause one on entry
	}
}
