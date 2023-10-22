package org.mercurialftc.mercurialftc.silversurfer.followable;

import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

public abstract class Followable {
	private Output[] outputs;
	private Marker[] markers;

	public final Output[] getOutputs() {
		return outputs;
	}

	protected final void setOutputs(Output[] outputs) {
		this.outputs = outputs;
	}

	public final Marker[] getMarkers() {
		return markers;
	}

	protected final void setMarkers(Marker[] markers) {
		this.markers = markers;
	}

	public static class Output {
		private final Vector2D translationVector; // controls x and y
		private final double callbackTime;
		private final Pose2D position, destination;
		private final double rotationalVelocity; // controls heading
		private double accumulatedTime;

		public Output(Vector2D translationVector, double rotationalVelocity, double callbackTime, Pose2D position, Pose2D destination) {
			this.translationVector = translationVector;
			this.rotationalVelocity = rotationalVelocity;
			this.callbackTime = callbackTime;
			this.accumulatedTime = 0;
			this.position = position;
			this.destination = destination;
		}

		public Vector2D getTranslationVector() {
			return translationVector;
		}

		public double getCallbackTime() {
			return callbackTime + accumulatedTime;
		}

		public double getRotationalVelocity() {
			return rotationalVelocity;
		}

		public Pose2D getPosition() {
			return position;
		}

		public Pose2D getDestination() {
			return destination;
		}

		public double getAccumulatedTime() {
			return accumulatedTime;
		}

		public void setAccumulatedTime(double accumulatedTime) {
			this.accumulatedTime = accumulatedTime;
		}
	}
}
