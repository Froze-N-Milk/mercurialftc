package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.mercurialftc.mercurialftc.silversurfer.followable.MotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

public abstract class ArbFollower {
	private final MotionConstants motionConstants;

	public ArbFollower(MotionConstants motionConstants) {
		this.motionConstants = motionConstants;
	}

	public abstract void follow(Vector2D translationVector, double rotationalVelocity);

	protected MotionConstants getMotionConstants() {
		return motionConstants;
	}

}
