package org.mercurialftc.mercurialftc.scheduler.commands;

import org.jetbrains.annotations.Nullable;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.*;

@SuppressWarnings("unused")
public class AdvancingStateCommandGroup implements CommandGroup {
	private final ArrayList<Command> commands;
	private final Set<SubsystemInterface> requiredSubsystems;
	private final Set<OpModeEX.OpModeEXRunStates> runStates;
	private int commandIndex;
	@Nullable
	private Command currentCommand;
	private boolean advanceState;

	/**
	 * a new empty AdvancingStateCommandGroup, which will interrupt its current command (if running) and move on to the next one when .queue() is called on it again
	 */
	public AdvancingStateCommandGroup() {
		this(new ArrayList<>(), new HashSet<>(), new HashSet<>(2));
	}

	private AdvancingStateCommandGroup(ArrayList<Command> commands, Set<SubsystemInterface> requirements, Set<OpModeEX.OpModeEXRunStates> runStates) {
		this.requiredSubsystems = requirements;
		this.runStates = runStates;
		this.commands = commands;
		commandIndex = -1;
		advanceState = false;
	}

	@Override
	public void queue() {
		CommandGroup.super.queue();
		commandIndex++;
		commandIndex %= commands.size();
		advanceState = true;
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new AdvancingStateCommandGroup, with the added commands
	 */
	@Override
	public AdvancingStateCommandGroup addCommands(Command... commands) {
		return addCommands(Arrays.asList(commands));
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new AdvancingStateCommandGroup, with the added commands
	 */
	@Override
	public AdvancingStateCommandGroup addCommands(Collection<Command> commands) {
		if (commandIndex != -1) {
			throw new IllegalStateException(
					"Commands cannot be added to a composition while it is running");
		}

		ArrayList<Command> newCommandList = new ArrayList<>(this.commands);
		newCommandList.addAll(commands);

		Set<SubsystemInterface> newRequirementSet = new HashSet<>(this.getRequiredSubsystems());

		HashSet<OpModeEX.OpModeEXRunStates> newRunStates = new HashSet<>(2);

		for (Command command : commands) {
			newRequirementSet.addAll(command.getRequiredSubsystems());
			newRunStates.addAll(command.getRunStates());
		}

		Scheduler.getSchedulerInstance().registerComposedCommands(commands);

		return new AdvancingStateCommandGroup(
				newCommandList,
				newRequirementSet,
				newRunStates
		);
	}

	@Override
	public final boolean interruptable() {
		return currentCommand == null || currentCommand.interruptable();
	}

	@Override
	public final void initialise() {
		if (commands.isEmpty()) {
			throw new RuntimeException("Attempted to run empty AdvancingStateCommandGroup, AdvancingStateCommandGroup requires a minimum of 1 Command to be run");
		}
		commandIndex = 0;
		currentCommand = commands.get(commandIndex);
	}

	@Override
	public final void execute() {
		if (advanceState) {
			if (currentCommand != null) currentCommand.end(true);
			currentCommand = commands.get(commandIndex);
			if (currentCommand != null) currentCommand.initialise();
			advanceState = false;
		}

		if (currentCommand == null) return;

		if (currentCommand.finished()) {
			currentCommand.end(false);
			currentCommand = null;
		} else currentCommand.execute();
	}

	@Override
	public final void end(boolean interrupted) {
		if (interrupted && currentCommand != null) currentCommand.end(true);
		else if (!interrupted && currentCommand != null) currentCommand.end(false);
		commandIndex = -1;
	}

	@Override
	public final boolean finished() {
		return false;
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
