package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.Wave;
import org.mercurialftc.mercurialftc.silversurfer.followable.MotionConstants;

public abstract class WaveFollower {
	private Wave wave;
	private double currentTime, previousTime;
	private final MotionConstants motionConstants;
	private boolean finished;

	protected WaveFollower(MotionConstants motionConstants) {
		this.motionConstants = motionConstants;
		currentTime = previousTime = 0;
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
