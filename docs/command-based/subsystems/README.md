---
description: >-
  Subsystems are classes that group and handle hardware devices, and reflect the
  physical structure of your robot.
---

# Subsystems

## Structure

### Constructor

```java
public <ClassName>(OpModeEX opModeEX) {
    super(opModeEX);
}
```

Every subsystem needs a constructor with the following minimum information, it is recommended that teams add additional parameters to the constructor in order to do dependency injection to add extra information to the subsystem. The OpModeEX passed into the subsystem can be used to access the HardwareMap and the Telemetry from the OpMode. Under the hood, this registers this subsystem against the scheduler

### init()

```java
@Override
public void init() {

}
```

The init method should contain the majority of the init code. This method is automatically run by the OpModeEX, and it allows the OpModeEX to keep track of the initialisation status of each subsystem.

This should include hardware map initialisations which look like:

```java
motor1 = opModeEX.hardwareMap.get(DcMotorEx.class, "motor1");
```

### periodic()

```java
@Override
public void periodic() {

}
```

The periodic gets run every loop, no matter what, so you should put any common telemetry outputs here, and any sensor reads that need to happen here. Mercurial takes care of bulk caching, so no need to worry about implementing it.

### defaultCommandExecute()

```java
@Override
public void defaultCommandExecute() {

}
```

Each subsystem has a predefined default command, with some sensible assumptions about its run conditions:&#x20;

* Won't end unless another command is queued that will interrupt it.
* Will automatically start running if no other commands are using that subsystem
* Will only run in the LOOP RunState by default.
* Requires the subsystem.
* Has no [#initialise](../commands/#initialise "mention") or [#end-interrupted](../commands/#end-interrupted "mention") method

This method becomes the execute component of that default command, and should contain the default behaviour of the system, like manipulating hardware.

The default configuration of the default command can be overwritten, see [advanced-subsystem-configuration.md](advanced-subsystem-configuration.md "mention")

### close()

```java
@Override
public void close() {

}
```

close() gets run for each subsystem when the OpModeEX ends. Most of the time you don't need to put anything in here, but you might close an open stream, like if you're using a logging utility (like [log.md](../../utilities/log.md "mention")) or similar. The vast majority of the time this can be left empty.

## Additional Methods

### getDefaultCommand()

Returns the default command of the subsystem, it is unlikely that you will need to call this. See [[advanced-subsystem-configuration.md](advanced-subsystem-configuration.md "mention")](advanced-subsystem-configuration.md#getdefaultcommand) for more information about overriding this.

### isBusy()

Returns true this subsystem is currently being required by a command other than its default command.
