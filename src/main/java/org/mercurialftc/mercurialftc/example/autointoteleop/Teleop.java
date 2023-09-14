package org.mercurialftc.mercurialftc.example.autointoteleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.mercurialftc.mercurialftc.example.DemoSubsystem;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.Scheduler;

import java.io.IOException;

/**
 * see readme.md in this directory
 */
@Disabled
@TeleOp
public class Teleop extends OpModeEX {
	private DemoSubsystem demoSubsystem;

	/**
	 * called before {@link #initEX()}, solely for initialising all subsystems, ensures that they are registered with the correct {@link com.mercurialftc.mercurialftc.scheduler.Scheduler}, and that their init methods will be run
	 */
	@Override
	public void registerSubsystems() {
		demoSubsystem = (DemoSubsystem) getScheduler().getStoredSubsystem("my very own Demosubsystem"); // we get back the first stored subsystem, and cast it back to our known type, this also removes it from the stored subsystems
//		demoSubsystem2 = (DemoSubsystem2) getScheduler().getStoredSubsystem("demosubsystem2"); // we could get back the second stored subsystem, and cast it back to our known type (but we don't have any more subsystems in this case)
	}

	/**
	 * should contain your regular init code
	 */
	@Override
	public void initEX() {
		Scheduler.getConfigOptionsManager().updateValue(Scheduler.ConfigOptions.SCHEDULER_REFRESH_ENABLED.getOption(), true); // after this OpModeEX runs we might want to go back to resetting the scheduler
	}

	/**
	 * registers triggers after the subsystem and regular init code,
	 * useful for organisation of your OpModeEX, but functionally no different to initialising them at the end of {@link #initEX()}
	 */
	@Override
	public void registerTriggers() {
		// register all your driver and operator control triggers here
		gamepadEX1().a().whilePressed(demoSubsystem.getDefaultCommand()); // this doesn't actually do anything as this command is guaranteed to be running, and we aren't running anything else but, for demonstration's sake
	}

	@Override
	public void init_loopEX() {

	}

	@Override
	public void startEX() {

	}

	@Override
	public void loopEX() {
		// your code!

	}

	@Override
	public void stopEX() {
		try {
			Scheduler.getConfigOptionsManager().update(); // actually updates the setting we changed
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// no need to do anything specific here
	}
}
