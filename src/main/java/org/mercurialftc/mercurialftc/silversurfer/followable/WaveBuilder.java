package org.mercurialftc.mercurialftc.silversurfer.followable;

import org.mercurialftc.mercurialftc.scheduler.commands.CommandSignature;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;
import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;
import org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.CurveBuilder;
import org.mercurialftc.mercurialftc.silversurfer.followable.linebuilder.LineBuilder;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.followable.stopbuilder.StopBuilder;
import org.mercurialftc.mercurialftc.silversurfer.followable.turnbuilder.TurnBuilder;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.Angle;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.EmptyObstacleMap;
import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.ObstacleMap;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("unused")
public class WaveBuilder {

	private final Units units;
	private final ArrayList<Followable> followables;
	private final MecanumMotionConstants mecanumMotionConstants;
	private final ObstacleMap obstacleMap;
	private Pose2D previousPose, previousTargetPose;
	private FollowableBuilder builder;
	private BuildState buildState;
	private MecanumMotionConstants buildingMotionConstants;

	/**
	 * constructs a wave builder with a totally empty obstacle map
	 * <p>note: scales all motion constants to 0.8 immediately</p>
	 *
	 * @param startPose       the starting position of the robot
	 * @param units           the unit type to be used throughout the rest of the builder
	 * @param motionConstants the motion constants of the drive base, should have its scaling modifiers set to 1
	 */
	public WaveBuilder(Pose2D startPose, Units units, MecanumMotionConstants motionConstants) {
		this(startPose, units, motionConstants, new EmptyObstacleMap());
	}

	/**
	 * constructs a wave builder
	 * <p>note: scales all motion constants to 0.8 immediately</p>
	 *
	 * @param startPose       the starting position of the robot
	 * @param units           the unit type to be used throughout the rest of the builder
	 * @param motionConstants the motion constants of the drive base, should have its scaling modifiers set to 1
	 * @param obstacleMap     the obstacle map to use for obstacle avoidance
	 */
	public WaveBuilder(Pose2D startPose, Units units, MecanumMotionConstants motionConstants, ObstacleMap obstacleMap) {
		this.previousPose = this.previousTargetPose = startPose;
		this.units = units;
		this.buildingMotionConstants = this.mecanumMotionConstants = motionConstants;
		this.obstacleMap = obstacleMap;
		followables = new ArrayList<>();
		buildState = BuildState.IDLE;

		scaleTranslationVelocity(0.8);
		scaleTranslationAcceleration(0.8);
		scaleRotationVelocity(0.8);
		scaleRotationAcceleration(0.8);
	}

	/**
	 * scales the translational velocity for all further instructions, calling this method again will override the previously set value
	 * <p>call this with an argument of 1 to reset to full velocity</p>
	 * <p>it is recommended to never use a value above 0.8, in order to ensure that the robot can always meet the demands created by the wave generator</p>
	 *
	 * @param scalingMultiplier the scaling multiplier in the domain [0, 1]
	 * @return self, for method chaining
	 */
	public WaveBuilder scaleTranslationVelocity(double scalingMultiplier) {
		buildingMotionConstants = new MecanumMotionConstants(
				scalingMultiplier,
				buildingMotionConstants.getRotationalVelocityMultiplier(),
				buildingMotionConstants.getTranslationalAccelerationMultiplier(),
				buildingMotionConstants.getRotationalAccelerationMultiplier(),
				buildingMotionConstants.getMaxTranslationalYVelocity(),
				buildingMotionConstants.getMaxTranslationalXVelocity(),
				buildingMotionConstants.getMaxTranslationalAngledVelocity(),
				buildingMotionConstants.getMaxRotationalVelocity(),
				buildingMotionConstants.getMaxTranslationalYAcceleration(),
				buildingMotionConstants.getMaxTranslationalXAcceleration(),
				buildingMotionConstants.getMaxTranslationalAngledAcceleration(),
				buildingMotionConstants.getMaxRotationalAcceleration()
		);
		if (builder != null) {
			builder.setMotionConstants(buildingMotionConstants);
		}
		return this;
	}

