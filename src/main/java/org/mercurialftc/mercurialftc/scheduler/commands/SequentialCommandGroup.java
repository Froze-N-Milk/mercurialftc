package org.mercurialftc.mercurialftc.scheduler.commands;

import org.jetbrains.annotations.Nullable;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.*;

@SuppressWarnings("unused")
public class SequentialCommandGroup implements CommandGroup {
	private final ArrayList<Command> commands;
	private final boolean interruptable;
	private final Set<SubsystemInterface> requiredSubsystems;
	private final Set<OpModeEX.OpModeEXRunStates> runStates;
	private int commandIndex;
	@Nullable
	private Command currentCommand;

	public SequentialCommandGroup() {
		this.requiredSubsystems = new HashSet<>();
		this.runStates = new HashSet<>(2);
		interruptable = true;
		this.commands = new ArrayList<>();
		commandIndex = -1;
	}

	private SequentialCommandGroup(ArrayList<Command> commands, Set<SubsystemInterface> requirements, Set<OpModeEX.OpModeEXRunStates> runStates, boolean interruptable) {
		this.requiredSubsystems = requirements;
		this.runStates = runStates;
		this.commands = commands;
		this.interruptable = interruptable;
		commandIndex = -1;
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new SequentialCommandGroup, with the added commands
	 */
	public SequentialCommandGroup addCommands(Command... commands) {
		return addCommands(Arrays.asList(commands));
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new SequentialCommandGroup, with the added commands
	 */
	public SequentialCommandGroup addCommands(Collection<Command> commands) {
		if (commandIndex != -1) {
			throw new IllegalStateException(
					"Commands cannot be added to a composition while it is running");
		}

		ArrayList<Command> newCommandList = new ArrayList<>(this.commands);
		newCommandList.addAll(commands);

		Set<SubsystemInterface> newRequirementSet = new HashSet<>(this.getRequiredSubsystems());
		boolean newInterruptable = this.interruptible();

		HashSet<OpModeEX.OpModeEXRunStates> newRunStates = new HashSet<>(2);

		for (Command command : commands) {
			newRequirementSet.addAll(command.getRequiredSubsystems());
			newInterruptable &= command.interruptible();
			newRunStates.addAll(command.getRunStates());
		}

		Scheduler.getSchedulerInstance().registerComposedCommands(commands);

		return new SequentialCommandGroup(
				newCommandList,
				newRequirementSet,
				newRunStates,
				newInterruptable
		);
	}

	@Override
	public final boolean interruptible() {
		return interruptable;
	}

	@Override
	public final void initialise() {
		if (commands.isEmpty()) {
			throw new RuntimeException("Attempted to run empty SequentialCommandGroup, SequentialCommandGroupRequires requires a minimum of 1 Command to be run");
		}
		commandIndex = 0;
		currentCommand = commands.get(commandIndex);
		if (currentCommand.getRunStates().contains(Scheduler.getSchedulerInstance().getRunState())) {
			currentCommand.initialise();
		}
	}

	@Override
	public final void execute() {
		if (currentCommand == null) return;
		if (currentCommand.finished() || !currentCommand.getRunStates().contains(Scheduler.getSchedulerInstance().getRunState())) {
			if (currentCommand.getRunStates().contains(Scheduler.getSchedulerInstance().getRunState())) {
				currentCommand.end(false);
			}
			commandIndex++;
			if (commandIndex < commands.size()) {
				currentCommand = commands.get(commandIndex);
				if (currentCommand.getRunStates().contains(Scheduler.getSchedulerInstance().getRunState())) {
					currentCommand.initialise();
				}
			} else {
				currentCommand = null;
				return;
			}
		}
		if (currentCommand.getRunStates().contains(Scheduler.getSchedulerInstance().getRunState())) {
			currentCommand.execute();
		}
	}

	@Override
	public final void end(boolean interrupted) {
		commandIndex = -1;
		if (currentCommand == null || !currentCommand.getRunStates().contains(Scheduler.getSchedulerInstance().getRunState()))
			return;
		currentCommand.end(interrupted);
	}

	@Override
	public final boolean finished() {
		return commandIndex >= commands.size() || currentCommand == null;
	}

	@Override
	public final Set<SubsystemInterface> getRequiredSubsystems() {
		return requiredSubsystems;
	}

	@Override
	public final Set<OpModeEX.OpModeEXRunStates> getRunStates() {
		return runStates;
	}
}
