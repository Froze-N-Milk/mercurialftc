package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.Wave;

public interface WaveFollower {
	Wave getWave();

	/**
	 * @param wave the wave to follow
	 */
	void setWave(Wave wave);

	/**
	 * coordinates between updating the wave and following the output with one input
	 *
	 * @param currentTime the time in seconds since the start of the wave following process
	 */
	void update(double currentTime);

	/**
	 * @param output   the output velocities and information, usually generated by a wave
	 * @param loopTime current loop time, to ensure performance
	 */
	void followOutput(@NotNull Followable.Output output, double loopTime);


	/**
	 * @return if the wave is finished being followed
	 */
	boolean isFinished();
}
