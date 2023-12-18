package dev.frozenmilk.mercurial.bindings

import dev.frozenmilk.dairy.calcified.gamepad.EnhancedBooleanSupplier
import dev.frozenmilk.mercurial.commands.Command
import dev.frozenmilk.mercurial.commands.toLambda
import java.util.function.Supplier

class BoundBooleanSupplier(private val booleanSupplier: EnhancedBooleanSupplier) : Supplier<Boolean> by booleanSupplier {

	/**
	 * non-mutating
	 *
	 * @param debounce is applied to both the rising and falling edges
	 */
	fun debounce(debounce: Double) = BoundBooleanSupplier(booleanSupplier.debounce(debounce))

	/**
	 * non-mutating
	 *
	 * @param rising is applied to the rising edge
	 * @param falling is applied to the falling edge
	 */
	fun debounce(rising: Double, falling: Double) = BoundBooleanSupplier(booleanSupplier.debounce(rising, falling))

	/**
	 * non-mutating
	 *
	 * @param debounce is applied to the rising edge
	 */
	fun debounceRisingEdge(debounce: Double) = BoundBooleanSupplier(booleanSupplier.debounceRisingEdge(debounce))

	/**
	 * non-mutating
	 *
	 * @param debounce is applied to the falling edge
	 */
	fun debounceFallingEdge(debounce: Double) = BoundBooleanSupplier(booleanSupplier.debounceFallingEdge(debounce))

	/**
	 * non-mutating
	 *
	 * @return a new BoundBooleanSupplier that combines the two conditions
	 */
	infix fun and(booleanSupplier: Supplier<Boolean>) = BoundBooleanSupplier(this.booleanSupplier and booleanSupplier)

	/**
	 * non-mutating
	 *
	 * @return a new BoundBooleanSupplier that combines the two conditions
	 */
	infix fun or(booleanSupplier: Supplier<Boolean>) = BoundBooleanSupplier(this.booleanSupplier or booleanSupplier)

	/**
	 * non-mutating
	 *
	 * @return a new BoundBooleanSupplier that combines the two conditions
	 */
	infix fun xor(booleanSupplier: Supplier<Boolean>) = BoundBooleanSupplier(this.booleanSupplier xor booleanSupplier)

	/**
	 * non-mutating
	 *
	 * @return a new BoundBooleanSupplier that has the inverse of this
	 */
	operator fun not() = BoundBooleanSupplier(booleanSupplier.not())

	/**
	 * registers [toRun] to be triggered when this condition becomes true
	 */
	fun whenTrue(toRun: Command): BoundBooleanSupplier {
		Binding(booleanSupplier::whenTrue, toRun)
		return this
	}

	/**
	 * registers [toRun] to be triggered when this condition becomes false
	 */
	fun whenFalse(toRun: Command): BoundBooleanSupplier {
		Binding(booleanSupplier::whenFalse, toRun)
		return this
	}

	/**
	 * registers [toRun] to be triggered when this condition is true, and ends it early if it becomes false
	 */
	fun whileTrue(toRun: Command): BoundBooleanSupplier {
		Binding(booleanSupplier::whenTrue, toRun.toLambda().addFinish { !booleanSupplier.get() })
		return this
	}

	/**
	 * registers [toRun] to be triggered when this condition is false, and ends it early if it becomes true
	 */
	fun whileFalse(toRun: Command): BoundBooleanSupplier {
		Binding(booleanSupplier::whenFalse, toRun.toLambda().addFinish(booleanSupplier::get))
		return this
	}

	/**
	 * registers [toRun] to be triggered when this condition is true, and ends it early if it becomes false
	 *
	 * @see EnhancedBooleanSupplier.toggleTrue
	 */
	fun toggleTrue(toRun: Command): BoundBooleanSupplier {
		Binding(booleanSupplier::toggleTrue, toRun)
		return this
	}

	/**
	 * registers [toRun] to be triggered when this condition is false, and ends it early if it becomes true
	 *
	 * @see EnhancedBooleanSupplier.toggleFalse
	 */
	fun toggleFalse(toRun: Command): BoundBooleanSupplier {
		Binding(booleanSupplier::toggleFalse, toRun)
		return this
	}
}