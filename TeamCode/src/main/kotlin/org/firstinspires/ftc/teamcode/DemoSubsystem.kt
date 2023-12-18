package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.util.ElapsedTime
import dev.frozenmilk.dairy.calcified.Calcified
import dev.frozenmilk.dairy.calcified.gamepad.EnhancedNumberSupplier
import dev.frozenmilk.dairy.core.OpModeLazyCell
import dev.frozenmilk.mercurial.commands.Command
import dev.frozenmilk.mercurial.commands.LambdaCommand
import dev.frozenmilk.mercurial.subsystems.Subsystem
import dev.frozenmilk.util.cell.LateInitCell
import kotlin.math.sin

object DemoSubsystem : Subsystem {
	val motor by OpModeLazyCell {
		Calcified.controlHub.getMotor(0)
	}
	val encoder by OpModeLazyCell {
		Calcified.controlHub.getTicksEncoder(0)
	}
	val elapsedTime = ElapsedTime()
	var control by LateInitCell<EnhancedNumberSupplier<Double>>()
	override fun init() {
		defaultCommand = LambdaCommand()
				.addRequirements(this)
				.setExecute { motor.power = sin(elapsedTime.seconds()) }
				.setFinish { false }
	}

	override fun reset() {
		encoder.reset()
	}

	override fun periodic() {

	}

	fun manualControl(enhancedNumberSupplier: EnhancedNumberSupplier<Double>): Command {
		return LambdaCommand()
				.addRequirements(this)
				.setInit { control = enhancedNumberSupplier }
				.setExecute { motor.power = control.get() }
				.setFinish { false }
	}

	override fun close() {

	}
}