package org.mercurialftc.mercurialftc.scheduler.commands;

import org.jetbrains.annotations.Nullable;
import org.mercurialftc.mercurialftc.scheduler.OpModeEX;
import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class SelectionCommandGroup<E extends Enum<E>> implements Command {
	private final HashMap<Enum<E>, Command> selectionMap;
	private final Set<SubsystemInterface> requiredSubsystems;
	private final Set<OpModeEX.OpModeEXRunStates> runStates;
	private final Enum<E> entrySelection;
	private Enum<E> selection;
	@Nullable
	private Command currentCommand;
	private boolean switchSelection;

	public SelectionCommandGroup(Enum<E> entrySelection) {
		this(entrySelection, new HashMap<>(), new HashSet<>(), new HashSet<>(2));
	}

	private SelectionCommandGroup(Enum<E> entrySelection, HashMap<Enum<E>, Command> selectionMap, Set<SubsystemInterface> requirements, Set<OpModeEX.OpModeEXRunStates> runStates) {
		this.entrySelection = entrySelection;
		this.selectionMap = selectionMap;
		this.requiredSubsystems = requirements;
		this.runStates = runStates;
	}

	public SelectionCommandGroup<E> addSelection(Enum<E> selection, Command toRun) {
		if (Scheduler.getSchedulerInstance().isScheduled(this))
			throw new IllegalStateException("Commands cannot be added to a composition while it's running");
		HashMap<Enum<E>, Command> selectionMap = new HashMap<>(this.selectionMap);
		selectionMap.put(selection, toRun);

		Set<SubsystemInterface> newRequirementSet = new HashSet<>(this.getRequiredSubsystems());

		HashSet<OpModeEX.OpModeEXRunStates> newRunStates = new HashSet<>(2);

		newRequirementSet.addAll(toRun.getRequiredSubsystems());
		newRunStates.addAll(toRun.getRunStates());

		Scheduler.getSchedulerInstance().registerComposedCommands(toRun);
		return new SelectionCommandGroup<>(this.selection, selectionMap, newRequirementSet, newRunStates);
	}

	public void queue(Enum<E> selection) {
		Command.super.queue();
		switchSelection = this.selection != selection;
		this.selection = selection;
	}

	@Override
	public void initialise() {
		this.selection = entrySelection;
	}

	@Override
	public void execute() {
		if (switchSelection) {
			if (currentCommand != null) currentCommand.end(true);
			currentCommand = selectionMap.get(selection);
			if (currentCommand == null) return;
			currentCommand.initialise();
			switchSelection = false;
		}
		if (currentCommand == null) return;
		currentCommand.execute();
		if (currentCommand.finished()) {
			currentCommand.end(false);
			currentCommand = null;
		}
	}

	@Override
	public void end(boolean interrupted) {
		if (currentCommand != null) currentCommand.end(interrupted);
	}

	@Override
	public boolean finished() {
		return selection == null;
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
	public boolean interruptable() {
		return currentCommand == null || currentCommand.interruptable();
	}

	@Override
	public void queue() {
		queue(entrySelection);
	}
}
