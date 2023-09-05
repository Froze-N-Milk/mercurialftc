package org.mercurialftc.mercurialftc.scheduler.commands;

import org.mercurialftc.mercurialftc.scheduler.commands.interfaces.CommandEnd;
import org.mercurialftc.mercurialftc.scheduler.commands.interfaces.CommandFinish;
import org.mercurialftc.mercurialftc.scheduler.commands.interfaces.CommandInit;
import org.mercurialftc.mercurialftc.scheduler.commands.interfaces.CommandMethod;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * todo fill in
 */
public class LambdaCommand extends Command {
	private final CommandInit commandInit;
	private final CommandMethod commandMethod;
	private final CommandFinish commandFinish;
	private final CommandEnd commandEnd;
	private final boolean isOverrideAllowed;
	
	/**
	 * constructs a default lambda command
	 */
	public LambdaCommand() {
		this(
				new HashSet<>(),
				() -> {},
				() -> {},
				() -> true,
				() -> {},
				true
		);
	}
	
	private LambdaCommand(
			Set<SubsystemInterface> requiredSubsystems,
			CommandInit commandInit,
			CommandMethod commandMethod,
			CommandFinish commandFinish,
			CommandEnd commandEnd,
			boolean isOverrideAllowed
	) {
		super(requiredSubsystems);
		this.commandInit = commandInit;
		this.commandMethod = commandMethod;
		this.commandFinish = commandFinish;
		this.commandEnd = commandEnd;
		this.isOverrideAllowed = isOverrideAllowed;
	}
	
	public LambdaCommand addRequirements(SubsystemInterface... requiredSubsystems) {
		Set<SubsystemInterface> requirements = this.getRequiredSubsystems();
		Collections.addAll(requirements, requiredSubsystems);
		
		return new LambdaCommand(
				requirements,
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.isOverrideAllowed
		);
	}
	
	public LambdaCommand init(CommandInit initialise) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				initialise,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				this.isOverrideAllowed
		);
	}
	
	public LambdaCommand execute(CommandMethod execute) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				this.commandInit,
				execute,
				this.commandFinish,
				this.commandEnd,
				this.isOverrideAllowed
		);
	}
	
	public LambdaCommand finish(CommandFinish finish) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				this.commandInit,
				this.commandMethod,
				finish,
				this.commandEnd,
				this.isOverrideAllowed
		);
	}
	
	public LambdaCommand end(CommandEnd end) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				end,
				this.isOverrideAllowed
		);
	}
	
	public LambdaCommand isOverrideAllowed(boolean isOverrideAllowed) {
		return new LambdaCommand(
				this.getRequiredSubsystems(),
				this.commandInit,
				this.commandMethod,
				this.commandFinish,
				this.commandEnd,
				isOverrideAllowed
		);
	}
	
	// Wrapper methods:
	@Override
	public final void initialise() {
		commandInit.initialise();
	}
	
	@Override
	public final void execute() {
		commandMethod.execute();
	}
	
	@Override
	public final boolean isFinished() {
		return commandFinish.isFinished();
	}
	
	@Override
	public void end() {
		commandEnd.end();
	}
	
	@Override
	public final boolean isOverrideAllowed() {
		return isOverrideAllowed;
	}
}
