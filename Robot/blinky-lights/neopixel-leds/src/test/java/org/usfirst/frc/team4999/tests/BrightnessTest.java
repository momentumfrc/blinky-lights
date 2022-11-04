package org.usfirst.frc.team4999.tests;

import org.junit.Test;
import org.usfirst.frc.team4999.lights.BrightnessFilter;
import org.usfirst.frc.team4999.lights.BufferDisplay;
import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.lights.animations.Solid;
import org.usfirst.frc.team4999.tools.*;


public class BrightnessTest {
    @Test
    public void testBrightness() {
        BufferDisplay display = new BufferDisplay(80);
        PixelComparator comparator = new PixelComparator();
        display.addBufferListener(comparator);

        TestAnimator animator = new TestAnimator(display);

        Animation white = new Solid(Color.WHITE);
        Animation red = new Solid(Color.RED);
        Animation green = new Solid(Color.GREEN);
        Animation blue = new Solid(Color.BLUE);

        BrightnessFilter.setBrightness(1);

        animator.setAnimation(white);
        animator.displayFrames(2);
        animator.setAnimation(red);
        animator.displayFrames(2);
        animator.setAnimation(green);
        animator.displayFrames(2);
        animator.setAnimation(blue);
        animator.displayFrames(2);

        BrightnessFilter.setBrightness(0.4);

        animator.setAnimation(white);
        animator.displayFrames(2);
        animator.setAnimation(red);
        animator.displayFrames(2);
        animator.setAnimation(green);
        animator.displayFrames(2);
        animator.setAnimation(blue);
        animator.displayFrames(2);

        BrightnessFilter.setBrightness(0.1);

        animator.setAnimation(white);
        animator.displayFrames(2);
        animator.setAnimation(red);
        animator.displayFrames(2);
        animator.setAnimation(green);
        animator.displayFrames(2);
        animator.setAnimation(blue);
        animator.displayFrames(2);

        // animator.display.writeToFile("TestBrightness");
        comparator.compareToFile("TestBrightness");
    }
}
