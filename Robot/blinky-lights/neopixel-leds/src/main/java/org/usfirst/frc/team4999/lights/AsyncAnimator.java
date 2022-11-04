package org.usfirst.frc.team4999.lights;

import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.lights.animations.Solid;

/**
 * Shows the frames of an {@link org.usfirst.frc.team4999.lights.animations.Animation} on a
 * {@link Display}.
 * @author jordan
 *
 */
public class AsyncAnimator implements Animator {
    private static class AnimatorThread extends Thread {
        final BlockingAnimator animator;

        public AnimatorThread(Display display, Animation current) {
            animator = new BlockingAnimator(display, current);
        }

        @Override
        public void run() {
            animator.animate();
        }

        @Override
        public void interrupt() {
            animator.interrupt();
            super.interrupt();
        }
    }

    private AnimatorThread thread;

    /**
     * Creates an animator using the specified {@link Display}
     * @param pixels The output display
     */
    public AsyncAnimator(Display pixels) {
        if(pixels == null)
            return;
        thread = new AnimatorThread(pixels, new Solid(Color.BLACK));
        thread.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAnimation(Animation newAnimation) {
        if(newAnimation == null) {
            System.out.println("Recieved null animation! Defaulting to solid black");
            thread.animator.setAnimation(new Solid(Color.BLACK));
            return;
        }
        thread.animator.setAnimation(newAnimation);
    }

    /**
     * Stops the current animation and kills the animator thread.
     * <p>
     * Note: <b>Once this method is invoked, the only way to re-start the animation is to create
     * a new Animator</b>
     */
    public void stopAnimation() {
        thread.interrupt();
    }

    public void setWarnOnLoopOverrun(boolean shouldWarn) {
        this.thread.animator.setWarnOnLoopOverrun(shouldWarn);
    }
}
