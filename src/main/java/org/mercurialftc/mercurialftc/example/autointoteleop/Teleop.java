package org.mercurialftc.mercurialftc.example.autointoteleop;

import org.mercurialftc.mercurialftc.example.DemoSubsystem;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.Scheduler;

/**
 * see readme.md in this directory
 */
public class Teleop extends OpModeEX {
	DemoSubsystem demoSubsystem;
	/**
	 * called before {@link #initEX()}, solely for initialising all subsystems, ensures that they are registered with the correct {@link com.mercurialftc.mercurialftc.scheduler.Scheduler}, and that their init methods will be run
	 */
	@Override
	public void registerSubsystems() {
		demoSubsystem = (DemoSubsystem) getScheduler().getStoredSubsystem(0); // we get back the first stored subsystem, and cast it back to our known type
//		demoSubsystem2 = (DemoSubsystem2) getScheduler().getStoredSubsystem(1); // we could get back the second stored subsystem, and cast it back to our known type (but we don't have any more subsystems in this case)
	}
	
	/**
	 * should contain your regular init code
	 */
	@Override
	public void initEX() {
		Scheduler.refreshScheduler = true; // after this OpModeEX runs we want to go back to resetting the scheduler
	}
	
	/**
	 * registers triggers after the subsystem and regular init code,
	 * useful for organisation of your OpModeEX, but functionally no different to initialising them at the end of {@link #initEX()}
	 */
	@Override
	public void registerTriggers() {
		// register all your driver and operator control triggers here
	}
	
	@Override
	public void init_loopEX() {
	
	}
	
	@Override
	public void startEX() {
		// probably don't reset anything here
	}
	
	@Override
	public void loopEX() {
		// your code!
		
	}
	
	@Override
	public void stopEX() {
		// no need to do anything specific here
	}
}
