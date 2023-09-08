package org.mercurialftc.mercurialftc.scheduler.commands;

import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.*;

public class ParallelCommandGroup extends Command {
	private final Map<CommandSignature, Boolean> commands;
	private final boolean interruptable;
	private CommandSignature currentCommand;

	public ParallelCommandGroup() {
		super(new HashSet<>());
		interruptable = true;
		this.commands = new HashMap<>();
	}

	private ParallelCommandGroup(HashMap<CommandSignature, Boolean> commands, Set<SubsystemInterface> requirements, boolean interruptable) {
		super(requirements);
		this.interruptable = interruptable;
		this.commands = commands;
	}

	@Override
	public void queue() {
		Scheduler.getSchedulerInstance().registerComposedCommands(commands.keySet().toArray(new CommandSignature[0]));
		super.queue();
	}

	/**
	 * non-mutating
	 *
	 * @param commands new commands to add
	 * @return a new ParallelCommandGroup, with the added commands
	 */
	public ParallelCommandGroup addCommands(CommandSignature... commands) {
		if (this.commands.containsValue(true)) {
			throw new IllegalStateException(
					"Commands cannot be added to a composition while it's running");
		}

		HashMap<CommandSignature, Boolean> newCommandMap = new HashMap<>(this.commands);


		Set<SubsystemInterface> newRequirementSet = new HashSet<>(this.getRequiredSubsystems());
		boolean newInterruptable = interruptable();

		for (CommandSignature command : commands) {
			if (!Collections.disjoint(command.getRequiredSubsystems(), getRequiredSubsystems())) {
				throw new IllegalArgumentException(
						"Multiple commands in a parallel composition cannot require the same subsystems");
			}
			newCommandMap.put(command, false);
			newRequirementSet.addAll(command.getRequiredSubsystems());
			newInterruptable &= command.interruptable();
		}

		return new ParallelCommandGroup(
				newCommandMap,
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
		for (Map.Entry<CommandSignature, Boolean> commandRunning : commands.entrySet()) {
			commandRunning.getKey().initialise();
			commandRunning.setValue(true);
		}
	}

	@Override
	public void execute() {
		for (Map.Entry<CommandSignature, Boolean> commandRunning : commands.entrySet()) {
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
			for (Map.Entry<CommandSignature, Boolean> commandRunning : commands.entrySet()) {
				if (commandRunning.getValue()) {
					commandRunning.getKey().end(true);
				}
			}
		}
	}

	@Override
	public boolean finished() {
		return !commands.containsValue(true);
	}
}
