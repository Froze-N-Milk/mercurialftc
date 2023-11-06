---
description: >-
  Commands provide a structure for the running of methods to control subsystem
  behaviour
---

# Commands

## Structure

### initialise()

```java
@Override
public void initialise() {

}
```

This method gets run once when the command is first successfully queued.

### execute()

```java
@Override
public void execute() {

}
```

This method gets run over and over again until the command finishes, see [#finished](./#finished "mention")

### finished()

```java
@Override
public boolean finished() {
    return finishedCondition;
}
```

a command will finish when this method returns true, the RunState is no longer allowed, or the command is [#interruptable](./#interruptable "mention") and another command requiring any of the same subsystems gets queued.

### end(interrupted)

```java
@Override
public void end(boolean interrupted) {

}
```

Gets run once when the command finishes as per [#finished](./#finished "mention").

The boolean interrupted will be true if the command was interrupted by another command requiring any of the same subsystems being queued was the cause of this command's finishing.&#x20;

### getRequiredSubsystems()

```java
@Override
public Set<SubsystemInterface> getRequiredSubsystems() {
   return requirementsSet;
}
```

Returns a set of the required subsystems. Should not construct a new set, instead should probably return a field.&#x20;

Required subsystems should be passed in using the dependency injection pattern to the constructor.

### getRunStates()

```java
@Override
public Set<OpModeEX.OpModeEXRunStates> getRunStates() {
	return runStatesSet;
}
```

Returns a set of the allowed RunStates. Should not construct a new set, instead should probably return a field.&#x20;

The allowed RunStates are:

* `OpModeEX.OpModeEXRunStates.INIT_LOOP`
* `OpModeEX.OpModeEXRunStates.LOOP`

By default most commands should only be allowed to run in LOOP, to prevent accidental motion before the OpMode is running.

A command will finish if the current RunState of the robot is not included in the set of allowed RunStates returned by a command.&#x20;

### interruptible()

```java
@Override
public boolean interruptible() {
	return true;
}
```

Return if this command is allowed to be interrupted by another command, should be true in most cases.

## Additional Methods

### queue()

Attempts to queue the command against the scheduler.&#x20;

This method may be overwritten if you wish to add on queue behaviour, but should still include a call to `super.queue();`

This will occasionally need to be called on a command if that command is not being passed to a binding, most commonly this is on wave following or one-off start commands.
