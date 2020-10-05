package org.usfirst.frc.team4999.tests;

import org.junit.Test;
import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.commands.*;
import org.usfirst.frc.team4999.lights.commands.StrideCommand;
import org.usfirst.frc.team4999.lights.animations.*;
import org.usfirst.frc.team4999.tools.TestAnimator;

import static org.junit.Assert.assertTrue;

public class ClipTests {

    @Test
    public void testStrideClip1() {
        TestAnimator animator = new TestAnimator(80);
        
        Animation background = new Solid(Color.WHITE);
        Animation strideAnimation = new Animation() {

			@Override
			public Command[] getNextFrame() {
				return new Command[] {new StrideCommand(0, Color.BLACK, 2, 4)};
			}

			@Override
			public int getFrameDelayMilliseconds() {
				return 500;
			}
            
        };
        Animation clipAnimation = new ClippedAnimation(strideAnimation, 5, 4);
        
        animator.setAnimation(new Overlay(new Animation[]{background, clipAnimation}));

        animator.displayFrames(10);

        // animator.display.writeToFile("TestStrideClip1.bin");
        assertTrue(animator.display.compareToFile("TestStrideClip1.bin"));
    }

    @Test
    public void testStrideClip2() {
        TestAnimator animator = new TestAnimator(80);
        
        Animation background = new Solid(Color.WHITE);
        Animation strideAnimation = new Animation() {

			@Override
			public Command[] getNextFrame() {
				return new Command[] {new StrideCommand(3, Color.BLACK, 2, 4)};
			}

			@Override
			public int getFrameDelayMilliseconds() {
				return 500;
			}
            
        };
        Animation clipAnimation = new ClippedAnimation(strideAnimation, 1, 5);
        
        animator.setAnimation(new Overlay(new Animation[]{background, clipAnimation}));

        animator.displayFrames(10);

        // animator.display.writeToFile("TestStrideClip2.bin");
        assertTrue(animator.display.compareToFile("TestStrideClip2.bin"));
    }

    @Test
    public void testStrideClip3() {
        TestAnimator animator = new TestAnimator(80);
        
        Animation background = new Solid(Color.WHITE);
        Animation strideAnimation = new Animation() {

			@Override
			public Command[] getNextFrame() {
				return new Command[] {new StrideCommand(1, Color.BLACK, 2, 4)};
			}

			@Override
			public int getFrameDelayMilliseconds() {
				return 500;
			}
            
        };
        Animation clipAnimation = new ClippedAnimation(strideAnimation, 5, 16);
        
        animator.setAnimation(new Overlay(new Animation[]{background, clipAnimation}));

        animator.displayFrames(10);

        // animator.display.writeToFile("TestStrideClip3.bin");
        assertTrue(animator.display.compareToFile("TestStrideClip3.bin"));
    }
}
