package org.mercurialftc.mercurialftc.scheduler.commands;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.*;

@SuppressWarnings("unused")
public class ParallelCommandGroup implements Command {
	private final Map<Command, Boolean> commands;
	private final boolean interruptable;
	private final Set<SubsystemInterface> requiredSubsystems;
	private final Set<OpModeEX.OpModeEXRunStates> runStates;
	private Command currentCommand;


	public ParallelCommandGroup() {
		this.requiredSubsystems = new HashSet<>();
		this.runStates = new HashSet<>(2);
		interruptable = true;
		this.commands = new HashMap<>();
	}

	private ParallelCommandGroup(@NotNull HashMap<Command, Boolean> commands, Set<SubsystemInterface> requirements, Set<OpModeEX.OpModeEXRunStates> runStates, boolean interruptable) {
		this.requiredSubsystems = requirements;
		this.runStates = runStates;
		this.interruptable = interruptable;
		this.commands = commands;

		Scheduler.getSchedulerInstance().registerComposedCommands(commands.keySet());
	}

	@Override
	public void queue() {
		Command.super.queue();
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new ParallelCommandGroup, with the added commands
	 */
	public ParallelCommandGroup addCommands(Command... commands) {
		return addCommands(Arrays.asList(commands));
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new ParallelCommandGroup, with the added commands
	 */
	public ParallelCommandGroup addCommands(List<Command> commands) {
		if (this.commands.containsValue(true)) {
			throw new IllegalStateException(
					"Commands cannot be added to a composition while it's running");
		}

		HashMap<Command, Boolean> newCommandMap = new HashMap<>(this.commands);

		Set<SubsystemInterface> newRequirementSet = new HashSet<>(this.getRequiredSubsystems());
		boolean newInterruptable = interruptable();
		HashSet<OpModeEX.OpModeEXRunStates> newRunStates = new HashSet<>(2);


		for (Command command : commands) {
			if (!Collections.disjoint(command.getRequiredSubsystems(), getRequiredSubsystems())) {
				throw new IllegalArgumentException(
						"Multiple commands in a parallel composition cannot require the same subsystems");
			}
			newCommandMap.put(command, false);
			newRequirementSet.addAll(command.getRequiredSubsystems());
			newInterruptable &= command.interruptable();
			newRunStates.addAll(command.getRunStates());
		}

		return new ParallelCommandGroup(
				newCommandMap,
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
		for (Map.Entry<Command, Boolean> commandRunning : commands.entrySet()) {
			commandRunning.getKey().initialise();
			commandRunning.setValue(true);
		}
	}

	@Override
	public void execute() {
		for (Map.Entry<Command, Boolean> commandRunning : commands.entrySet()) {
			if (!commandRunning.getValue()) {
				continue;
			}
			commandRunning.getKey().execute();
			if (commandRunning.getKey().finished()) {
				commandRunning.getKey().end(false);
				commandRunning.setValue(false);
			}
		}
	}

	@Override
	public void end(boolean interrupted) {
		if (interrupted) {
			for (Map.Entry<Command, Boolean> commandRunning : commands.entrySet()) {
				if (commandRunning.getValue()) {
					commandRunning.getKey().end(true);
				}
			}
		}
	}

	@Override
	public Set<SubsystemInterface> getRequiredSubsystems() {
		return requiredSubsystems;
	}

	@Override
	public Set<OpModeEX.OpModeEXRunStates> getRunStates() {
		return runStates;
	}

	@Override
	public boolean finished() {
		return !commands.containsValue(true);
	}
}