	/**
	 * scales the translational acceleration for all further instructions, calling this method again will override the previously set value
	 * <p>call this with an argument of 1 to reset to full acceleration</p>
	 * <p>it is recommended to never use a value above 0.8, in order to ensure that the robot can always meet the demands created by the wave generator</p>
	 *
	 * @param scalingMultiplier the scaling multiplier in the domain [0, 1]
	 * @return self, for method chaining
	 */
	public WaveBuilder scaleTranslationAcceleration(double scalingMultiplier) {
		buildingMotionConstants = new MecanumMotionConstants(
				buildingMotionConstants.getTranslationalVelocityMultiplier(),
				buildingMotionConstants.getRotationalVelocityMultiplier(),
				scalingMultiplier,
				buildingMotionConstants.getRotationalAccelerationMultiplier(),
				buildingMotionConstants.getMaxTranslationalYVelocity(),
				buildingMotionConstants.getMaxTranslationalXVelocity(),
				buildingMotionConstants.getMaxTranslationalAngledVelocity(),
				buildingMotionConstants.getMaxRotationalVelocity(),
				buildingMotionConstants.getMaxTranslationalYAcceleration(),
				buildingMotionConstants.getMaxTranslationalXAcceleration(),
				buildingMotionConstants.getMaxTranslationalAngledAcceleration(),
				buildingMotionConstants.getMaxRotationalAcceleration()
		);
		if (builder != null) {
			builder.setMotionConstants(buildingMotionConstants);
		}
		return this;
	}

	/**
	 * scales the rotational velocity for all further instructions, calling this method again will override the previously set value
	 * <p>call this with an argument of 1 to reset to full velocity</p>
	 * <p>it is recommended to never use a value above 0.8, in order to ensure that the robot can always meet the demands created by the wave generator</p>
	 *
	 * @param scalingMultiplier the scaling multiplier in the domain [0, 1]
	 * @return self, for method chaining
	 */
	public WaveBuilder scaleRotationVelocity(double scalingMultiplier) {
		buildingMotionConstants = new MecanumMotionConstants(
				buildingMotionConstants.getTranslationalVelocityMultiplier(),
				scalingMultiplier,
				buildingMotionConstants.getTranslationalAccelerationMultiplier(),
				buildingMotionConstants.getRotationalAccelerationMultiplier(),
				buildingMotionConstants.getMaxTranslationalYVelocity(),
				buildingMotionConstants.getMaxTranslationalXVelocity(),
				buildingMotionConstants.getMaxTranslationalAngledVelocity(),
				buildingMotionConstants.getMaxRotationalVelocity(),
				buildingMotionConstants.getMaxTranslationalYAcceleration(),
				buildingMotionConstants.getMaxTranslationalXAcceleration(),
				buildingMotionConstants.getMaxTranslationalAngledAcceleration(),
				buildingMotionConstants.getMaxRotationalAcceleration()
		);
		if (builder != null) {
			builder.setMotionConstants(buildingMotionConstants);
		}
		return this;
	}


	/**
	 * scales the rotational acceleration for all further instructions, calling this method again will override the previously set value
	 * <p>call this with an argument of 1 to reset to full acceleration</p>
	 * <p>it is recommended to never use a value above 0.8, in order to ensure that the robot can always meet the demands created by the wave generator</p>
	 *
	 * @param scalingMultiplier the scaling multiplier in the domain [0, 1]
	 * @return self, for method chaining
	 */
	public WaveBuilder scaleRotationAcceleration(double scalingMultiplier) {
		buildingMotionConstants = new MecanumMotionConstants(
				buildingMotionConstants.getTranslationalVelocityMultiplier(),
				buildingMotionConstants.getRotationalVelocityMultiplier(),
				buildingMotionConstants.getTranslationalAccelerationMultiplier(),
				scalingMultiplier,
				buildingMotionConstants.getMaxTranslationalYVelocity(),
				buildingMotionConstants.getMaxTranslationalXVelocity(),
				buildingMotionConstants.getMaxTranslationalAngledVelocity(),
				buildingMotionConstants.getMaxRotationalVelocity(),
				buildingMotionConstants.getMaxTranslationalYAcceleration(),
				buildingMotionConstants.getMaxTranslationalXAcceleration(),
				buildingMotionConstants.getMaxTranslationalAngledAcceleration(),
				buildingMotionConstants.getMaxRotationalAcceleration()
		);
		if (builder != null) {
			builder.setMotionConstants(buildingMotionConstants);
		}
		return this;
	}

