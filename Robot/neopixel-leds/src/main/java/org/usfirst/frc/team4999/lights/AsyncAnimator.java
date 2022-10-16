package org.usfirst.frc.team4999.lights;

import java.util.Arrays;
import java.util.stream.Stream;

import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.lights.animations.Solid;
import org.usfirst.frc.team4999.lights.commands.Command;
import org.usfirst.frc.team4999.lights.commands.ShowCommand;

/**
 * Shows the frames of an {@link org.usfirst.frc.team4999.lights.animations.Animation} on a
 * {@link Display}.
 * @author jordan
 *
 */
public class AsyncAnimator implements Animator {
    class AnimatorThread extends Thread {
        private Display out;
        private Animation current;

        // Modifier intentionally omitted - I want the default package visibility
        boolean warnLoopOverrun;

        private ShowCommand showCommand = new ShowCommand();

        public AnimatorThread(Display out, Animation current) {
            super("Animator Thread");
            this.out = out;
            this.current = current;
        }

        public void setAnimation(Animation newAnimation) {
            this.current = newAnimation;
        }

        public void run() {
            while(!Thread.interrupted()){
                // Note how long the send takes
                long millis = System.currentTimeMillis();
                // Make a local reference to the current animation
                // This way, if current is overwritten by setAnimation mid-loop, the code is using a local reference that isn't overwritten
                Animation animation = current;
                // show current frame
                Command[] commands = animation.getNextFrame();
                Command[] commandSequence = Stream.concat(Arrays.stream(commands), Stream.of(showCommand)).toArray(Command[]::new);
                out.show(commandSequence);
                // get how long to delay for
                int delay = animation.getFrameDelayMilliseconds();

                if(delay < 0 ) System.out.println("Animation returned a delay less than 0... interpreting as no delay");

                // Account for transmission time before delaying
                delay -= (System.currentTimeMillis() - millis);
                if (delay > 0) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        break;
                    }
                } else if(warnLoopOverrun) {
                    System.out.format("\u001B[31mLED clock lag (overrun of %dms)\u001B[37m\n", -delay);
                }
            }
        }
    }

    private AnimatorThread animate;

    /**
     * Creates an animator using the {@link NeoPixels} as the default display
     */
    public AsyncAnimator() {
        this(NeoPixels.getInstance());
    }

    /**
     * Creates an animator using the specified {@link Display}
     * @param pixels The output display
     */
    public AsyncAnimator(Display pixels) {
        if(pixels == null)
            return;
        animate = new AnimatorThread(pixels, new Solid(Color.BLACK));
        animate.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAnimation(Animation newAnimation) {
        if(newAnimation == null) {
            System.out.println("Recieved null animation! Defaulting to solid black");
            animate.setAnimation(new Solid(Color.BLACK));
            return;
        }
        animate.setAnimation(newAnimation);
    }

    /**
     * Stops the current animation and kills the animator thread.
     * <p>
     * Note: <b>Once this method is invoked, the only way to re-start the animation is to create
     * a new Animator</b>
     */
    public void stopAnimation() {
        animate.interrupt();
    }

    public void setWarnOnLoopOverrun(boolean shouldWarn) {
        this.animate.warnLoopOverrun = shouldWarn;
    }
}
