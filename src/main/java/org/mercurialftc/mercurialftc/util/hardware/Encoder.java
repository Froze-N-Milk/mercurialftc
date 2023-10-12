package org.mercurialftc.mercurialftc.util.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.jetbrains.annotations.NotNull;

public class Encoder {
	private final DcMotor motor;
	private Direction direction;
	private double previousTime;
	private int previousPosition;
	private VelocityDataPacket output;

	/**
	 * an encoder wrapper, designed to work with REV Through Bore Encoders in particular
	 * <p>ENSURE THAT THE ENCODER IS PLUGGED INTO PORTS 0 OR 3, PREFERABLY IN THE CONTROL HUB</p>
	 *
	 * @param motor the motor that the encoder is plugged into,
	 */
	public Encoder(@NotNull DcMotor motor) {
		this.motor = motor;
		this.direction = Direction.FORWARD;

		previousTime = System.nanoTime() / 1e9;
		previousPosition = motor.getCurrentPosition();

		output = new VelocityDataPacket(0, 1);
	}

	/**
	 * sets the motor associated with this encoder to {@link DcMotor.RunMode#STOP_AND_RESET_ENCODER} and then back its previous {@link DcMotor.RunMode}
	 */
	public void reset() {
		output = new VelocityDataPacket(0, 1);
		DcMotor.RunMode previousRunMode = motor.getMode();
		motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		previousPosition = motor.getCurrentPosition();
		previousTime = System.nanoTime() / 1e9;
		motor.setMode(previousRunMode);
	}

	public Direction getDirection() {
		return direction;
	}

	/**
	 * Allows you to set the direction of the counts and velocity without modifying the motor's direction state
	 *
	 * @param direction either reverse or forward depending on if encoder counts should be negated
	 */
	public Encoder setDirection(Direction direction) {
		this.direction = direction;
		return this;
	}

	private int getMultiplier() {
		return getDirection().getMultiplier() * (motor.getDirection() == DcMotorSimple.Direction.FORWARD ? 1 : -1);
	}

	/**
	 * Gets the position from the underlying motor and adjusts for the set direction.
	 *
	 * @return encoder position
	 */
	public int getCurrentPosition() {
		return motor.getCurrentPosition() * getMultiplier();
	}

	/**
	 * needs {@link #updateVelocity()} to be called exactly once per loop, before this function is ever called, to be accurate
	 *
	 * @return the current value of the output, calculated by {@link #updateVelocity()}
	 */
	public VelocityDataPacket getVelocityDataPacket() {
		return output;
	}

	/**
	 * <p>needs to be called once per loop to be accurate</p>
	 * <p>if using this in a {@link org.mercurialftc.mercurialftc.scheduler.subsystems.SubsystemInterface} subclass, chuck it in the periodic loop, and never think about it again</p>
	 */
	public void updateVelocity() {
		int currentPosition = getCurrentPosition();
		double currentTime = System.nanoTime() / 1e9;
		double dt = currentTime - previousTime;

		VelocityDataPacket velocity = new VelocityDataPacket((currentPosition - previousPosition), dt);

		previousTime = currentTime;
		previousPosition = currentPosition;

		output = velocity;
	}

	public enum Direction {
		FORWARD((byte) 1),
		REVERSE((byte) -1);

		private final byte multiplier;

		Direction(byte multiplier) {
			this.multiplier = multiplier;
		}

		private byte getMultiplier() {
			return multiplier;
		}
	}

	public static class VelocityDataPacket {
		private final double deltaTime;
		private final int deltaPosition;

		private VelocityDataPacket(int deltaPosition, double deltaTime) {
			this.deltaPosition = deltaPosition;
			this.deltaTime = deltaTime;
		}

		public double getDeltaTime() {
			return deltaTime;
		}

		public int getDeltaPosition() {
			return deltaPosition;
		}

		public double getVelocity() {
			return deltaPosition / deltaTime;
		}
	}
}