	/**
	 * generates and optimises a spline path that passes through all supplied points
	 * <p>this should be the primary movement method for your waves</p>
	 * <p>this motion prioritises translation, rather than rotation, if you want to rotate while you drive, and are not rotating enough, you should use {@link #scaleTranslationVelocity(double)} to reduce the translation speed, which will free up the drive base to rotate more</p>
	 *
	 * @param x
	 * @param y
	 * @param theta
	 * @return self, for method chaining
	 */
	public WaveBuilder splineTo(double x, double y, Angle theta) {
		handleState(BuildState.CURVE);
		addSegment(units.toMillimeters(x), units.toMillimeters(y), theta);
		return this;
	}

	/**
	 * instructs the robot to wait in place
	 *
	 * @param seconds the time of the wait (in seconds)
	 * @return self, for method chaining
	 */
	public WaveBuilder waitFor(double seconds) {
		handleState(BuildState.STOP);
		StopBuilder stopBuilder = (StopBuilder) builder;
		stopBuilder.addWait(previousTargetPose, seconds);
		return this;
	}

	/**
	 * turns to the provided angle
	 *
	 * @param theta the angle to turn to
	 * @return self, for method chaining
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
	 * @return self, for method chaining
	 */
	public WaveBuilder turn(Angle theta) {
		handleState(BuildState.TURN);
		addSegment(previousPose.getX(), previousPose.getY(), previousPose.getTheta().add(theta));
		return this;
	}

	/**
	 * @param x
	 * @param y
	 * @param theta
	 * @return self, for method chaining
	 */
	public WaveBuilder lineTo(double x, double y, Angle theta) {
		handleState(BuildState.LINE);
		addSegment(x, y, theta);
		return this;
	}

	private void addSegment(double x, double y, Angle theta) {
		Pose2D destination = new Pose2D(x, y, theta, units);
		builder.addFollowableSegment(previousPose, destination);
		previousPose = previousTargetPose = destination;
	}

	/**
	 * sets a callback command to occur with a timed offset reference to the end of the instruction before it.
	 *
	 * @param offset        a relative offset, in seconds
	 * @param markerReached the command to run
	 * @return self, for method chaining
	 */
	public WaveBuilder addOffsetActionMarker(double offset, CommandSignature markerReached) {
		builder.addOffsetCommandMarker(offset, Marker.MarkerType.COMMAND, markerReached);
		return this;
	}

	/**
	 * sets a callback action to occur with a timed offset reference to the end of the instruction before it.
	 *
	 * @param offset        a relative offset, in seconds
	 * @param markerReached the method to run
	 * @return self, for method chaining
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

			// ensures that previous pose is properly handled if a builder does not succeed in meeting targets (e.g. curve builder and turns)
			Followable.Output[] outputs = followables.get(followables.size() - 1).getOutputs();
			previousPose = outputs[outputs.length - 1].getPosition();
		}
		buildState = newState;
		switch (newState) {
			case CURVE:
				builder = new CurveBuilder(buildingMotionConstants, mecanumMotionConstants, obstacleMap);
				break;
			case LINE:
				builder = new LineBuilder(buildingMotionConstants, mecanumMotionConstants);
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
