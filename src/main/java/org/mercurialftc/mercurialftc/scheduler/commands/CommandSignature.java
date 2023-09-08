package org.mercurialftc.mercurialftc.scheduler.commands;

import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.Set;

public interface CommandSignature {
	/**
	 * Gets run once when a command is first scheduled
	 */
	void initialise();

	/**
	 * Gets run once per loop until {@link #finished()}
	 */
	void execute();

	/**
	 * Gets run once when {@link #finished()} or when interrupted
	 */
	void end(boolean interrupted);

	/**
	 * the supplier for the natural end condition of the command
	 *
	 * @return true when the command should finish
	 */
	boolean finished();

	Set<SubsystemInterface> getRequiredSubsystems();

	default boolean interruptable() {
		return true;
	}
}
