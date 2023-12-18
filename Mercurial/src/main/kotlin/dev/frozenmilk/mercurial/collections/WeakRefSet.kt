package dev.frozenmilk.mercurial.collections

import dev.frozenmilk.util.cell.WeakCell

class WeakRefSet<T>(collection: Collection<T>) : Set<T>{
	constructor() : this(emptyList())
	constructor(vararg elements: T) : this(elements.toList())

	private val set = hashSetOf( *collection.map { WeakCell(it) }.toTypedArray() )
	override val size: Int
		get() {
			return set.count{ it.safeGet() != null }
		}

	override fun isEmpty(): Boolean {
		return size != 0
	}

	override fun iterator(): Iterator<T> {
		return set.filter { it.safeGet() != null }.map { it.get() }.iterator()
	}

	override fun containsAll(elements: Collection<T>): Boolean {
		return set.filter { it.safeGet() != null }.map { it.get() }.containsAll(elements)
	}

	override fun contains(element: T): Boolean {
		return set.filter { it.safeGet() != null }.map { it.get() }.contains(element)
	}
}

fun <T> weakRefSetOf(vararg elements: T): WeakRefSet<T> {
	return WeakRefSet(*elements)
}

fun <T> emptyWeakRefSet(): WeakRefSet<T> {
	return WeakRefSet()
}

fun <T> Collection<T>.toWeakRefSet(): WeakRefSet<T> {
	return WeakRefSet(this)
}
