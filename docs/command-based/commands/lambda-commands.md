---
description: >-
  LambdaCommand is a builder class that allows you to write Commands without
  creating a whole new class.
---

# Lambda Commands

## Advantages

LambdaCommands are super easy to read, and mean that your project doesn't get bogged down with lots of extra command files, especially when you need to write short commands.

LambdaCommands also make some default assumptions which make them safe to use even when you have not modified them at all, pick and choose which defaults to override in order to fill out the structure of the command.

## Defaults

By default, a new LambdaCommand will:

* has no requirements
* do nothing in init
* do nothing during execute
* do nothing in end
* instantly finish
* runs only in the LOOP RunState
* is interruptible

## Methods

LambdaCommands offer a series of methods to build upon the existing defaults.

Each of the methods are non-mutating, which means that they return a totally new LambdaCommand, without modifying the original.

Methods that start with 'set' will overwrite the previous value, and should be used most of the time.

Additionally, methods that start with 'add' will build upon pre-exisiting values. These methods do not exist for all fields. For example, .addInit() allows you to add another method to the pre-exisiting init method on the command.

These methods are well documented using javadoc and obvious to use, you can explore them in android studio by making a new LambdaCommand:

```java
new LambdaCommand();
```

and then pressing 'dot' (.) to see the methods that can be called on it.

## Converting a Command to a LambdaCommand

If you want to make small, in-place changes to a Command, you can compose a LambdaCommand from any Command:

```java
Command myCommand = new MyCommand();

LambdaCommand composedFrom = LambdaCommand.from(myCommand);
```

If myCommand was made by implementing the Command interface, a new LambdaCommand representation of myCommand will be made and given back to you.

If myCommand was made using a LambdaCommand, it will be cast and returned back to you.

This means we can safely and efficiently get a LambdaCommand from any Command.

Using the LambdaCommand builder methods won't change myCommand, as they are non-mutating.
