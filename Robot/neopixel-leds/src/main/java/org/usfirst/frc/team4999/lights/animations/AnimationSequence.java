package org.usfirst.frc.team4999.lights.animations;

import org.usfirst.frc.team4999.lights.commands.Command;

/**
 * An meta-animation that will sequentially show several other animations.
 */
public class AnimationSequence implements Animation {

    private AnimationSequenceMember[] members;

    private long startTime = 0;
    private long currentTime = 0;
    private int currentidx = 0;

    /**
     * Construct an AnimationSequence that will show every animation for the
     * same fixed duration.
     * @param animations the animations which will be shown
     * @param time the duration (in milliseconds) for which each animation
     * will be shown
     */
    public AnimationSequence(Animation[] animations, int time) {
        members = new AnimationSequenceMember[animations.length];
        for(int i = 0; i < animations.length; i++) {
            members[i] = new AnimationSequenceMember(animations[i], time);
        }
    }

    /**
     * An {@link Animation} paired with the duration of time for which that
     * animation will be shown.
     */
    public static class AnimationSequenceMember {
        private final Animation animation;
        private final int showTimeMs;

        /**
         * Construct an AnimationSequenceMember that will show the specified
         * animation for the specified duration of time.
         * @param animation the animation to show
         * @param showTimeMs the duration (in milliseconds) for which to show
         * the animation
         */
        public AnimationSequenceMember(Animation animation, int showTimeMs) {
            this.animation = animation;
            this.showTimeMs = showTimeMs;
        }

        public Animation getAnimation() {
            return animation;
        }

        public int getShowTimeMs() {
            return showTimeMs;
        }
    }

    /**
     * Construct an AnimationSequence that will show every animation for a
     * distinct period of time.
     * @param members the animations which will be shown
     */
    public AnimationSequence(AnimationSequenceMember[] members) {
        this.members = members;
    }

    /**
     * Loops through a series of animations
     * <p>
     * Note: A time must be specified for each animation. Thus,
     * animations.length must equal times.length
     *
     * @deprecated As of release 1.10, replaced by
     * {@link #AnimationSequence(AnimationSequenceMember[])}
     *
     * @param animations the animations which will be shown
     * @param times the time to wait (in milliseconds) for each animation
     */
    @Deprecated
    public AnimationSequence(Animation[] animations, int[] times) {
        if (animations.length != times.length)
            throw new IllegalArgumentException("Each animation must have a time set");

        members = new AnimationSequenceMember[animations.length];
        for(int i = 0; i < members.length; ++i) {
            members[i] = new AnimationSequenceMember(animations[i], times[i]);
        }
    }

    @Override
    public Command[] getNextFrame() {
        AnimationSequenceMember curr = members[currentidx];
        if(currentTime - startTime >= curr.getShowTimeMs()) {
            startTime = currentTime;
            currentidx = (currentidx + 1) % members.length;
        }
        curr = members[currentidx];
        return curr.getAnimation().getNextFrame();
    }

    @Override
    public int getFrameDelayMilliseconds() {
        AnimationSequenceMember curr = members[currentidx];

        int delay = curr.getAnimation().getFrameDelayMilliseconds();

        // If the animation wants to delay indefinitely, instead delay for the length of this animation's duration
        delay = (delay < 0) ? curr.getShowTimeMs() : delay;

        currentTime += delay;

        return delay;
    }

}
