package org.mercurialftc.mercurialftc.scheduler.subsystems;

import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;

public abstract class Subsystem implements SubsystemInterface {
	public final OpModeEX opModeEX;
	private final Command defaultCommand;

	public Subsystem(OpModeEX opModeEX) {
		this.defaultCommand = new LambdaCommand().addRequirements(this).isOverrideAllowed(true).execute(this::defaultCommandExecute).finish(() -> false);
		this.opModeEX = opModeEX;
		opModeEX.getScheduler().registerSubsystem(this);
		this.defaultCommand.queue();
	}

	public final Command getDefaultCommand() {
		return defaultCommand;
	}

	/**
	 * @return if currently required by a non-default command
	 */
	public final boolean isBusy() {
		return opModeEX.getScheduler().isBusy(this);
	}

}
