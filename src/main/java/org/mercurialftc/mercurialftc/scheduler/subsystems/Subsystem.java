package org.mercurialftc.mercurialftc.scheduler.subsystems;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.scheduler.commands.LambdaCommand;

@SuppressWarnings("unused")
public abstract class Subsystem implements SubsystemInterface {
	public final OpModeEX opModeEX;
	private final Command defaultCommand;

	public Subsystem(@NotNull OpModeEX opModeEX) {
		this.defaultCommand = new LambdaCommand().setRequirements(this).setInterruptable(true).execute(this::defaultCommandExecute).finish(() -> false);
		this.opModeEX = opModeEX;
		opModeEX.getScheduler().registerSubsystem(this);
		this.defaultCommand.queue();
	}

	public Command getDefaultCommand() {
		return defaultCommand;
	}

	/**
	 * @return if currently required by a non-default command
	 */
	public final boolean isBusy() {
		return opModeEX.getScheduler().isBusy(this);
	}

}
