---
description: Bindings are how Commands are mapped to activation conditions.
---

# Binding

## Built-in Bindings

Bindings can be found built in to the library in two places:

1. [#buttons](gamepadex.md#buttons "mention") on the [gamepadex.md](gamepadex.md "mention")s available through the [opmodeex](../opmodeex/ "mention")
2. DomainBindings built off [#domainsuppliers](gamepadex.md#domainsuppliers "mention")

Custom bindings are easy to build though -> [custom-bindings-and-domainsuppliers.md](custom-bindings-and-domainsuppliers.md "mention")

## Methods

Bindings can hold any number of Commands on each of their triggers, each Trigger you bind using a Binding returns the original Binding back to the author for chaining, but does not actually change the Binding itself.

Each of these methods takes in a Command, denoted as 'toRun', and will automatically queue it once the condition is satisfied.

### onTrue(toRun)

Queues toRun when the binding becomes true i.e. goes from false to true.

### onFalse(toRun)

Queues toRun when the binding becomes false i.e. goes from true to false.

### whileTrue(toRun)

Queues toRun when the binding becomes true i.e. goes from false to true. Adds a finishing condition to toRun which causes toRun to finish if the binding is no longer true.

### whileFalse(toRun)

Queues toRun when the binding becomes false i.e. goes from true to false. Adds a finishing condition to toRun which causes toRun to finish if the binding is no longer false.

## toggle(toRun)

The Binding holds an internal toggle state that changes each time the Binding becomes true and starts as false.

Queues toRun once the internal toggle state becomes true, adds a finishing condition to toRun which causes toRun to finish if the internal toggle state is no longer true.&#x20;

This is similar to whileTrue(), but does not require the Binding to be constantly true.

### getAsBoolean()

Returns the internal state including all modifications.

### debouncing(DebouncingType type, double duration)

Applies debouncing to the edge targeted by the type param, in seconds as specified by duration

### and(BooleanSupplier andSupplier)

Allows you to combine Bindings and BooleanSuppliers together to create compounded bindings with ease. This adds another supplier with the condition that both suppliers must be true for the output to be true.&#x20;

### or(BooleanSupplier orSupplier)

Allows you to combine Bindings and BooleanSuppliers together to create compounded bindings with ease. This adds another supplier with the condition that either of the suppliers must be true for the output to be true.
