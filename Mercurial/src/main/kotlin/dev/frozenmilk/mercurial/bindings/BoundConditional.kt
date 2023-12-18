package dev.frozenmilk.mercurial.bindings

import dev.frozenmilk.dairy.calcified.gamepad.Conditional
import dev.frozenmilk.dairy.calcified.gamepad.conditionalBind
import java.util.function.Supplier

class BoundConditional<N: Number>(private val conditional: Conditional<N>){
	constructor(numberSupplier: Supplier<N>) : this(numberSupplier.conditionalBind())

	fun lessThan(value: N): BoundConditional<N> {
		conditional.lessThan(value)
		return this
	}
	fun lessThanEqualTo(value: N): BoundConditional<N> {
		conditional.lessThanEqualTo(value)
		return this
	}
	fun greaterThan(value: N): BoundConditional<N> {
		conditional.greaterThan(value)
		return this
	}
	fun greaterThanEqualTo(value: N): BoundConditional<N> {
		conditional.greaterThanEqualTo(value)
		return this
	}
	fun bind(): BoundBooleanSupplier {
		return BoundBooleanSupplier(conditional.bind())
	}
}