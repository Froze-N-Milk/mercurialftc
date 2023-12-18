package dev.frozenmilk.mercurial.bindings

import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.commands.Command
import java.util.function.Supplier

class Binding(val activationCondition: Supplier<Boolean>, val toRun: Command) {
	init {
		Mercurial.registerBinding(this)
	}
}