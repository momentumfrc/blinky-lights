package org.usfirst.frc.team4999.tools;

import java.util.Arrays;
import java.util.stream.Stream;

import org.usfirst.frc.team4999.lights.*;
import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.lights.animations.Solid;
import org.usfirst.frc.team4999.lights.commands.Command;
import org.usfirst.frc.team4999.lights.commands.ShowCommand;

public class TestAnimator extends Animator {

    private Animation current;
    public final Display display;

    private ShowCommand showCommand = new ShowCommand();

    /**
     * Creates an animator using the specified {@link Display}
     * @param pixels Display to output to
     */
    public TestAnimator(Display display) {
        super(null);
        this.display = display;
        setAnimation(new Solid(Color.BLACK));
    }

    /**
     * Set the animation run on the AnimationThread
     * @param newAnimation
     */
    @Override
    public void setAnimation(Animation newAnimation) {
        if(newAnimation == null) {
            System.out.println("Recieved null animation! Defaulting to solid black");
            current = new Solid(Color.BLACK);
        } else {
            current = newAnimation;
        }
    }

    public void displayFrames(int numFrames) {
        displayFrames(numFrames, false);
    }


    public void displayFrames(int numFrames, boolean shouldSleep) {
        for(int i = 0; i < numFrames; i++) {
            Command[] commands = current.getNextFrame();
            Command[] commandSequence = Stream.concat(Arrays.stream(commands), Stream.of(showCommand)).toArray(Command[]::new);
            display.show(commandSequence);
            int delay = current.getFrameDelayMilliseconds();

            if(delay < 0 ) System.out.println("Animation returned a delay less than 0... interpreting as no delay");
            if(shouldSleep && delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
