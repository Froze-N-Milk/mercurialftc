package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.Wave;

public abstract class WaveFollower {
	private Wave wave;
	public final void setWave(Wave wave) {
		this.wave = wave;
	}
	
	public final void update(double currentTime) {
		wave.update(currentTime);
		followOutput(wave.getOutput());
	}
	protected abstract void followOutput(Followable.Output output);
}
