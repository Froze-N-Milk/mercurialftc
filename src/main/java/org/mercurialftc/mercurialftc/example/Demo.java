package org.mercurialftc.mercurialftc.example;

import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;

public class Demo extends OpModeEX {
	DemoSubsystem demoSubsystem;
	/**
	 * called before {@link #initEX()}, solely for initialising all subsystems
	 */
	@Override
	public void registerSubsystems() {
		demoSubsystem = new DemoSubsystem(this);
	}
	
	@Override
	public void initEX() {
	
	}
	
	@Override
	public void registerTriggers() {
		gamepadEX1()
				.b()
				.isReleased(
						new LambdaCommand()
							.init(() -> {
								telemetry.addLine("WE ARE SO BALLINK");
										
							})
							.execute(() -> {
								telemetry.addLine("WE ARE SO CALLINK");
							})
							.finish(() -> {
								return true;
							})
				);
	}
	
	@Override
	public void init_loopEX() {
	
	}
	
	@Override
	public void startEX() {
	
	}
	
	@Override
	public void loopEX() {
	
	}
	
	@Override
	public void stopEX() {
	
	}
}
