package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.util.ElapsedTime
import dev.frozenmilk.dairy.calcified.Calcified
import dev.frozenmilk.dairy.core.OpModeLazyCell
import dev.frozenmilk.mercurial.commands.LambdaCommand
import dev.frozenmilk.mercurial.subsystems.Subsystem
import kotlin.math.sin

object DemoSubsystem : Subsystem {
	val motor by OpModeLazyCell {
		Calcified.controlHub.getMotor(0)
	}
	val elapsedTime = ElapsedTime()
	override fun init() {
		defaultCommand = LambdaCommand()
				.addRequirements(this)
				.setExecute { motor.power = sin(elapsedTime.seconds()) }
				.setFinish { false }
	}

	override fun reset() {
		// todo lmao this doesn't actually get called yet internally
	}

	override fun periodic() {

	}

	override fun close() {

	}
}