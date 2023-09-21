package org.mercurialftc.mercurialftc.scheduler.commands;

import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.HashSet;
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

	/**
	 * @return the set of subsystems required by this command
	 */
	Set<SubsystemInterface> getRequiredSubsystems();

	/**
	 * @return the set of OpMode run states during which this command is allowed to run, defaults to just {@link org.mercurialftc.mercurialftc.scheduler.OpModeEX.OpModeEXRunStates#LOOP}
	 */
	default Set<OpModeEX.OpModeEXRunStates> getRunStates() {
		HashSet<OpModeEX.OpModeEXRunStates> defaultSet = new HashSet<>(1);
		defaultSet.add(OpModeEX.OpModeEXRunStates.LOOP);
		return defaultSet;
	}

	default boolean interruptable() {
		return true;
	}
}
