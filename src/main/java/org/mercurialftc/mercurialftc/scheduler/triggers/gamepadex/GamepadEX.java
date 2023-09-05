package org.mercurialftc.mercurialftc.scheduler.triggers.gamepadex;

import com.qualcomm.robotcore.hardware.Gamepad;

public class GamepadEX {
    
    public ButtonEX a() {
        return a;
    }
    
    public ButtonEX b() {
        return b;
    }
    
    public ButtonEX x() {
        return x;
    }
    
    public ButtonEX y() {
        return y;
    }
    
    public ButtonEX dpad_up() {
        return dpad_up;
    }
    
    public ButtonEX dpad_down() {
        return dpad_down;
    }
    
    public ButtonEX dpad_left() {
        return dpad_left;
    }
    
    public ButtonEX dpad_right() {
        return dpad_right;
    }
    
    public ButtonEX left_bumper() {
        return left_bumper;
    }
    
    public ButtonEX right_bumper() {
        return right_bumper;
    }
    
    public ButtonEX left_stick_button() {
        return left_stick_button;
    }
    
    public ButtonEX right_stick_button() {
        return right_stick_button;
    }
    
    public ButtonEX back() {
        return back;
    }
    
    public ContinuousInput leftX() {
        return leftX;
    }
    
    public ContinuousInput leftY() {
        return leftY;
    }
    
    public ContinuousInput rightX() {
        return rightX;
    }
    
    public ContinuousInput rightY() {
        return rightY;
    }
    
    public ContinuousInput left_trigger() {
        return left_trigger;
    }
    
    public ContinuousInput right_trigger() {
        return right_trigger;
    }
    
    private final ButtonEX
            a,
            b,
            x,
            y,

            dpad_up,
            dpad_down,
            dpad_left,
            dpad_right,

            left_bumper,
            right_bumper,
    
            left_stick_button,
            right_stick_button,
    
            back;

    private final ContinuousInput
            leftX,
            leftY,
            rightX,
            rightY,

            left_trigger,
            right_trigger;

    Gamepad gamepad;

    public GamepadEX(Gamepad gamepad){
        this.gamepad = gamepad;

        a = new ButtonEX(() -> gamepad.a);
        b = new ButtonEX(() -> gamepad.b);
        x = new ButtonEX(() -> gamepad.x);
        y = new ButtonEX(() -> gamepad.y);

        dpad_up = new ButtonEX(() -> gamepad.dpad_up);
        dpad_down = new ButtonEX(() -> gamepad.dpad_down);
        dpad_left = new ButtonEX(() -> gamepad.dpad_left);
        dpad_right = new ButtonEX(() -> gamepad.dpad_right);

        left_bumper = new ButtonEX(() -> gamepad.left_bumper);
        right_bumper = new ButtonEX(() -> gamepad.right_bumper);
    
        left_stick_button = new ButtonEX(() -> gamepad.left_stick_button);
        right_stick_button = new ButtonEX(() -> gamepad.right_stick_button);
    
        back = new ButtonEX(() -> gamepad.back);
        
        leftX = new ContinuousInput(() -> gamepad.left_stick_x);
        leftY = new ContinuousInput(() -> -gamepad.left_stick_y);
        rightX = new ContinuousInput(() -> gamepad.right_stick_x);
        rightY = new ContinuousInput(() -> -gamepad.right_stick_y);

        left_trigger = new ContinuousInput(() -> gamepad.left_trigger);
        right_trigger = new ContinuousInput(() -> gamepad.right_trigger);
    }

    public void endLoopUpdate(){
        a.endLoopUpdate();
        b.endLoopUpdate();
        x.endLoopUpdate();
        y.endLoopUpdate();
        dpad_up.endLoopUpdate();
        dpad_down.endLoopUpdate();
        dpad_left.endLoopUpdate();
        dpad_right.endLoopUpdate();
        left_bumper.endLoopUpdate();
        right_bumper.endLoopUpdate();
    }
}

