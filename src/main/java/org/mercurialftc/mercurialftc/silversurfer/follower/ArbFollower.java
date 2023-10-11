package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

public abstract class ArbFollower extends WaveFollower {
	public ArbFollower(MecanumMotionConstants motionConstants) {
		super(motionConstants);
	}

	/**
	 * transforms the output into unit instructions for use in {@link #follow(Vector2D, double, double)}
	 */
	@Override
	protected abstract void followOutput(@NotNull Followable.Output output, double loopTime);

	/**
	 * takes in driver inputs, rotates them and sends them to the wheels
	 *
	 * @param translationVector  should be a unit vector
	 * @param rotationalVelocity should be in the domain [-1, 1]
	 */
	public abstract void follow(Vector2D translationVector, double rotationalVelocity, double loopTime);
}
