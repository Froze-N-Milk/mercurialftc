---
description: >-
  The logging utility makes it easy to durably store information while the robot
  is running.
---

# Log

A Log can be used in a subsystem or OpModeEX to record a csv of information to the ControlHub's file system.

The Log will automatically handle removing older records and can be disabled across the board using the [durable-settings-manager.md](durable-settings-manager.md "mention"), for when you want to disable its processes.

Construct the log during init, store it as a field, write to it during periodic using `.logData()`, at the end of periodic call `.updateLoop(storeTime)` to write that loop to the file and put `.close()` in the close section of the Subsystem

A Log can can be constructed by giving it headings you'll refer back to later, which become column names

```java
Log myLog = new Log("MySubsystem", "Position", "Error", "Velocity");
```

```java
@Override
public void periodic() {
    myLog.logData("Position", getPosition());
    myLog.logData("Error", getError());
    myLog.logData("Velocity", getVelocity());
    
    // records the time if true
    myLog.updateLoop(true);
}
```

```java
@Override
public void close() {
    myLog.close();
}
```

The results can be found in the control hub's file system at `SD_CARD/FIRST/mercurialftc/logs/` where a directory will be made in the name of the system being logged, as passed to the Log constructor.

The newest Log will have the number 0, the oldest will have the largest number.&#x20;

This system is very similar to the FTCRC's telemetry.
