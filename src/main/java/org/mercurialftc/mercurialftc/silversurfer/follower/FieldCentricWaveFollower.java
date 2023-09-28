package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.geometry.AngleRadians;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.tracker.Tracker;

public class FieldCentricWaveFollower extends WaveFollower {
	private final Tracker tracker;
	private final ArbFollower arbFollower;

	public FieldCentricWaveFollower(@NotNull Tracker tracker, @NotNull ArbFollower arbFollower) {
		super(arbFollower.getMotionConstants());
		this.arbFollower = arbFollower;
		this.tracker = tracker;
	}

	@Override
	protected void followOutput(@NotNull Followable.Output output, double loopTime) {
		Vector2D translationVector = output.getTranslationVector();
		translationVector = translationVector.rotate(new AngleRadians(-tracker.getPose2D().getTheta().getRadians()));

		arbFollower.follow(
				translationVector,
				output.getRotationalVelocity()
		);
	}
}
