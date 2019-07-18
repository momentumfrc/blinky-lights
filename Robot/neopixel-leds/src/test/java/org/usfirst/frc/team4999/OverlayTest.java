package org.usfirst.frc.team4999;

import org.junit.Test;
import org.usfirst.frc.team4999.lights.AnimationCoordinator;
import org.usfirst.frc.team4999.lights.Animator;
import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.animations.*;


public class OverlayTest {

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFader() {
        SwingDisplay display = new SwingDisplay(80);

        Animation fader = new ClippedAnimation(new Fade(new Color[] {Color.RED, Color.YELLOW, Color.BLUE}, 1000, 100), 5, 25);
        Animation background = Snake.rainbowSnake(50);

        Animation overlayed = new Overlay(new Animation[] {background, fader});

        Animator an = new Animator(display);

        an.setAnimation(overlayed);

        display.waitForClose();

        an.stopAnimation();
    }

    @Test
    public void testCoordinator() {
        SwingDisplay display = new SwingDisplay(80);
        Animator an = new Animator(display);
        AnimationCoordinator coord = new AnimationCoordinator(an);

        Animation background = new BounceStack(new Color[] {Color.MOMENTUM_PURPLE, Color.MOMENTUM_PURPLE, Color.MOMENTUM_BLUE, Color.MOMENTUM_BLUE}, 8, 40);

        coord.setBase(background);
        sleep(5000);

        Animation greenSection = new ClippedAnimation(new Solid(Color.GREEN), 15, 30);
        coord.pushAnimation("Green Section", greenSection, true);
        sleep(5000);

        Animation rainbow = Snake.rainbowSnake(50);
        coord.pushAnimation("RainbowSnake", rainbow, false);
        sleep(5000);

        Animation fader = new ClippedAnimation(new Fade(new Color[] {Color.RED, Color.YELLOW, Color.BLUE}, 1000, 100), 5, 25);
        coord.pushAnimation("Fader", fader, true);
        sleep(5000);

        coord.popAnimation("Green Section");
        coord.pushAnimation("Green Section", greenSection, true);
        sleep(5000);

        coord.setBase(new Solid(Color.RED));
        sleep(5000);

        coord.popAnimation("Fader");
        coord.popAnimation("Green Section");
        coord.popAnimation("RainbowSnake");
        sleep(5000);

        an.stopAnimation();
    }

    @Test
    public void testClippedAnimation() {
        SwingDisplay display = new SwingDisplay(80);
        Animator an = new Animator(display);
        
        Animation base = Snake.rainbowSnake(300);
        Animation fade = new Fade(new Color[] {Color.RED, Color.YELLOW, Color.BLUE}, 2000, 0);
        Animation clippedFade = new ClippedAnimation(fade, 0, 30);
        Animation solid = new Solid(new Color[] {Color.GREEN, Color.BLUE});
        Animation clippedSolid = new ClippedAnimation(solid, 50, 20);

        Animation overlay = new Overlay(new Animation[] {base, clippedFade, clippedSolid});

        an.setAnimation(overlay);

        display.waitForClose();

        an.stopAnimation();
    }
}
