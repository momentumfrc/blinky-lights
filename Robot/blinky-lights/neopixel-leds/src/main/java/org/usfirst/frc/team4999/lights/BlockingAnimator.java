package org.usfirst.frc.team4999.lights;

import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.lights.commands.Command;
import org.usfirst.frc.team4999.lights.commands.ShowCommand;

import java.util.Arrays;
import java.util.stream.Stream;

public class BlockingAnimator implements Animator {
    private final Display out;
    private Animation current;
    private ShowCommand showCommand = new ShowCommand();

    private boolean run = true;
    private boolean warnLoopOverrun = false;

    public BlockingAnimator(Display display, Animation current) {
        this.out = display;
        this.current = current;
    }

    public void animate() {
        while(run) {
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

            if (delay < 0) System.out.println("Animation returned a delay less than 0... interpreting as no delay");

            // Account for transmission time before delaying
            delay -= (System.currentTimeMillis() - millis);
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    break;
                }
            } else if (warnLoopOverrun) {
                System.out.format("\u001B[31mLED clock lag (overrun of %dms)\u001B[37m\n", -delay);
            }
        }
    }

    public void interrupt() {
        run = false;
    }

    public void setWarnOnLoopOverrun(boolean shouldWarn) {
        this.warnLoopOverrun = shouldWarn;
    }

    @Override
    public void setAnimation(Animation newAnimation) {
        current = newAnimation;
    }
}
