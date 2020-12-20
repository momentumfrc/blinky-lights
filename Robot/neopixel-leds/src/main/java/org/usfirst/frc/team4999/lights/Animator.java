package org.usfirst.frc.team4999.lights;

import java.util.Arrays;

import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.lights.animations.Solid;
import org.usfirst.frc.team4999.lights.commands.Command;

/**
 * Runs in an infinite loop. Displays a frame of {@link Animation}, then waits the duration specified by the animation
 * @author jordan
 *
 */
class AnimatorThread extends Thread {
	private Display out;
	private Animation current;
	
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
			Packet[] builtCommands = Arrays.stream(commands).map(Command::build).toArray(Packet[]::new);
			out.show(builtCommands);
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
			}
		}
	}
}
	
/**
 * Holds a runnable {@link AnimationThread}
 * @author jordan
 *
 */
public class Animator {
	
	private AnimatorThread animate;
	
	/**
	 * Creates an animator using the {@link NeoPixels} as the default display
	 */
	public Animator() {
		this(NeoPixels.getInstance());
	}
	
	/**
	 * Creates an animator using the specified {@link Display} 
	 * @param pixels Display to output to
	 */
	public Animator(Display pixels) {
		if(pixels == null)
			return;
		animate = new AnimatorThread(pixels, new Solid(Color.BLACK));
		animate.start();
	}
	
	/**
	 * Set the animation run on the AnimationThread
	 * @param newAnimation
	 */
	public void setAnimation(Animation newAnimation) {
		if(newAnimation == null) {
			System.out.println("Recieved null animation! Defaulting to solid black");
			animate.setAnimation(new Solid(Color.BLACK));
			return;
		}
		animate.setAnimation(newAnimation);
	}
	
	public void stopAnimation() {
		animate.interrupt();
	}
}