---
description: >-
  OpModeEX is the mercurialftc wrapper over the traditional FTCRC OpMode.
  OpModeEX has some extra features and manages a bunch of behaviour in the
  background.
---

# OpModeEX

## Structure

### registerSubsystems()

```java
/**
 * called before {@link #initEX()}, solely for initialising all subsystems, ensures that they are registered with the correct {@link com.mercurialftc.mercurialftc.scheduler.Scheduler}, and that their init methods will be run
 */
@Override
public void registerSubsystems() {

}
```

registerSubsystems() allows you to construct each of your subsystems, assigning values to the fields that need to be made in the OpModeEX class. After this method is run, the OpModeEX will automatically call .init() on each subsystem that was constructed, and initialise them in the order they are constructed in.

### initEX()

```java
/**
 * should contain your regular init code
 */
@Override
public void initEX() {

}
```

initEX() should contain any additional initialisation code that needs to be run, after the subsystems are all initialised. This might include additional configuration, the generation of paths, or similar.

### registerBindings()

```java
/**
 * registers bindings after the subsystem and regular init code,
 * useful for organisation of your OpModeEX, but functionally no different to initialising them at the end of {@link #initEX()}
 */
@Override
public void registerBindings() {

}
```

registerBindings() functionally is equivalent to initEX(), but allows for a easier to read structure when writing an OpModeEX. Bindings are declared once, and from then on are managed by the scheduler. This makes it easy to read and write the declaration of which gamepad inputs are bound to which commands.

### init\_loopEX()

```java
@Override
public void init_loopEX() {

}
```

Works like a standard init\_loop(), also manages commands in the background.

### startEX()

```java
@Override
public void startEX() {

}
```

Works like a standard start(), also resets the elapsedTime available on the OpModeEX

### loopEX()

```java
@Override
public void loopEX() {

}
```

Works like a standard loop(), also manages commands in the background.
