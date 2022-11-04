package org.usfirst.frc.team4999.lights.animations;

import java.util.ArrayList;

import org.usfirst.frc.team4999.lights.commands.*;

/**
 * A meta-animation used to display one animation on top of another.
 * <p>
 * Note that, in order to overlay animation A on top of animation B,
 * animation A must not consume the entire LED strip. This means that
 * the Commands specified by animation A must leave some of the LEDs undrawn.
 */
public class Overlay implements Animation {

    private static final int delay_grouping = 5;

    private class AnimationTiming {

        public Animation animation;
        public int remainingDelay;
        public boolean resetTiming;
        public Command[] currentFrame;

        private AnimationTiming(Animation animation) {
            this.animation = animation;
            resetTiming = true;
        }


    }

    private AnimationTiming[] animations;
    private ArrayList<Command> commandBuffer;

    /**
     * Construct an {@link Overlay} using the provided animations.
     * Animations which occur later in the input array will be displayed over
     * animations which occur earlier.
     * @param animations the animations to overlay
     */
    public Overlay(Animation[] animations) {
        this.animations = new AnimationTiming[animations.length];
        for(int i = 0; i < animations.length; i++) {
            this.animations[i] = new AnimationTiming(animations[i]);
        }

        commandBuffer = new ArrayList<>();
    }

    @Override
    public Command[] getNextFrame() {
        commandBuffer.clear();

        for(int i = 0; i < animations.length; i++) {
            if(animations[i].resetTiming) {
                animations[i].currentFrame = animations[i].animation.getNextFrame();
            }
            for(int j = 0; j < animations[i].currentFrame.length; j++) {
                commandBuffer.add(animations[i].currentFrame[j]);
            }
        }

        return commandBuffer.toArray(new Command[]{});
    }

    @Override
    public int getFrameDelayMilliseconds() {

        for(int i = 0; i < animations.length; i++) {
            AnimationTiming curr = animations[i];
            if(curr.resetTiming) {
                curr.resetTiming = false;
                curr.remainingDelay = curr.animation.getFrameDelayMilliseconds();
            }
        }

        AnimationTiming min_delay = animations[0];
        for(int i = 0; i < animations.length; i++) {
            AnimationTiming curr = animations[i];
            if(min_delay.remainingDelay > curr.remainingDelay)
                min_delay = curr;
        }

        int delay = min_delay.remainingDelay;
        for(int i = 0; i < animations.length; i++) {
            AnimationTiming curr = animations[i];
            curr.remainingDelay -= delay;
            if(curr.remainingDelay < delay_grouping) {
                curr.resetTiming = true;
            }
        }

        return delay;
    }

}
