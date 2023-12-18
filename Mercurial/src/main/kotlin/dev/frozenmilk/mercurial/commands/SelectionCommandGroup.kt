package dev.frozenmilk.mercurial.commands

import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.RunStates
import dev.frozenmilk.mercurial.subsystems.Subsystem

class SelectionCommandGroup<E : Enum<E>> private constructor(private val entrySelection: Enum<E>?, private val selectionMap: HashMap<Enum<E>, Command>, requirements: Set<Subsystem>, runStates: Set<RunStates>) : Command {
	override val requiredSubsystems: Set<Subsystem>
	override val runStates: Set<RunStates>
	private var selection: Enum<E>? = null
	private var currentCommand: Command? = null
	private var switchSelection = false

	constructor(entrySelection: Enum<E>?) : this(entrySelection, HashMap<Enum<E>, Command>(), HashSet<Subsystem>(), HashSet<RunStates>(2))

	init {
		requiredSubsystems = requirements
		this.runStates = runStates
	}

	fun addSelection(selection: Enum<E>?, toRun: Command): SelectionCommandGroup<E> {
		check(!Mercurial.isScheduled(this)) { "Commands cannot be added to a composition while it's running" }
		val selectionMap = HashMap(selectionMap)
		selectionMap[selection] = toRun
		val newRequirementSet = HashSet(requiredSubsystems)
		val newRunStates = HashSet<RunStates>(2)
		newRequirementSet.addAll(toRun.requiredSubsystems)
		newRunStates.addAll(toRun.runStates)
		Mercurial.registerComposedCommands(listOf(toRun))
		return SelectionCommandGroup(this.selection, selectionMap, newRequirementSet, newRunStates)
	}

	fun schedule(selection: Enum<E>? = entrySelection) {
		super.schedule()
		switchSelection = this.selection !== selection
		this.selection = selection
	}

	override fun initialise() {
		selection = entrySelection
	}

	override fun execute() {
		if (switchSelection) {
			if (currentCommand != null) currentCommand!!.end(true)
			currentCommand = selectionMap[selection]
			if (currentCommand == null) return
			currentCommand!!.initialise()
			switchSelection = false
		}
		if (currentCommand == null) return
		currentCommand!!.execute()
		if (currentCommand!!.finished()) {
			currentCommand!!.end(false)
			currentCommand = null
		}
	}

	override fun end(interrupted: Boolean) {
		if (currentCommand != null) currentCommand!!.end(interrupted)
	}

	override fun finished(): Boolean {
		return selection == null
	}

	override val interruptible: Boolean
		get() {
			return currentCommand == null || currentCommand!!.interruptible
		}
}
