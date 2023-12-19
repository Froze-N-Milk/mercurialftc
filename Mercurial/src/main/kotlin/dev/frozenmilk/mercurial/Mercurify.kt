package dev.frozenmilk.mercurial

import dev.frozenmilk.dairy.calcified.Calcified

annotation class Mercurify(
		/**
		 * Controls when [Mercurial] runs reset on subsystems
		 *
		 * Set to false if you want to reset the subsystems by hand
		 *
		 * By default, resets subsystems at the start of an auto, and at the end of a teleop, allowing values to be carried over from an auto to a teleop
		 *
		 * @see dev.frozenmilk.dairy.calcified.Calcify.crossPollinate
		 */
		val crossPollinate: Boolean = true
)
