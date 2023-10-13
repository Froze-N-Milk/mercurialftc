package org.mercurialftc.mercurialftc.scheduler.commands;

import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.*;

@SuppressWarnings("unused")
public class SequentialCommandGroup implements CommandSignature {
	private final ArrayList<CommandSignature> commands;
	private final boolean interruptable;
	private final Set<SubsystemInterface> requiredSubsystems;
	private final Set<OpModeEX.OpModeEXRunStates> runStates;
	private int commandIndex;
	private CommandSignature currentCommand;

	public SequentialCommandGroup() {
		this.requiredSubsystems = new HashSet<>();
		this.runStates = new HashSet<>(2);
		interruptable = true;
		this.commands = new ArrayList<>();
		commandIndex = -1;
	}

	private SequentialCommandGroup(ArrayList<CommandSignature> commands, Set<SubsystemInterface> requirements, Set<OpModeEX.OpModeEXRunStates> runStates, boolean interruptable) {
		this.requiredSubsystems = requirements;
		this.runStates = runStates;
		this.commands = commands;
		this.interruptable = interruptable;
		commandIndex = -1;
		Scheduler.getSchedulerInstance().registerComposedCommands(commands);
	}

	@Override
	public void queue() {
		CommandSignature.super.queue();
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new SequentialCommandGroup, with the added commands
	 */
	public SequentialCommandGroup addCommands(CommandSignature... commands) {
		return addCommands(Arrays.asList(commands));
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new SequentialCommandGroup, with the added commands
	 */
	public SequentialCommandGroup addCommands(List<CommandSignature> commands) {
		if (commandIndex != -1) {
			throw new IllegalStateException(
					"Commands cannot be added to a composition while it is running");
		}

		ArrayList<CommandSignature> newCommandList = new ArrayList<>(this.commands);
		newCommandList.addAll(commands);

		Set<SubsystemInterface> newRequirementSet = new HashSet<>(this.getRequiredSubsystems());
		boolean newInterruptable = this.interruptable();

		HashSet<OpModeEX.OpModeEXRunStates> newRunStates = new HashSet<>(2);

		for (CommandSignature command : commands) {
			newRequirementSet.addAll(command.getRequiredSubsystems());
			newInterruptable &= command.interruptable();
			newRunStates.addAll(command.getRunStates());
		}

		return new SequentialCommandGroup(
				newCommandList,
				newRequirementSet,
				newRunStates,
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

	@Override
	public Set<SubsystemInterface> getRequiredSubsystems() {
		return requiredSubsystems;
	}

	@Override
	public Set<OpModeEX.OpModeEXRunStates> getRunStates() {
		return runStates;
	}
}
