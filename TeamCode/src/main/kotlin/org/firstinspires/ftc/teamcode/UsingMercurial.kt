package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.frozenmilk.dairy.calcified.Calcify
import dev.frozenmilk.dairy.core.OpModeLazyCell
import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.Mercurify

@Mercurify
@Calcify
@TeleOp
class UsingMercurial : OpMode() {
	val demoSubsystem by OpModeLazyCell {
		Mercurial.registerSubsystem(DemoSubsystem)
		DemoSubsystem
	}
	override fun init() {

	}

	override fun loop() {
	}
}