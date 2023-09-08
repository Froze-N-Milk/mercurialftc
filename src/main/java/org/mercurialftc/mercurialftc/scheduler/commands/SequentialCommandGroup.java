package org.mercurialftc.mercurialftc.scheduler.commands;

import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SequentialCommandGroup extends Command {
	private final ArrayList<Command> commands;
	private final boolean interruptable;
	private int commandIndex;
	private int previousCommandIndex;

	public SequentialCommandGroup() {
		super(new HashSet<>());
		interruptable = true;
		this.commands = new ArrayList<>();
		commandIndex = -1;
		previousCommandIndex = -2;
	}

	private SequentialCommandGroup(ArrayList<Command> commands, Set<SubsystemInterface> requirements, boolean interruptable) {
		super(requirements);
		this.interruptable = interruptable;
		this.commands = commands;
		commandIndex = -1;
		previousCommandIndex = -2;
	}

	public SequentialCommandGroup addCommands(Command... commands) {

		ArrayList<Command> newCommandList = new ArrayList<>(this.commands);
		Collections.addAll(newCommandList, commands);

		Set<SubsystemInterface> newRequirementSet = new HashSet<>(this.getRequiredSubsystems());
		boolean newIsOverrideAllowed = true;

		for (Command command : commands) {
			newRequirementSet.addAll(command.getRequiredSubsystems());
			if (!command.interruptable()) {
				newIsOverrideAllowed = false;
			}
		}

		return new SequentialCommandGroup(
				newCommandList,
				newRequirementSet,
				newIsOverrideAllowed
		);
	}

	@Override
	public boolean interruptable() {
		return interruptable;
	}

	@Override
	public void initialise() {
		commandIndex = 0;
		previousCommandIndex = -1;
	}

	@Override
	public void execute() {
		Command command = commands.get(commandIndex);

		if (previousCommandIndex != commandIndex) {
			previousCommandIndex = commandIndex;
			command.initialise();
		}

		if (command.finished()) {
			command.end(false);
			commandIndex++;
			return;
		}

		command.execute();
	}

	@Override
	public void end(boolean interrupted) {

	}

	@Override
	public boolean finished() {
		return commandIndex >= commands.size() - 1;
	}
}
