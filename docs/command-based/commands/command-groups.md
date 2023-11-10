---
description: Command groups can be used to join multiple commands together in a group.
---

# Command Groups

MercurialFTC has fewer command group options than WPIlib, but offers ParallelCommandGroup and SequentialCommandGroup.

They work like LambdaCommand, but are far simpler, with only one unique method.

`addCommands()` is a non-mutating methods allows you to add any number of commands to the group.

Command groups are still commands under the hood, and can be used anywhere that another command would be used.

ParallelCommandGroup runs its commands at the same time, in 'parallel' (there is no actual parallelism)

SequentialCommandGroup runs its commands one after another, in sequence
