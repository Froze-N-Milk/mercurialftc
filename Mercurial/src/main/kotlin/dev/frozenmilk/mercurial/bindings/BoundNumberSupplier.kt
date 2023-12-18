package dev.frozenmilk.mercurial.bindings

import dev.frozenmilk.dairy.calcified.gamepad.EnhancedNumberSupplier
import java.util.function.Supplier

class BoundNumberSupplier<N: Number>(private val numberSupplier: EnhancedNumberSupplier<N>) : Supplier<Double> by numberSupplier {
	/**
	 * non-mutating
	 */
	fun applyDeadzone(deadzone: Double) = BoundNumberSupplier(numberSupplier.applyDeadzone(deadzone))
	/**
	 * non-mutating
	 */
	fun applyDeadzone(lowerDeadzone: Double, upperDeadzone: Double) = BoundNumberSupplier(numberSupplier.applyDeadzone(lowerDeadzone, upperDeadzone))
	/**
	 * non-mutating
	 */
	fun applyLowerDeadzone(lowerDeadzone: Double) = BoundNumberSupplier(numberSupplier.applyLowerDeadzone(lowerDeadzone))
	/**
	 * non-mutating
	 */
	fun applyUpperDeadzone(upperDeadzone: Double) = BoundNumberSupplier(numberSupplier.applyUpperDeadzone(upperDeadzone))

	fun conditionalBind(): BoundConditional<Double> = BoundConditional(this)
}