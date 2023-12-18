package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.frozenmilk.dairy.calcified.Calcified
import dev.frozenmilk.dairy.calcified.Calcify
import dev.frozenmilk.dairy.core.FeatureRegistrar
import dev.frozenmilk.dairy.core.OpModeLazyCell
import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.Mercurify

@Mercurify
@Calcify
@TeleOp
class UsingMercurial : OpMode() {
	init {
		FeatureRegistrar.checkFeatures(this, Calcified, Mercurial)
	}

	val demoSubsystem by OpModeLazyCell {
		Mercurial.registerSubsystem(DemoSubsystem)
		DemoSubsystem
	}

	override fun init() {
		Mercurial.gamepad1
				.a
				.whenTrue(
						DemoSubsystem.manualControl(Calcified.gamepad1.leftStickX)
				)
	}

	override fun loop() {
	}
}