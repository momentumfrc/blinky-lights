package org.usfirst.frc.team4999.tests;

import org.junit.Test;
import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.commands.*;
import org.usfirst.frc.team4999.lights.commands.StrideCommand;
import org.usfirst.frc.team4999.lights.animations.ClippedAnimation;
import org.usfirst.frc.team4999.lights.animations.Overlay;
import org.usfirst.frc.team4999.lights.animations.Solid;
import org.usfirst.frc.team4999.lights.animations.Animation;

import static org.usfirst.frc.team4999.tools.CommonTests.headlessCompareToFile;

public class ClipTests {

    @Test
    public void testStrideClip1() {
        Animation background = new Solid(Color.WHITE);
        Animation strideAnimation = new Animation() {

			@Override
			public Command[] getNextFrame() {
				return new Command[] {new StrideCommand(0, Color.BLACK, 2, 4).dim()};
			}

			@Override
			public int getFrameDelayMilliseconds() {
				return 500;
			}
            
        };
        Animation clipAnimation = new ClippedAnimation(strideAnimation, 5, 4);
        Animation overlay = new Overlay(new Animation[]{background, clipAnimation});

        headlessCompareToFile(overlay, 10, "TestStrideClip1");
    }

    @Test
    public void testStrideClip2() {
        Animation background = new Solid(Color.WHITE);
        Animation strideAnimation = new Animation() {

			@Override
			public Command[] getNextFrame() {
				return new Command[] {new StrideCommand(3, Color.BLACK, 2, 4).dim()};
			}

			@Override
			public int getFrameDelayMilliseconds() {
				return 500;
			}
            
        };
        Animation clipAnimation = new ClippedAnimation(strideAnimation, 1, 5);
        Animation overlay = new Overlay(new Animation[]{background, clipAnimation});

        headlessCompareToFile(overlay, 10, "TestStrideClip2");
    }

    @Test
    public void testStrideClip3() {
        Animation background = new Solid(Color.WHITE);
        Animation strideAnimation = new Animation() {

			@Override
			public Command[] getNextFrame() {
				return new Command[] {new StrideCommand(1, Color.BLACK, 2, 4).dim()};
			}

			@Override
			public int getFrameDelayMilliseconds() {
				return 500;
			}
            
        };
        Animation clipAnimation = new ClippedAnimation(strideAnimation, 5, 16);
        Animation overlay = new Overlay(new Animation[]{background, clipAnimation});

        headlessCompareToFile(overlay, 10, "TestStrideClip3");
    }
}
