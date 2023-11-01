package org.mercurialftc.mercurialftc.scheduler.commands;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.*;

@SuppressWarnings("unused")
public class ParallelCommandGroup implements CommandGroup {
	private final Map<Command, Boolean> commands;
	private final Set<SubsystemInterface> requiredSubsystems;
	private final Set<OpModeEX.OpModeEXRunStates> runStates;
	private boolean interruptable;

	/**
	 * a new empty ParallelCommandGroup, which will run all its commands at the same time
	 */
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
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new ParallelCommandGroup, with the added commands
	 */
	@Override
	public ParallelCommandGroup addCommands(Command... commands) {
		return addCommands(Arrays.asList(commands));
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new ParallelCommandGroup, with the added commands
	 */
	@Override
	public ParallelCommandGroup addCommands(Collection<Command> commands) {
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

		Scheduler.getSchedulerInstance().registerComposedCommands(commands);

		return new ParallelCommandGroup(
				newCommandMap,
				newRequirementSet,
				newRunStates,
				newInterruptable
		);
	}

	@Override
	public final boolean interruptable() {
		return interruptable;
	}

	@Override
	public final void initialise() {
		if (commands.isEmpty()) {
			throw new RuntimeException("Attempted to run empty ParallelCommandGroup, ParallelCommandGroup requires a minimum of 1 Command to be run");
		}
		interruptable = true;
		for (Map.Entry<Command, Boolean> commandRunning : commands.entrySet()) {
			if (commandRunning.getKey().getRunStates().contains(Scheduler.getSchedulerInstance().getRunState())) {
				commandRunning.getKey().initialise();
				commandRunning.setValue(true);
				interruptable &= commandRunning.getKey().interruptable();
			} else {
				commandRunning.setValue(false);
			}
		}
	}

	@Override
	public final void execute() {
		interruptable = true;
		for (Map.Entry<Command, Boolean> commandRunning : commands.entrySet()) {
			if (!(commandRunning.getValue() && commandRunning.getKey().getRunStates().contains(Scheduler.getSchedulerInstance().getRunState()))) {
				continue;
			}
			Command command = commandRunning.getKey();

			if (command.finished()) {
				command.end(false);
				commandRunning.setValue(false);
			} else {
				interruptable &= command.interruptable();
				command.execute();
			}
		}
	}

	@Override
	public final void end(boolean interrupted) {
		for (Map.Entry<Command, Boolean> commandRunning : commands.entrySet()) {
			if (commandRunning.getValue()) {
				Command command = commandRunning.getKey();
				if (command.getRunStates().contains(Scheduler.getSchedulerInstance().getRunState())) {
					command.end(interrupted);
				}
			}
		}
	}

	@Override
	public final Set<SubsystemInterface> getRequiredSubsystems() {
		return requiredSubsystems;
	}

	@Override
	public final Set<OpModeEX.OpModeEXRunStates> getRunStates() {
		return runStates;
	}

	@Override
	public final boolean finished() {
		return !commands.containsValue(true);
	}
}
