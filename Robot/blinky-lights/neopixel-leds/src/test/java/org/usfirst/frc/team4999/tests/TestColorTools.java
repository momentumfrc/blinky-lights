package org.usfirst.frc.team4999.tests;

import org.junit.Test;
import org.usfirst.frc.team4999.lights.animations.*;
import org.usfirst.frc.team4999.tools.CommonTests;
import org.usfirst.frc.team4999.lights.*;

import static org.junit.Assert.assertThrows;
import static org.usfirst.frc.team4999.tools.CommonTests.headlessCompareToFile;

public class TestColorTools {
    private static Color[] rainbowcolors = {
        new Color(139, 0, 255),
        Color.BLUE,
        Color.GREEN,
        Color.YELLOW,
        new Color(255, 127, 0),
        Color.RED
    };

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
    public void testColorTails() {
        Animation tails1 = new Solid(ColorTools.getColorTails(
            new Color[] { Color.RED, Color.GREEN, Color.BLUE},
            Color.WHITE, 9, 3
        ));
        headlessCompareToFile(tails1, 10, "ColorTails1");

        Animation tails2 = new Solid(ColorTools.getColorTails(
            new Color[] { Color.RED, Color.BLUE },
            Color.BLACK, 6, 0
        ));
        headlessCompareToFile(tails2, 10, "ColorTails2");

        Animation tails3 = new Solid(ColorTools.getColorTails(
            new Color[] { Color.GREEN, Color.BLUE },
            Color.RED, 0, 5
        ));
        headlessCompareToFile(tails3, 10, "ColorTails3");
    }

}
