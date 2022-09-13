package org.usfirst.frc.team4999.tests;

import org.junit.Test;
import org.usfirst.frc.team4999.lights.animations.*;
import org.usfirst.frc.team4999.lights.*;

import static org.junit.Assert.assertThrows;
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
        headlessCompareToFile(blink, 10, "BlinkAnimation");
    }

    @Test
    public void testBounceAnimation() {
        Animation bounce = new Bounce(Color.WHITE, rainbowcolors, 45, 20);
        headlessCompareToFile(bounce, 250, "BounceAnimation");
    }

    @Test
    public void testBounceStackAnimation()  {
        Animation bounceStack = new BounceStack(rainbowcolors, 14, 20);
        headlessCompareToFile(bounceStack, 250, "BounceStackAnimation");
    }

    @Test
    public void testFadeAnimation() {
        Animation fade = new Fade(rainbowcolors, 300, 300);
        headlessCompareToFile(fade, 600, "FadeAnimation");
    }

    @Test
    public void testSnakeAnimation() {
        Animation snake = Snake.twoColorSnake(Color.BLUE, Color.WHITE, 1, 5, 10, 20);
        headlessCompareToFile(snake, 150, "TwoColorSnakeAnimation");
    }

    @Test
    public void testSolidAnimation() {
        Animation solid = new Solid(rainbowcolors);
        headlessCompareToFile(solid, 4, "SolidAnimation");
    }

    @Test
    public void testStackAnimation() {
        Animation stack = new Stack(rainbowcolors, 25, 40);
        headlessCompareToFile(stack, 625, "StackAnimation");
    }

    @Test
    public void testOverlayAnimation() {
        Animation bouncer = new ClippedAnimation(new BounceStack(new Color[]{Color.BLACK, Color.BLACK, Color.BLACK}, 14, 20), 5, 25);
        Animation background = Snake.rainbowSnake(60);

        Animation overlayed = new Overlay(new Animation[] {background, bouncer});

        headlessCompareToFile(overlayed, 250, "OverlayAnimation");
    }

    @Test
    public void testClippedAnimation() {
        Animation base = Snake.rainbowSnake(300);
        Animation fade = new Fade(new Color[] {Color.RED, Color.YELLOW, Color.BLUE}, 2000, 0);
        Animation clippedFade = new ClippedAnimation(fade, 0, 30);
        Animation solid = new Solid(new Color[] {Color.GREEN, Color.BLUE});
        Animation clippedSolid = new ClippedAnimation(solid, 50, 20);

        Animation overlay = new Overlay(new Animation[] {base, clippedFade, clippedSolid});
        headlessCompareToFile(overlay, 300, "ClippedAnimation");
    }

    @Test
    public void testGradientThrows() {
        assertThrows(
            "Missing initial gradient stop",
            IllegalArgumentException.class,
            () -> {
                ColorTools.getGradient(5, new ColorTools.GradientStop[] {
                    new ColorTools.GradientStop(1, Color.BLACK),
                    new ColorTools.GradientStop(5, Color.WHITE)
                });
            }
        );

        assertThrows(
            "Gradient stop pos is out of bounds",
            IllegalArgumentException.class,
            () -> {
                ColorTools.getGradient(5, new ColorTools.GradientStop[] {
                    new ColorTools.GradientStop(-1, Color.BLACK),
                    new ColorTools.GradientStop(4, Color.WHITE)
                });
            }
        );

        assertThrows(
            "Gradient stop pos is out of bounds",
            IllegalArgumentException.class,
            () -> {
                ColorTools.getGradient(3, new ColorTools.GradientStop[] {
                    new ColorTools.GradientStop(1, Color.BLACK),
                    new ColorTools.GradientStop(5, Color.WHITE)
                });
            }
        );

        assertThrows(
            "Gradient stop positions must always increase",
            IllegalArgumentException.class,
            () -> {
                ColorTools.getGradient(5, new ColorTools.GradientStop[] {
                    new ColorTools.GradientStop(0, Color.BLACK),
                    new ColorTools.GradientStop(3, Color.BLUE),
                    new ColorTools.GradientStop(2, Color.RED),
                    new ColorTools.GradientStop(4, Color.WHITE)
                });
            }
        );
    }

    @Test
    public void testGradientAnimation() {
        Animation gradient1 = new Solid(ColorTools.getGradient(
            5,
            new ColorTools.GradientStop[] {
                new ColorTools.GradientStop(0, Color.BLACK),
                new ColorTools.GradientStop(4, Color.WHITE)
            }
        ));
        headlessCompareToFile(gradient1, 10, "GradientAnimation1");

        Animation gradient2 = new Solid(ColorTools.getGradient(
            10,
            new ColorTools.GradientStop[] {
                new ColorTools.GradientStop(0, Color.BLACK),
                new ColorTools.GradientStop(5, Color.WHITE)
            }
        ));
        headlessCompareToFile(gradient2, 10, "GradientAnimation2");
    }

    @Test
    public void testBlurredSolidAnimation() {
        Animation gradient3 = new Solid(ColorTools.getSmearedColors(rainbowcolors, 8));
        headlessCompareToFile(gradient3, 10, "SmearAnimation1");

        Animation gradient4 = new Snake(ColorTools.getSmearedColors(rainbowcolors, 20), 50);
        headlessCompareToFile(gradient4, 180, "SmearAnimation2");
    }

    @Test
    public void testAnimationSequence() {
        Animation solid = new Solid(new Color[] { Color.RED, Color.GREEN, Color.BLUE});
        Animation fade = new Fade(new Color[] { Color.RED, Color.GREEN, Color.BLUE}, 1000, 0);

        Animation sequence = new AnimationSequence(
            new AnimationSequence.AnimationSequenceMember[] {
                new AnimationSequence.AnimationSequenceMember(solid, 1000),
                new AnimationSequence.AnimationSequenceMember(fade, 4000)
            }
        );

        headlessCompareToFile(sequence, 300, "AnimationSequence1");
    }

    /*
    CommonTests.TestSteps guiSteps = CommonTests.makeShowGUITestSteps(
        gradient3,
        10
    );
    CommonTests.TestSteps saveSteps = CommonTests.makeSaveToFileTestSteps("GradientAnimation3");
    CommonTests.Test test = new CommonTests.Test(new CommonTests.TestSteps[] {
        guiSteps, saveSteps
    });
    test.run();
    */
}
