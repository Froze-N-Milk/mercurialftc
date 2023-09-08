package org.mercurialftc.mercurialftc.scheduler.commands;

import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class SequentialCommandGroup extends CommandGroup {
	private final ArrayList<Command> commands;
	private final boolean isOverrideAllowed;
	private int commandIndex;
	private int previousCommandIndex;

	public SequentialCommandGroup() {
		super(new HashSet<>());
		isOverrideAllowed = true;
		this.commands = new ArrayList<>();
		commandIndex = -1;
		previousCommandIndex = -2;
	}

	private SequentialCommandGroup(ArrayList<Command> commands, Set<SubsystemInterface> requirements, boolean isOverrideAllowed) {
		super(requirements);
		this.isOverrideAllowed = isOverrideAllowed;
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
			if (!command.getOverrideAllowed()) {
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
	public boolean getOverrideAllowed() {
		return isOverrideAllowed;
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
			command.end();
			commandIndex++;
			return;
		}

		command.execute();
	}

	@Override
	public void end() {

	}

	@Override
	public boolean finished() {
		return commandIndex >= commands.size() - 1;
	}
}
