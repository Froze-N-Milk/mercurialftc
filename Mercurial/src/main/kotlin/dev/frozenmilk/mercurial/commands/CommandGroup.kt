package dev.frozenmilk.mercurial.commands

import java.util.Arrays

interface CommandGroup : Command {
	fun addCommands(vararg commands: Command): CommandGroup {
		return addCommands(listOf(*commands))
	}

	fun addCommands(commands: Collection<Command>): CommandGroup
}
