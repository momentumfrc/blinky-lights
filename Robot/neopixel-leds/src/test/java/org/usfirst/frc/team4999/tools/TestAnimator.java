package org.usfirst.frc.team4999.tools;

import java.util.Arrays;
import java.util.function.BooleanSupplier;

import org.usfirst.frc.team4999.lights.*;
import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.lights.animations.Solid;
import org.usfirst.frc.team4999.lights.commands.Command;

public class TestAnimator extends Animator {
	
    private Animation current;
    private final Display display;
	
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
        displayFrames(numFrames, () -> false);
    }

    public void displayFrames(int numFrames, BooleanSupplier shouldSleep) {
        for(int i = 0; i < numFrames; i++) {
            Command[] commands = current.getNextFrame();
			Packet[] builtCommands = Arrays.stream(commands).map(Command::build).toArray(Packet[]::new);
			display.show(builtCommands);
			int delay = current.getFrameDelayMilliseconds();
			
			if(delay < 0 ) System.out.println("Animation returned a delay less than 0... interpreting as no delay");
			if(shouldSleep.getAsBoolean() && delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public void stepFrames(int numFrames, Object stepLock) {
        for(int i = 0; i < numFrames; i++) {
			Command[] commands = current.getNextFrame();
			Packet[] builtCommands = Arrays.stream(commands).map(Command::build).toArray(Packet[]::new);
            display.show(builtCommands);
            
            try {
                stepLock.wait();
            } catch (InterruptedException e) {
                break;
            }
    }
    }
}
