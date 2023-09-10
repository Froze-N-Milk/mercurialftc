package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.MotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

public abstract class ArbFollower extends WaveFollower {
	public ArbFollower(MotionConstants motionConstants) {
		super(motionConstants);
	}

	@Override
	public final void followOutput(Followable.Output output, double loopTime) {
		follow(output.getTranslationVector(), output.getRotationalVelocity());
	}

	public abstract void follow(Vector2D translationVector, double rotationalVelocity);
}
