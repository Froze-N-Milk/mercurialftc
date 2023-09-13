package org.mercurialftc.mercurialftc.scheduler;

import android.os.Environment;
import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.scheduler.commands.CommandSignature;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;
import org.mercurialftc.mercurialftc.scheduler.triggers.Trigger;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.*;
import java.util.*;

public class Scheduler {
	public static Scheduler scheduler;

	private static boolean schedulerRefreshed, loggingEnabled;
	private final LinkedHashSet<SubsystemInterface> subsystems; // currently registered Subsystems
	private final LinkedHashSet<Trigger> triggers;

	private final Set<CommandSignature> composedCommands = Collections.newSetFromMap(new WeakHashMap<>());
	private final LinkedHashSet<CommandSignature> commands; // currently scheduled Commands
	private final ArrayList<CommandSignature> commandsToCancel; // commands to be cancelled this loop
	private final LinkedHashSet<CommandSignature> commandsToSchedule; // commands to be scheduled this loop;

	private final LinkedHashMap<SubsystemInterface, CommandSignature> requirements; // the mapping of required Subsystems to commands
	private final HashMap<String, SubsystemInterface> storedSubsystems;

	private Scheduler() {
		this.subsystems = new LinkedHashSet<>();
		this.commands = new LinkedHashSet<>();
		this.commandsToCancel = new ArrayList<>();
		this.commandsToSchedule = new LinkedHashSet<>();
		this.requirements = new LinkedHashMap<>();
		this.triggers = new LinkedHashSet<>();
		this.storedSubsystems = new HashMap<>();

		try {
			interpretConfigFiles();
		} catch (IOException e) {
			throw new RuntimeException("Error creating/reading scheduler config:\n" + e);
		}
	}

	/**
	 * A safe method of accessing the scheduler singleton, if it has not been generated, the generation will be run.
	 *
	 * @return safe return of a non-null scheduler instance
	 */
	public static Scheduler getSchedulerInstance() {
		if (scheduler == null) {
			scheduler = new Scheduler();
		}
		return scheduler;
	}

	public static Scheduler freshInstance() {
		return scheduler = new Scheduler();
	}

	public static void interpretConfigFiles() throws IOException {
		String directoryPath = Environment.getExternalStorageDirectory().getPath() + "/FIRST/mercurialftc/";
		File directory = new File(directoryPath);
		File configFile = new File(directoryPath, "config.toml");
		if (directory.mkdirs() || directory.isDirectory()) {
			if (!(configFile.isFile())) {
				if (configFile.createNewFile()) {
					FileWriter writer = new FileWriter(configFile);
					writer.write("[configOptions]\n");
					writer.write(ConfigOptions.SCHEDULER_REFRESHED.getOption() + " = true\n");
					writer.write(ConfigOptions.ENABLE_LOGGING.getOption() + " = false\n");
					writer.close();
				} else {
					throw new IOException();
				}
			}
			TomlParseResult config = Toml.parse(new FileReader(configFile));
			schedulerRefreshed = Boolean.TRUE.equals(config.getBoolean(ConfigOptions.SCHEDULER_REFRESHED.getOption()));
			loggingEnabled = Boolean.TRUE.equals(config.getBoolean(ConfigOptions.ENABLE_LOGGING.getOption()));
		} else {
			schedulerRefreshed = true;
			loggingEnabled = false;
		}
	}

	public static TomlParseResult getConfig() {
		// all the required checks to ensure this exists have already been done by the scheduler
		String directoryPath = Environment.getExternalStorageDirectory().getPath() + "/FIRST/mercurialftc/";
		File configFile = new File(directoryPath, "config.toml");
		try {
			return Toml.parse(new FileReader(configFile));
		} catch (IOException e) {
			throw new RuntimeException("Error reading scheduler config:\n" + e);
		}
	}

	/**
	 * inverts the boolean found at selection
	 */
	public static void setBooleanConfigOption(int selection, boolean newValue) throws RuntimeException {
		// all the required checks to ensure this exists have already been done by interpretConfigFiles()

		Set<Map.Entry<String, Object>> configSettings = getConfig().getTableOrEmpty("configOptions").dottedEntrySet(true);
		String directoryPath = Environment.getExternalStorageDirectory().getPath() + "/FIRST/mercurialftc/";
		File configFile = new File(directoryPath, "config.toml");
		try {
			FileWriter writer = new FileWriter(configFile);
			StringBuilder builder = new StringBuilder();
			Iterator<Map.Entry<String, Object>> settingsIterator = configSettings.iterator();
			for (int i = 0; i < configSettings.size(); i++) {
				Map.Entry<String, Object> entry = settingsIterator.next();
				builder.append(entry.getKey()).append(" = ");
				if (selection == i) {
					builder.append(newValue);
				} else {
					builder.append("\"").append(entry.getValue()).append("\"");
				}
				builder.append("\n");
			}
			writer.write(builder.toString());
			writer.close();
			Scheduler.interpretConfigFiles();
		} catch (IOException e) {
			throw new RuntimeException("Error writing to the scheduler config:\n" + e);
		}
	}

