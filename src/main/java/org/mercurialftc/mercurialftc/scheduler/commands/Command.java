package org.mercurialftc.mercurialftc.scheduler.commands;

import org.mercurialftc.mercurialftc.scheduler.Scheduler;
import org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The low-level command abstract class to use
 */
public abstract class Command implements CommandSignature {
	public void queue() {
		Scheduler.getSchedulerInstance().scheduleCommand(this);
	}
}
