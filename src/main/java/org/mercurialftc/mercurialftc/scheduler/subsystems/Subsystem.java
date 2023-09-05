package org.mercurialftc.mercurialftc.scheduler.subsystems;

import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;

public abstract class Subsystem implements SubsystemInterface {
	private final Command defaultCommand;
	public final OpModeEX opModeEX;
	
	public Subsystem(OpModeEX opModeEX) {
		this.defaultCommand = new LambdaCommand().addRequirements(this).isOverrideAllowed(true).execute(this::defaultCommandExecute).finish(() -> false);
		this.opModeEX = opModeEX;
		opModeEX.getScheduler().registerSubsystem(this);
		this.defaultCommand.queue();
	}

	public final Command getDefaultCommand() {
		return defaultCommand;
	}
}
