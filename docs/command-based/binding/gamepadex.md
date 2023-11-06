---
description: GamepadEX is the MercurialFTC enhancement wrapper over the built in gamepad(s)
---

# GamepadEX

## Accessing the GamepadEX(s)

The GamepadEX(s) can be accessed by calling:

```java
gamepadEX1();
gamepadEX2();
```

Which correspond the similarly numbered gamepads in the SDK.

&#x20;these access methods, importantly, are methods rather than fields.

## Buttons

All the buttons on the sdk gamepads have corresponding Bindings on the associated GamepadEX.

i.e.:

```java
gamepadEX1().a();
```

Gives access to the Binding of button a on GamepadEX 1.

## DomainSuppliers

DomainSuppliers wrap around the sticks of the gamepads, and implement DoubleSupplier.

i.e.:

```java
gamepadEX1().rightY();
```

Gives access to the DomainSupplier of the right stick y on GamepadEX1.

DomainSuppliers have several utilities to make them more powerful that the pre-exisiting sticks:

* The y sticks have been flipped to make them give positive values when moved upwards
* Can have a curve supplier method applied to them
* Can have deadzones applied to them
* Can be inverted, to give a second non-mutated DomainSupplier that inherits all the of the characteristics of the parent.

Note that deadzone operations get done before curve operations when calculating the output.

DomainSuppliers also allow for the building of complex bindings based off the values returned by the DomainSupplier.

This process can be started by calling `.buildBinding()` then using the range specification methods:

* `.lessThan(value)`
* `.greaterThan(value)`
* `.lessThanEqualTo(value)`
* `.greaterThanEqualTo(value)`

The builder will attempt to close these operations into domain sections

i.e.:

```java
gamepadEX2().rightY().buildBinding().greaterThanEqualTo(-1.0).lessThan(-0.5).greaterThan(0.0).bind();
```

Will give a binding that is true if the result is&#x20;

Finally, a binding can be made and used by calling `.bind()` which then works just like any other Binding, however, the additional method `.endBinding()` will be available, to return the original DomainSupplier to the author, for further chaining.&#x20;
