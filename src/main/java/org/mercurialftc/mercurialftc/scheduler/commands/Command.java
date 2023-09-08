package org.mercurialftc.mercurialftc.scheduler.commands;

import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The low-level command interface to use
 */
public abstract class Command implements CommandSignature {
	private final Set<SubsystemInterface> requiredSubsystems;

	/**
	 * constructs a new command with the subsystems required
	 *
	 * @param requiredSubsystems requirements for this command
	 */
	protected Command(Set<SubsystemInterface> requiredSubsystems) {
		this.requiredSubsystems = requiredSubsystems;
	}

	/**
	 * constructs a new command with the subsystems required
	 *
	 * @param requiredSubsystems requirements for this command
	 */
	public Command(SubsystemInterface... requiredSubsystems) {
		this.requiredSubsystems = new HashSet<>(Arrays.asList(requiredSubsystems));
	}

	public void queue() {
		Scheduler.getSchedulerInstance().scheduleCommand(this);
	}

	@Override
	public final Set<SubsystemInterface> getRequiredSubsystems() {
		return requiredSubsystems;
	}
}
