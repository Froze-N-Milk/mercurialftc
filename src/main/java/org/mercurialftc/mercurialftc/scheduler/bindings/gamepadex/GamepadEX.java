package org.mercurialftc.mercurialftc.scheduler.bindings.gamepadex;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.mercurialftc.mercurialftc.scheduler.bindings.Binding;

@SuppressWarnings("unused rawtypes")
public class GamepadEX {
	private final Binding
			a,
			b,
			x,
			y,
			circle,
			square,
			cross,
			triangle,
			dpad_up,
			dpad_down,
			dpad_left,
			dpad_right,
			left_bumper,
			right_bumper,
			left_stick_button,
			right_stick_button,
			back,
			start,
			share,
			guide,
			options,
			ps,
			touchpad_finger_1,
			touchpad_finger_2;
	private final DomainSupplier
			leftX,
			leftY,
			rightX,
			rightY,
			left_trigger,
			right_trigger;

	public GamepadEX(Gamepad gamepad) {

		a = new Binding(() -> gamepad.a);
		b = new Binding(() -> gamepad.b);
		x = new Binding(() -> gamepad.x);
		y = new Binding(() -> gamepad.y);

		circle = new Binding(() -> gamepad.circle);
		square = new Binding(() -> gamepad.square);
		cross = new Binding(() -> gamepad.cross);
		triangle = new Binding(() -> gamepad.triangle);

		dpad_up = new Binding(() -> gamepad.dpad_up);
		dpad_down = new Binding(() -> gamepad.dpad_down);
		dpad_left = new Binding(() -> gamepad.dpad_left);
		dpad_right = new Binding(() -> gamepad.dpad_right);

		left_bumper = new Binding(() -> gamepad.left_bumper);
		right_bumper = new Binding(() -> gamepad.right_bumper);

		left_stick_button = new Binding(() -> gamepad.left_stick_button);
		right_stick_button = new Binding(() -> gamepad.right_stick_button);

		back = new Binding(() -> gamepad.back);
		start = new Binding(() -> gamepad.start);
		share = new Binding(() -> gamepad.share);
		guide = new Binding(() -> gamepad.guide);
		options = new Binding(() -> gamepad.options);
		ps = new Binding(() -> gamepad.ps);
		touchpad_finger_1 = new Binding(() -> gamepad.touchpad_finger_1);
		touchpad_finger_2 = new Binding(() -> gamepad.touchpad_finger_2);

		leftX = new DomainSupplier(() -> gamepad.left_stick_x);
		leftY = new DomainSupplier(() -> -gamepad.left_stick_y);
		rightX = new DomainSupplier(() -> gamepad.right_stick_x);
		rightY = new DomainSupplier(() -> -gamepad.right_stick_y);

		left_trigger = new DomainSupplier(() -> gamepad.left_trigger);
		right_trigger = new DomainSupplier(() -> gamepad.right_trigger);
	}

	public Binding circle() {
		return circle;
	}

	public Binding square() {
		return square;
	}

	public Binding cross() {
		return cross;
	}

	public Binding triangle() {
		return triangle;
	}

	public Binding start() {
		return start;
	}

	public Binding share() {
		return share;
	}

	public Binding guide() {
		return guide;
	}

	public Binding options() {
		return options;
	}

	public Binding ps() {
		return ps;
	}

	public Binding touchpad_finger_1() {
		return touchpad_finger_1;
	}

	public Binding touchpad_finger_2() {
		return touchpad_finger_2;
	}

	public Binding a() {
		return a;
	}

	public Binding b() {
		return b;
	}

	public Binding x() {
		return x;
	}

	public Binding y() {
		return y;
	}

	public Binding dpad_up() {
		return dpad_up;
	}

	public Binding dpad_down() {
		return dpad_down;
	}

	public Binding dpad_left() {
		return dpad_left;
	}

	public Binding dpad_right() {
		return dpad_right;
	}

	public Binding left_bumper() {
		return left_bumper;
	}

	public Binding right_bumper() {
		return right_bumper;
	}

	public Binding left_stick_button() {
		return left_stick_button;
	}

	public Binding right_stick_button() {
		return right_stick_button;
	}

	public Binding back() {
		return back;
	}

	public DomainSupplier leftX() {
		return leftX;
	}

	/**
	 * the left stick y has been inverted to make a vertical increase also result in a positive increase in value
	 * <p>see {@link DomainSupplier#invert()} if you wish to use the default configuration instead</p>
	 */
	public DomainSupplier leftY() {
		return leftY;
	}

	public DomainSupplier rightX() {
		return rightX;
	}

	/**
	 * the right stick y has been inverted to make a vertical increase also result in a positive increase in value
	 * <p>see {@link DomainSupplier#invert()} if you wish to use the default configuration instead</p>
	 */
	public DomainSupplier rightY() {
		return rightY;
	}

	public DomainSupplier left_trigger() {
		return left_trigger;
	}

	public DomainSupplier right_trigger() {
		return right_trigger;
	}
}

