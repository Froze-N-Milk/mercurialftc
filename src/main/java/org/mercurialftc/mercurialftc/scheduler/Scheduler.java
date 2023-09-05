package org.mercurialftc.mercurialftc.scheduler;

import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;
import org.mercurialftc.mercurialftc.scheduler.triggers.Trigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class Scheduler {
	public static Scheduler scheduler;
	
	public static boolean refreshScheduler = true;
	
	public LinkedHashSet<SubsystemInterface> getSubsystems() {
		return subsystems;
	}
	
	public LinkedHashSet<Trigger> getTriggers() {
		return triggers;
	}
	
	public LinkedHashSet<Command> getCommands() {
		return commands;
	}
	
	private final LinkedHashSet<SubsystemInterface> subsystems; // currently registered Subsystems
	private final LinkedHashSet<Trigger> triggers;
	private final LinkedHashSet<Command> commands; // currently scheduled Commands
	private final LinkedHashMap<SubsystemInterface, Command> requirements; // the mapping of required Subsystems to commands
	private ArrayList<SubsystemInterface> storedSubsystems;
	
	private Scheduler() {
		this.subsystems = new LinkedHashSet<>();
		this.commands = new LinkedHashSet<>();
		this.requirements = new LinkedHashMap<>();
		this.triggers = new LinkedHashSet<>();
		this.storedSubsystems = new ArrayList<>();
	}
	
	/**
	 * A safe method of accessing the scheduler singleton, if it has not been generated, the generation will be run.
	 *
	 * @return safe return of a non null scheduler instance
	 */
	public static Scheduler getSchedulerInstance() {
		if (scheduler == null) {
			scheduler = new Scheduler();
		}
		return scheduler;
	}
	
	public static Scheduler freshInstance() {
		if (scheduler != null) {
			for (Command command : scheduler.commands) {
				scheduler.cancelCommand(command);
			}
			for (SubsystemInterface subsystem : scheduler.subsystems) {
				scheduler.subsystems.remove(subsystem);
			}
			for (Trigger trigger : scheduler.triggers) {
				scheduler.deregisterTrigger(trigger);
			}
		}
		
		scheduler = new Scheduler();
		return scheduler;
	}
	
	public void registerSubsystem(SubsystemInterface subsystem) {
		this.subsystems.add(subsystem);
	}
	
	public void pollSubsystemsPeriodic() {
		for (SubsystemInterface subsystem : subsystems) {
			subsystem.periodic();
		}
	}
	
	public boolean registerCommand(Command command) {
		Set<SubsystemInterface> commandRequirements = command.getRequiredSubsystems();
		
		// if the subsystems required by the command are not required, register it
		if (Collections.disjoint(commandRequirements, requirements.keySet())) {
			initialiseCommand(command, commandRequirements);
			return true;
		} else {
			// for each subsystem required, check the command currently requiring it, and make sure that they can all be overwritten
			for (SubsystemInterface subsystem : commandRequirements) {
				Command requirer = requirements.get(subsystem);
				if (requirer != null && !requirer.isOverrideAllowed()) {
					return false;
				}
			}
		}
		
		// cancel all required commands
		for (SubsystemInterface subsystem : commandRequirements) {
			Command requirer = requirements.get(subsystem);
			if (requirer != null) {
				cancelCommand(requirer);
			}
		}
		
		initialiseCommand(command, commandRequirements);
		return true;
	}
	
	private void cancelCommand(Command command) {
		command.end();
		commands.remove(command);
		for (SubsystemInterface requirement : command.getRequiredSubsystems()) {
			requirements.remove(requirement, command);
		}
	}
	
	private void initialiseCommand(Command command, Set<SubsystemInterface> commandRequirements) {
		commands.add(command);
		for (SubsystemInterface requirement : commandRequirements) {
			requirements.put(requirement, command);
		}
		command.initialise();
	}
	
	public void pollCommands() {
		for (Command command : commands) {
			if (command.isFinished()) {
				cancelCommand(command);
			} else {
				// runs the commands
				command.execute();
			}
		}
		
		// checks if any subsystems are not being used by any commands, if so, initialises the default command for that subsystem
		for (SubsystemInterface subsystem : subsystems) {
			if (!requirements.containsKey(subsystem)) {
				registerCommand(subsystem.getDefaultCommand());
			}
		}
	}
	
	public void registerTrigger(Trigger trigger) {
		triggers.add(trigger);
	}
	
	public void deregisterTrigger(Trigger trigger) {
		triggers.remove(trigger);
	}
	
	public void pollTriggers() {
		for (Trigger trigger : triggers) {
			trigger.poll();
		}
	}
	
	
	public void storeSubsystem(SubsystemInterface subsystem) {
		storedSubsystems.add(subsystem);
	}
	
	public SubsystemInterface getStoredSubsystem(int index) {
		return storedSubsystems.get(index);
	}
}
