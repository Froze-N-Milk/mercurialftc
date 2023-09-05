package org.mercurialftc.mercurialftc.scheduler.triggers.gamepadex;

import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.scheduler.triggers.Trigger;

import java.util.function.BooleanSupplier;

public class ButtonEX {
    private final BooleanSupplier internalInput;
    private boolean previousState;
    private long leadingEdgeDebounce;
    private long trailingEdgeDebounce;
    private long lastCheck = System.nanoTime() - 100;

    private boolean processedInput = false;
    
    public ButtonEX(BooleanSupplier internalInput) {
        this.internalInput = internalInput;
    }

    /**
     * must be called at the end of every teleop loop, or other environment in which the {@link GamepadEX} inputs are used, which should be handled by {@link GamepadEX} update method
     */
    public void endLoopUpdate() {
        previousState = buttonState();
    }

    /**
     * includes debouncing
     *
     * @return returns the boolean value of the button with debouncing used
     */
    
    public boolean buttonState(){
        if (processedInput != previousState) {
            lastCheck = System.nanoTime();
        }
        if (internalInput.getAsBoolean() && (System.nanoTime() - lastCheck + 1 >= leadingEdgeDebounce)) {
            processedInput = true;
            lastCheck = System.nanoTime();
        }
        else if (!internalInput.getAsBoolean() && (System.nanoTime() - lastCheck + 1 >= trailingEdgeDebounce)) {
            processedInput = false;
            lastCheck = System.nanoTime();
        }
    
        return processedInput;
    }

    public ButtonEX isPressed(Command toRun) {
        new Trigger(this::buttonState, toRun);
        return this;
    }
    public ButtonEX isReleased(Command toRun) {
        new Trigger(() -> !buttonState(), toRun);
        return this;
    }


    public ButtonEX raw(Command toRun) {
        new Trigger(internalInput, toRun);
        return this;
    }

    public ButtonEX onPress(Command toTun) {
        new Trigger(() -> (buttonState() && buttonState() != previousState), toTun);
        return this;
    }

    public ButtonEX onRelease(Command toRun) {
        new Trigger(() -> (!buttonState() && buttonState() != previousState), toRun);
        return this;
    }
    public enum debouncingType {
        LEADING_EDGE,
        TRAILING_EDGE,
        BOTH
    }

    /**
     * applies debouncing to the button
     *
     * @param type     type of debouncing to be updated, leading edge effects the change from true to false, trailing edge effects the change from false to true
     * @param duration the duration of the debouncing to be applied, in seconds
     * @return returns the updated button object
     */
    public ButtonEX debounce(debouncingType type, long duration) {
        switch (type) {
            case LEADING_EDGE:
                leadingEdgeDebounce = (long) (duration * 1e9);
                break;
            case TRAILING_EDGE:
                trailingEdgeDebounce = (long) (duration * 1e9);
                break;
            case BOTH:
                leadingEdgeDebounce = (long) (duration * 1e9);
                trailingEdgeDebounce = (long) (duration * 1e9);
                break;
        }
        return this;
    }
}
