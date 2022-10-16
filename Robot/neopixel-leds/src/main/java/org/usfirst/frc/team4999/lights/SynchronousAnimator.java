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
public class SynchronousAnimator implements Animator {
    protected Display display;
    protected Animation currentAnimation;
    private long lastLoopTs = 0;
    private long timeTillNextFrame = 0;

    private Command showCommand = new ShowCommand();

     /**
     * Creates an animator using the specified {@link Display}
     * @param pixels The output display
     */
    public SynchronousAnimator(Display pixels) {
        if(pixels == null)
            throw new IllegalArgumentException("Invalid display");
        this.display = pixels;

        this.currentAnimation = new Solid(Color.WHITE);
    }

     /**
     * {@inheritDoc}
     */
    @Override
    public void setAnimation(Animation newAnimation) {
        if(newAnimation == null) {
            throw new IllegalArgumentException("Invalid animation");
        }
        this.currentAnimation = newAnimation;
    }

    public void animatePeriodic() {
        long nowTs = System.currentTimeMillis();
        long timeSinceLastLoop = nowTs - lastLoopTs;
        lastLoopTs = nowTs;

        timeTillNextFrame -= timeSinceLastLoop;
        if(timeTillNextFrame <= 0) {
            // show current frame
            Command[] commands = currentAnimation.getNextFrame();
            Command[] commandSequence = Stream.concat(Arrays.stream(commands), Stream.of(showCommand)).toArray(Command[]::new);
            display.show(commandSequence);
            // get how long to delay for
            timeTillNextFrame = currentAnimation.getFrameDelayMilliseconds();
        }
    }
}
