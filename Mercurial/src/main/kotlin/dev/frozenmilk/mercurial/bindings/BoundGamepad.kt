package dev.frozenmilk.mercurial.bindings

import dev.frozenmilk.dairy.calcified.gamepad.CalcifiedGamepad

class BoundGamepad(gamepad: CalcifiedGamepad){
	/**
	 * left analog stick horizontal axis
	 */
	var leftStickX = BoundNumberSupplier(gamepad.leftStickX)

	/**
	 * left analog stick vertical axis
	 */
	var leftStickY = BoundNumberSupplier(gamepad.leftStickY)

	/**
	 * right analog stick horizontal axis
	 */
	var rightStickX = BoundNumberSupplier(gamepad.rightStickX)

	/**
	 * right analog stick vertical axis
	 */
	var rightStickY = BoundNumberSupplier(gamepad.rightStickY)

	/**
	 * dpad up
	 */
	var dpadUp = BoundBooleanSupplier(gamepad.dpadUp)

	/**
	 * dpad down
	 */
	var dpadDown = BoundBooleanSupplier(gamepad.dpadDown)

	/**
	 * dpad left
	 */
	var dpadLeft = BoundBooleanSupplier(gamepad.dpadLeft)

	/**
	 * dpad right
	 */
	var dpadRight = BoundBooleanSupplier(gamepad.dpadRight)

	/**
	 * button a
	 */
	var a = BoundBooleanSupplier(gamepad.a)

	/**
	 * button b
	 */
	var b = BoundBooleanSupplier(gamepad.b)

	/**
	 * button x
	 */
	var x = BoundBooleanSupplier(gamepad.x)

	/**
	 * button y
	 */
	var y = BoundBooleanSupplier(gamepad.y)

	/**
	 * button guide - often the large button in the middle of the controller. The OS may
	 * capture this button before it is sent to the app; in which case you'll never
	 * receive it.
	 */
	var guide = BoundBooleanSupplier(gamepad.guide)

	/**
	 * button start
	 */
	var start = BoundBooleanSupplier(gamepad.start)

	/**
	 * button back
	 */
	var back = BoundBooleanSupplier(gamepad.back)

	/**
	 * button left bumper
	 */
	var leftBumper = BoundBooleanSupplier(gamepad.leftBumper)

	/**
	 * button right bumper
	 */
	var rightBumper = BoundBooleanSupplier(gamepad.rightBumper)

	/**
	 * left stick button
	 */
	var leftStickButton = BoundBooleanSupplier(gamepad.leftStickButton)

	/**
	 * right stick button
	 */
	var rightStickButton = BoundBooleanSupplier(gamepad.rightStickButton)

	/**
	 * left trigger
	 */
	var leftTrigger = BoundNumberSupplier(gamepad.leftTrigger)

	/**
	 * right trigger
	 */
	var rightTrigger = BoundNumberSupplier(gamepad.rightTrigger)

	/**
	 * PS4 Support - Circle
	 */
	var circle
		get() = b
		set(value) {
			b = value
		}

	/**
	 * PS4 Support - cross
	 */
	var cross
		get() = a
		set(value) {
			a = value
		}

	/**
	 * PS4 Support - triangle
	 */
	var triangle
		get() = y
		set(value) {
			y = value
		}

	/**
	 * PS4 Support - square
	 */
	var square
		get() = x
		set(value) {
			x = value
		}

	/**
	 * PS4 Support - share
	 */
	var share
		get() = back
		set(value) {
			back = value
		}

	/**
	 * PS4 Support - options
	 */
	var options
		get() = start
		set(value) {
			start = value
		}

	/**
	 * PS4 Support - touchpad
	 */
	var touchpad = BoundBooleanSupplier(gamepad.touchpad)

	var touchpadFinger1 = BoundBooleanSupplier(gamepad.touchpadFinger1)

	var touchpadFinger2 = BoundBooleanSupplier(gamepad.touchpadFinger2)

	var touchpadFinger1X = BoundNumberSupplier(gamepad.touchpadFinger1X)

	var touchpadFinger1Y = BoundNumberSupplier(gamepad.touchpadFinger1Y)

	var touchpadFinger2X = BoundNumberSupplier(gamepad.touchpadFinger2X)

	var touchpadFinger2Y = BoundNumberSupplier(gamepad.touchpadFinger2Y)

	/**
	 * PS4 Support - PS Button
	 */
	var ps
		get() = guide
		set(value) {
			guide = value
		}
}