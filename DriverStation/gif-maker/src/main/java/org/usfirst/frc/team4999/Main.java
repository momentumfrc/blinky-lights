package org.usfirst.frc.team4999;

import java.io.IOException;

import org.usfirst.frc.team4999.lights.BrightnessFilter;
import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.animations.*;

public class Main {

    private static Color[] rainbowcolors = {
        new Color(139, 0, 255),
        Color.BLUE,
        Color.GREEN,
        Color.YELLOW,
        new Color(255, 127, 0),
        Color.RED
    };

    public static void main(String[] args) {
        BrightnessFilter.setBrightness(1);
        Animation bounce = new Bounce(Color.WHITE, rainbowcolors, 40, 20);
        GifAnimator animator = new GifAnimator(bounce, 40, "gifs\\Bounce.gif", 80);
        try {
            animator.renderFrames(80, true);
        } catch (IOException e) {
			e.printStackTrace();
		}
    }
}
