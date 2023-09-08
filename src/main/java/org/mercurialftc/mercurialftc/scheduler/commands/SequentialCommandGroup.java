package org.mercurialftc.mercurialftc.scheduler.commands;

import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SequentialCommandGroup extends Command {
	private final ArrayList<CommandSignature> commands;
	private final boolean interruptable;
	private int commandIndex;
	private CommandSignature currentCommand;

	public SequentialCommandGroup() {
		super(new HashSet<>());
		interruptable = true;
		this.commands = new ArrayList<>();
		commandIndex = -1;
	}

	private SequentialCommandGroup(ArrayList<CommandSignature> commands, Set<SubsystemInterface> requirements, boolean interruptable) {
		super(requirements);
		this.interruptable = interruptable;
		this.commands = commands;
		commandIndex = -1;
	}

	@Override
	public void queue() {
		Scheduler.getSchedulerInstance().registerComposedCommands(commands);
		super.queue();
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new SequentialCommandGroup, with the added commands
	 */
	public SequentialCommandGroup addCommands(CommandSignature... commands) {
		if (commandIndex != -1) {
			throw new IllegalStateException(
					"Commands cannot be added to a composition while it's running");
		}

		ArrayList<CommandSignature> newCommandList = new ArrayList<>(this.commands);
		Collections.addAll(newCommandList, commands);

		Set<SubsystemInterface> newRequirementSet = new HashSet<>(this.getRequiredSubsystems());
		boolean newInterruptable = this.interruptable();

		for (CommandSignature command : commands) {
			newRequirementSet.addAll(command.getRequiredSubsystems());
			newInterruptable &= command.interruptable();
		}

		return new SequentialCommandGroup(
				newCommandList,
				newRequirementSet,
				newInterruptable
		);
	}

	@Override
	public boolean interruptable() {
		return interruptable;
	}

	@Override
	public void initialise() {
		if (commands.isEmpty()) {
			throw new RuntimeException("Attempted to run empty SequentialCommandGroup, SequentialCommandGroupRequires a minimum of 1 Command to be run");
		}
		commandIndex = 0;
		currentCommand = commands.get(commandIndex);
	}

	@Override
	public void execute() {
		if (currentCommand.finished()) {
			currentCommand.end(false);
			commandIndex++;
		}
		if (commandIndex < commands.size()) {
			currentCommand = commands.get(commandIndex);
			currentCommand.initialise();
		} else {
			return;
		}
		currentCommand.execute();
	}

	@Override
	public void end(boolean interrupted) {
		if (interrupted
				&& !commands.isEmpty()
				&& commandIndex > -1
				&& commandIndex < commands.size()) {
			commands.get(commandIndex).end(true);
		}
		commandIndex = -1;
	}

	@Override
	public boolean finished() {
		return commandIndex >= commands.size() - 1;
	}
}
