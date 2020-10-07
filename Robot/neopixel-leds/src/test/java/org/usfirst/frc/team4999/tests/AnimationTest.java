package org.usfirst.frc.team4999.tests;

import org.junit.Test;
import org.usfirst.frc.team4999.lights.animations.*;
import org.usfirst.frc.team4999.lights.*;


import static org.usfirst.frc.team4999.tools.CommonTests.headlessCompareToFile;


public class AnimationTest {

    private static Color[] rainbowcolors = {
        new Color(139, 0, 255),
        Color.BLUE,
        Color.GREEN,
        Color.YELLOW,
        new Color(255, 127, 0),
        Color.RED
    };

    @Test
    public void testBlinkAnimation() {
        Animation blink = new Blink(rainbowcolors, 500);
        headlessCompareToFile(blink, 10, "BlinkAnimation.bin");
    }

    @Test
    public void testBounceAnimation() {
        Animation bounce = new Bounce(Color.WHITE, rainbowcolors, 45, 20);
        headlessCompareToFile(bounce, 250, "BounceAnimation.bin");
    }

    @Test
    public void testBounceStackAnimation()  {
        Animation bounceStack = new BounceStack(rainbowcolors, 14, 20);
        headlessCompareToFile(bounceStack, 250, "BounceStackAnimation.bin");
    }

    @Test
    public void testFadeAnimation() {
        Animation fade = new Fade(rainbowcolors, 300, 300);
        headlessCompareToFile(fade, 600, "FadeAnimation.bin");
    }

    @Test
    public void testSnakeAnimation() {
        Animation snake = Snake.twoColorSnake(Color.BLUE, Color.WHITE, 1, 5, 10, 20);
        headlessCompareToFile(snake, 150, "TwoColorSnakeAnimation.bin");
    }

    @Test
    public void testSolidAnimation() {
        Animation solid = new Solid(rainbowcolors);
        headlessCompareToFile(solid, 4, "SolidAnimation.bin");
    }

    @Test
    public void testStackAnimation() {
        Animation stack = new Stack(rainbowcolors, 25, 40);
        headlessCompareToFile(stack, 625, "StackAnimation.bin");
    }

    @Test
    public void testOverlayAnimation() {
        Animation bouncer = new ClippedAnimation(new BounceStack(new Color[]{Color.BLACK, Color.BLACK, Color.BLACK}, 14, 20), 5, 25);
        Animation background = Snake.rainbowSnake(60);

        Animation overlayed = new Overlay(new Animation[] {background, bouncer});

        headlessCompareToFile(overlayed, 250, "OverlayAnimation.bin");
    }

    @Test
    public void testClippedAnimation() {
        Animation base = Snake.rainbowSnake(300);
        Animation fade = new Fade(new Color[] {Color.RED, Color.YELLOW, Color.BLUE}, 2000, 0);
        Animation clippedFade = new ClippedAnimation(fade, 0, 30);
        Animation solid = new Solid(new Color[] {Color.GREEN, Color.BLUE});
        Animation clippedSolid = new ClippedAnimation(solid, 50, 20);

        Animation overlay = new Overlay(new Animation[] {base, clippedFade, clippedSolid});
        headlessCompareToFile(overlay, 300, "ClippedAnimation.bin");
    }
}
