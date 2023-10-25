package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.Wave;

public abstract class AbstractWaveFollower implements WaveFollower {
	private Wave wave;
	private double currentTime, previousTime;
	private boolean finished;

	public AbstractWaveFollower() {
		currentTime = previousTime = 0;
		finished = false;
	}

	@Override
	public final Wave getWave() {
		return wave;
	}

	@Override
	public final void setWave(Wave wave) {
		this.wave = wave;
		this.finished = false;
	}

	@Override
	public final void update(double currentTime) {
		this.previousTime = this.currentTime;
		this.currentTime = currentTime;
		finished = wave.update(currentTime);
		followOutput(wave.getOutput(), currentTime - previousTime);
	}

	@Override
	public boolean isFinished() {
		return finished;
	}
}
