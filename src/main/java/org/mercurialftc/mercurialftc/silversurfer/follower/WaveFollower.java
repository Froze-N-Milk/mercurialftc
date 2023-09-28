package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.followable.Wave;

public abstract class WaveFollower {
	private final MecanumMotionConstants motionConstants;
	private Wave wave;
	private double currentTime, previousTime;
	private boolean finished;

	public WaveFollower(MecanumMotionConstants motionConstants) {
		this.motionConstants = motionConstants;
		currentTime = previousTime = 0;
		finished = false;
	}

	public final Wave getWave() {
		return wave;
	}

	public final void setWave(Wave wave) {
		this.wave = wave;
		this.finished = false;
	}

	public final void update(double currentTime) {
		this.previousTime = this.currentTime;
		this.currentTime = currentTime;
		finished = wave.update(currentTime);
		followOutput(wave.getOutput(), currentTime - previousTime);
	}

	protected abstract void followOutput(Followable.Output output, double loopTime);

	protected MecanumMotionConstants getMotionConstants() {
		return motionConstants;
	}

	public boolean isFinished() {
		return finished;
	}
}
