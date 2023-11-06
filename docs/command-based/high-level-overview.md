---
description: >-
  A high level overview of the terminology involved in the mercurialftc
  implementation of the command based paradigm
---

# High Level Overview

## Subsystems, Commands, Bindings

Command based revolves around the concept of writing well structured code with a focus on OOP principles to control your robot, which takes the form of three components.

Classes extend `Subsystem` to abstract away interfacing with hardware objects, and provide mid to high level public methods to control the behaviour of the subsystem.

Commands are state machines that take control of one or more subsystems in order to issue calls to the public methods of said subsystems. They work with a scheduler in the background to help ensure that no two commands manipulate a subsystem at the same time.

Bindings bind advanced boolean conditions overseen by the scheduler to run commands.

These concepts combine together to ensure that procedure is only written once in a reusable fashion, and OpModes are written in an easy to read declarative manner that makes it easy to quickly switch how the robot is controlled.

## OpModeEX

mercurialftc uses an OpMode wrapper to handle the behaviour of each of these classes.
