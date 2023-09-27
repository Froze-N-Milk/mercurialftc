package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

public abstract class ArbFollower extends WaveFollower {
	public ArbFollower(MecanumMotionConstants motionConstants) {
		super(motionConstants);
	}

	@Override
	public void followOutput(Followable.Output output, double loopTime) {
		follow(output.getTranslationVector(), output.getRotationalVelocity());
	}

	public abstract void follow(Vector2D translationVector, double rotationalVelocity);
}
