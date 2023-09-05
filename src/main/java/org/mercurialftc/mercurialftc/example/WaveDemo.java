package org.mercurialftc.mercurialftc.example;

import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;
import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;
import org.mercurialftc.mercurialftc.silversurfer.followable.WaveBuilder;
import org.mercurialftc.mercurialftc.silversurfer.geometry.AngleDegrees;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.motionprofile.MotionConstants;

public class WaveDemo {
	public void something() {
		WaveBuilder waveBuilder  = new WaveBuilder(
				new Pose2D(),
				Units.MILLIMETER,
				new MotionConstants(0, 0, 0, 0, 0, 0)
		)
				.splineTo(10, 10, new AngleDegrees(100))
				.waitFor(10)
				.addOffsetActionMarker(-5, new LambdaCommand().init(() -> {/*lift*/}));
		
		
		
		for (int i = 0; i < 5; i++) {
			waveBuilder.splineTo(i * 5, i * 5, new AngleDegrees(i));
		}
	}
}
