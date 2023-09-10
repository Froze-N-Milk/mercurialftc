package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.MotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.followable.Wave;

public abstract class WaveFollower {
	private final MotionConstants motionConstants;
	private Wave wave;
	private double currentTime, previousTime;
	private boolean finished;

	public WaveFollower(MotionConstants motionConstants) {
		this.motionConstants = motionConstants;
		currentTime = previousTime = 0;
	}

	public final Wave getWave() {
		return wave;
	}

	public final void setWave(Wave wave) {
		this.wave = wave;
	}

	public final void update(double currentTime) {
		this.previousTime = this.currentTime;
		this.currentTime = currentTime;
		finished = wave.update(currentTime);
		followOutput(wave.getOutput(), currentTime - previousTime);
	}

	protected abstract void followOutput(Followable.Output output, double loopTime);

	protected MotionConstants getMotionConstants() {
		return motionConstants;
	}

	public boolean isFinished() {
		return finished;
	}
}