	public static void setBooleanConfigOption(String selection, boolean newValue) throws RuntimeException {
		// all the required checks to ensure this exists have already been done by interpretConfigFiles()

		Set<Map.Entry<String, Object>> configSettings = getConfig().getTableOrEmpty("configOptions").dottedEntrySet(true);
		String directoryPath = Environment.getExternalStorageDirectory().getPath() + "/FIRST/mercurialftc/";
		File configFile = new File(directoryPath, "config.toml");
		try {
			FileWriter writer = new FileWriter(configFile);
			StringBuilder builder = new StringBuilder();
			Iterator<Map.Entry<String, Object>> settingsIterator = configSettings.iterator();
			for (int i = 0; i < configSettings.size(); i++) {
				Map.Entry<String, Object> entry = settingsIterator.next();
				builder.append(entry.getKey()).append(" = ");
				if (Objects.equals(entry.getKey(), selection)) {
					builder.append(newValue);
				} else {
					builder.append("\"").append(entry.getValue()).append("\"");
				}
				builder.append("\n");
			}
			writer.write(builder.toString());
			writer.close();
			Scheduler.interpretConfigFiles();
		} catch (IOException e) {
			throw new RuntimeException("Error writing to the scheduler config:\n" + e);
		}
	}

	public static void setBooleanConfigOption(ConfigOptions selection, boolean newValue) throws RuntimeException {
		setBooleanConfigOption(selection.getOption(), newValue);
	}

	public static boolean isSchedulerRefreshed() {
		try {
			interpretConfigFiles();
		} catch (IOException e) {
			throw new RuntimeException("Error creating/reading scheduler config:\n" + e);
		}
		return schedulerRefreshed;
	}

	public static boolean isLoggingEnabled() {
		try {
			interpretConfigFiles();
		} catch (IOException e) {
			throw new RuntimeException("Error creating/reading scheduler config:\n" + e);
		}
		return loggingEnabled;
	}

	public LinkedHashSet<SubsystemInterface> getSubsystems() {
		return subsystems;
	}

	public LinkedHashSet<Trigger> getTriggers() {
		return triggers;
	}

	public LinkedHashSet<CommandSignature> getCommands() {
		return commands;
	}

	public void registerSubsystem(SubsystemInterface subsystem) {
		this.subsystems.add(subsystem);
	}

	public void pollSubsystemsPeriodic() {
		for (SubsystemInterface subsystem : subsystems) {
			subsystem.periodic();
		}
	}

	public void scheduleCommand(CommandSignature command) {
		commandsToSchedule.add(command);
	}

	private void cancelCommand(CommandSignature command, boolean interrupted) {
		if (command == null) return;
		if (!isScheduled(command)) return;
		command.end(interrupted);
		for (SubsystemInterface requirement : command.getRequiredSubsystems()) {
			requirements.remove(requirement, command);
		}
		commands.remove(command);
	}

	private void initialiseCommand(CommandSignature command) {
		Set<SubsystemInterface> commandRequirements = command.getRequiredSubsystems();

		// if the subsystems required by the command are not required, register it
		if (Collections.disjoint(commandRequirements, requirements.keySet())) {
			initialiseCommand(command, commandRequirements);
			return;
		} else {
			// for each subsystem required, check the command currently requiring it, and make sure that they can all be overwritten
			for (SubsystemInterface subsystem : commandRequirements) {
				CommandSignature requirer = requirements.get(subsystem);
				if (requirer != null && !requirer.interruptable()) {
					return;
				}
			}
		}

		// cancel all required commands
		for (SubsystemInterface subsystem : commandRequirements) {
			CommandSignature requirer = requirements.get(subsystem);
			if (requirer != null) {
				commandsToCancel.add(requirer);
			}
		}

		initialiseCommand(command, commandRequirements);
	}

