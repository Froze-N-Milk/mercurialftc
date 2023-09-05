package org.mercurialftc.mercurialftc.scheduler.commands;

import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.Set;

public abstract class CommandGroup extends Command {
	public CommandGroup(Set<SubsystemInterface> requiredSubsystems) {
		super(requiredSubsystems);
	}
	
	@Override
	public boolean isOverrideAllowed() {
		return false;
	}
	
	@Override
	public void initialise() {
	
	}
	
	@Override
	public void execute() {
	
	}
	
	@Override
	public void end() {
	
	}
	
	@Override
	public boolean isFinished() {
		return false;
	}
}
