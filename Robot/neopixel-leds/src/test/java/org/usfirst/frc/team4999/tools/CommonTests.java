package org.usfirst.frc.team4999.tools;

import org.usfirst.frc.team4999.lights.BrightnessFilter;
import org.usfirst.frc.team4999.lights.animations.Animation;

public class CommonTests {
    
    public static void headlessCompareToFile(Animation animation, int frames, String file) {
        BrightnessFilter.setBrightness(1);
        
        BufferDisplay display = new BufferDisplay(80);
        PixelComparator comparator = new PixelComparator();
        display.addBufferListener(comparator);

        TestAnimator animator = new TestAnimator(display);

        animator.setAnimation(animation);

        animator.displayFrames(frames);

        //animator.display.writeToFile("BlinkAnimation.bin");
        comparator.compareToFile(file);
    }

}
