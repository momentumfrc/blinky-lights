package org.usfirst.frc.team4999.lights.animations;

import org.usfirst.frc.team4999.lights.commands.Command;

/**
 * Animations are used by the
 * {@link org.usfirst.frc.team4999.lights.Animator Animator}
 * to determine what colors will be displayed on the LEDs at any moment in time.
 * <p>
 * Animations are defined as follows. First, the Animator uses
 * {@link #getNextFrame()} to build the current frame of the animation. Next,
 * the Animator displays the frame for the period of time specified
 * by {@link #getFrameDelayMilliseconds()}. Finally, the animator will use
 * {@link #getNextFrame()} to get the next frame of the animation.
 */
public interface Animation {
    /**
     * Gets the sequence of commands that should be used to build the current
     * frame of the animation.
     * @return the sequence of commands
     */
    Command[] getNextFrame();
    /**
     * Gets time to wait before calling getNextFrame.
     * @return the time interval in milliseconds
     */
    int getFrameDelayMilliseconds();
}
