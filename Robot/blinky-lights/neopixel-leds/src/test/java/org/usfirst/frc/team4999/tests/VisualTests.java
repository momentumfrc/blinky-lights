package org.usfirst.frc.team4999.tests;

import java.awt.GraphicsEnvironment;

import org.junit.Test;
import org.usfirst.frc.team4999.lights.*;
import org.usfirst.frc.team4999.lights.animations.*;
import org.usfirst.frc.team4999.tools.gui.BufferShower;

import static org.usfirst.frc.team4999.tools.CommonTests.guiShowAnimation;

public class VisualTests {

    private static Color[] rainbowcolors = {
        new Color(139, 0, 255),
        Color.BLUE, Color.GREEN,
        Color.YELLOW,
        new Color(255, 127, 0),
        Color.RED
    };

    @Test
    public void testIndexingAnimation() {
        Color[] indexColors = new Color[10];
        for(int i = 0; i < 5; i++) {
            indexColors[i] = Color.WHITE;
        }
        for(int i = 5; i < 10; i++) {
            indexColors[i] = Color.BLACK;
        }

        Animation indexingAnimation = new Solid(indexColors);
        guiShowAnimation(indexingAnimation, 10);
    }

    @Test
    public void testRainbowAnimation() {
        Animation rainbow = new AnimationSequence(new Animation[] {
            Snake.rainbowSnake(70),
            Fade.rainbowFade(100, 20),
            new Bounce(Color.WHITE, rainbowcolors, 50, 50),
            new Stack(rainbowcolors, 50, 40),
            new BounceStack(rainbowcolors, 14, 40) },
            new int[] { 5000, 5000, 10000, 10000, 10000 }
        );
        guiShowAnimation(rainbow, 6000);
    }

    @Test
    public void testRainbowAnimation2() {
        Color[] rainbowcolors = {
            new Color(72, 21, 170),
            new Color(55, 131, 255),
            new Color(77, 233, 76),
            new Color(255, 238, 0),
            new Color(255, 140, 0),
            new Color(246, 0, 0)
        };

        Color[] rainbowTails = ColorTools.getColorTails(rainbowcolors, Color.BLACK, 12, 20);

        Animation rainbow = new AnimationSequence(
            new AnimationSequence.AnimationSequenceMember[] {
                new AnimationSequence.AnimationSequenceMember(
                    new Snake(rainbowTails, 10),
                    5000
                ),
                new AnimationSequence.AnimationSequenceMember(
                    new Snake(ColorTools.getSmearedColors(rainbowcolors, 16), 50),
                    5000
                ),
                new AnimationSequence.AnimationSequenceMember(
                    new BounceStack(ColorTools.getSmearedColors(rainbowcolors, 3), Color.BLACK, 32, 15),
                    8000
                ),
                new AnimationSequence.AnimationSequenceMember(
                    new Fade(rainbowcolors, 100, 20),
                    5000
                )
        });

        guiShowAnimation(rainbow, 4000);
    }

    @Test
    public void testSynchronousAnimator() {
        BufferDisplay display = new BufferDisplay(80);
        CallbackAnimator animator = new CallbackAnimator(display);

        if(GraphicsEnvironment.isHeadless()) {
            return;
        }

        BufferShower gui = new BufferShower();
        BrightnessFilter.setBrightness(1);
        display.addBufferListener(gui);

        Color[] rainbowcolors = {
            new Color(72, 21, 170),
            new Color(55, 131, 255),
            new Color(77, 233, 76),
            new Color(255, 238, 0),
            new Color(255, 140, 0),
            new Color(246, 0, 0)
        };

        Color[] rainbowTails = ColorTools.getColorTails(rainbowcolors, Color.BLACK, 12, 20);

        Animation animation = new Snake(rainbowTails, 10);
        animator.setAnimation(animation);
        for(int i = 0; i < 3000; ++i) {
            animator.animatePeriodic();;
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