	private void initialiseCommand(CommandSignature command, @NotNull Set<SubsystemInterface> commandRequirements) {
		if (command == null) return;
		if (isScheduled(command)) return;
		commands.add(command);
		for (SubsystemInterface requirement : commandRequirements) {
			requirements.put(requirement, command);
		}
		command.initialise();
	}

	public void pollCommands() {
		// checks to see if any commands are finished, if so, queues them to be canceled
		for (CommandSignature command : commands) {
			if (command.finished()) {
				cancelCommand(command, false);
			}
		}

		// checks if any subsystems are not being used by any commands, if so, schedules the default command for that subsystem
		for (SubsystemInterface subsystem : subsystems) {
			if (!requirements.containsKey(subsystem)) {
				scheduleCommand(subsystem.getDefaultCommand());
			}
		}

		// initialises all the commands that are due to be scheduled
		for (CommandSignature command : commandsToSchedule) {
			initialiseCommand(command);
		}
		// empties the queue
		commandsToSchedule.clear();

		// cancels all cancel queued commands
		for (CommandSignature command : commandsToCancel) {
			cancelCommand(command, true);
		}
		// empties the queue
		commandsToCancel.clear();

		// runs the commands
		for (CommandSignature command : commands) {
			command.execute();
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

	public void storeSubsystem(String name, SubsystemInterface subsystem) {
		storedSubsystems.put(name, subsystem);
	}

	public SubsystemInterface getStoredSubsystem(String name) {
		return storedSubsystems.get(name);
	}

	/**
	 * Checks to see if a subsystem has a non-default command running
	 *
	 * @param subsystem the subsystem to check
	 * @return true if it isn't running its default command
	 */
	public boolean isBusy(SubsystemInterface subsystem) {
		return requirements.containsKey(subsystem) && !requirements.containsValue(subsystem.getDefaultCommand());
	}

	/**
	 * Register commands as composed. An exception will be thrown if these commands are scheduled
	 * directly or added to a composition.
	 *
	 * @param commands the commands to register
	 * @throws IllegalArgumentException if the given commands have already been composed.
	 */
	public void registerComposedCommands(CommandSignature... commands) {
		Set<CommandSignature> commandSet = new HashSet<>(Arrays.asList(commands));
		requireNotComposed(commandSet);
		composedCommands.addAll(commandSet);
	}

	/**
	 * Register commands as composed. An exception will be thrown if these commands are scheduled
	 * directly or added to a composition.
	 *
	 * @param commands the commands to register
	 * @throws IllegalArgumentException if the given commands have already been composed.
	 */
	public void registerComposedCommands(ArrayList<CommandSignature> commands) {
		Set<CommandSignature> commandSet = new HashSet<>(commands);
		requireNotComposed(commandSet);
		composedCommands.addAll(commandSet);
	}

	/**
	 * Checks to see if a command is scheduled
	 *
	 * @param command the command to check
	 * @return true if it is scheduled
	 */
	public boolean isScheduled(CommandSignature command) {
		return commands.contains(command);
	}

	/**
	 * Requires that the specified command hasn't been already added to a composition.
	 *
	 * @param command The command to check
	 * @throws IllegalArgumentException if the given commands have already been composed.
	 */
	public void requireNotComposed(CommandSignature command) {
		if (composedCommands.contains(command)) {
			throw new IllegalArgumentException(
					"Commands that have been composed may not be added to another composition or scheduled "
							+ "individually!");
		}
	}

	/**
	 * Requires that the specified commands not have been already added to a composition.
	 *
	 * @param commands The commands to check
	 * @throws IllegalArgumentException if the given commands have already been composed.
	 */
	public void requireNotComposed(Collection<CommandSignature> commands) {
		if (!Collections.disjoint(commands, getComposedCommands())) {
			throw new IllegalArgumentException(
					"Commands that have been composed may not be added to another composition or scheduled "
							+ "individually!");
		}
	}

	/**
	 * Check if the given command has been composed.
	 *
	 * @param command The command to check
	 * @return true if composed
	 */
	public boolean isComposed(CommandSignature command) {
		return getComposedCommands().contains(command);
	}

	public Set<CommandSignature> getComposedCommands() {
		return composedCommands;
	}

	public enum ConfigOptions {
		SCHEDULER_REFRESHED("schedulerRefreshed"),
		ENABLE_LOGGING("enableLogging");

		private final String option;

		private ConfigOptions(String option) {
			this.option = option;
		}

		public String getOption() {
			return option;
		}

	}

}
