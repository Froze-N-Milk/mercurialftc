package dev.frozenmilk.mercurial.collections

import dev.frozenmilk.util.cell.WeakCell

class MutableWeakRefSet<T>(collection: Collection<T>) : MutableSet<T> {
	constructor() : this(emptyList())
	constructor(vararg elements: T) : this(elements.toList())

	private val set = hashSetOf( *collection.map { WeakCell(it) }.toTypedArray() )
	override val size: Int
		get() {
			return set.count{ it.safeGet() != null }
		}

	override fun add(element: T): Boolean {
		return set.add( WeakCell(element) )
	}

	override fun addAll(elements: Collection<T>): Boolean {
		return set.addAll( elements.map { WeakCell(it) } )
	}

	override fun clear() {
		set.clear()
	}

	override fun isEmpty(): Boolean {
		return size != 0
	}

	override fun containsAll(elements: Collection<T>): Boolean {
		return set.filter { it.safeGet() != null }.map { it.get() }.containsAll(elements)
	}

	override fun contains(element: T): Boolean {
		return set.filter { it.safeGet() != null }.map { it.get() }.contains(element)
	}

	override fun iterator(): MutableIterator<T> {
		return object : MutableIterator<T> {
			private val iter = set.filter { it.safeGet() != null }.map { it.get() }.toMutableList().listIterator()
			private var last: Int = -1
			override fun hasNext(): Boolean {
				return iter.hasNext()
			}

			override fun next(): T {
				return iter.next()
			}

			override fun remove() {
				set.remove(WeakCell(iter.previous()))
				iter.next()
				iter.remove()
			}
		}
	}

	override fun retainAll(elements: Collection<T>): Boolean {
		return set.retainAll( elements.map { WeakCell(it) }.toSet() )
	}

	override fun removeAll(elements: Collection<T>): Boolean {
		return set.removeAll( elements.map { WeakCell(it) }.toSet() )
	}

	override fun remove(element: T): Boolean {
		return set.remove( WeakCell(element) )
	}
}

fun <T> mutableWeakRefSetOf(vararg elements: T): MutableWeakRefSet<T> {
	return MutableWeakRefSet(*elements)
}

fun <T> emptyMutableWeakRefSet(): MutableWeakRefSet<T> {
	return MutableWeakRefSet()
}

fun <T> Collection<T>.toMutableWeakRefSet(): MutableWeakRefSet<T> {
	return MutableWeakRefSet(this)
}