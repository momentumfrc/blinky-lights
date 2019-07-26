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


        sleep(5000);

        display.close();

        an.stopAnimation();
    }

    @Test
    public void testCoordinator() {
        SwingDisplay display = new SwingDisplay(80);
        Animator an = new Animator(display);
        AnimationCoordinator coord = new AnimationCoordinator(an);

        Animation background = new BounceStack(new Color[] {Color.MOMENTUM_PURPLE, Color.MOMENTUM_PURPLE, Color.MOMENTUM_BLUE, Color.MOMENTUM_BLUE}, 8, 40);

        coord.popAnimation("DNE Animation");

        coord.pushAnimation("Background", background, 1, false);
        sleep(2000);

        Animation greenSection = new ClippedAnimation(new Solid(Color.GREEN), 15, 30);
        coord.pushAnimation("Green Section", greenSection, 10, true);
        sleep(2000);

        Animation rainbow = Snake.rainbowSnake(50);
        coord.pushAnimation("RainbowSnake", rainbow, 100, false);
        sleep(2000);

        Animation fader = new ClippedAnimation(new Fade(new Color[] {Color.RED, Color.YELLOW, Color.BLUE}, 1000, 100), 5, 25);
        coord.pushAnimation("Fader", fader, 500, true);
        sleep(2000);

        coord.popAnimation("Green Section");
        coord.pushAnimation("Green Section", greenSection, 700, true);
        sleep(2000);

        coord.popAnimation("Background");
        coord.pushAnimation("Background", new Solid(Color.RED), 1, false);
        sleep(2000);

        coord.popAnimation("DNE Animation");

        coord.popAnimation("Fader");
        coord.popAnimation("Green Section");
        coord.popAnimation("RainbowSnake");
        sleep(2000);

        display.close();
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

        sleep(5000);

        display.close();
        an.stopAnimation();
    }

    @Test
    public void testAnimationCoordinatorPriority() {
        SwingDisplay display = new SwingDisplay(80);
        Animator an = new Animator(display);
        AnimationCoordinator coord = new AnimationCoordinator(an);
        sleep(2000);

        Animation solid_blue = new Solid(Color.BLUE);
        coord.pushAnimation("Solid Blue", solid_blue, 1, false);
        sleep(2000);

        Animation solid_rainbow = Solid.rainbow();
        coord.pushAnimation("Rainbow", solid_rainbow, 10, false);
        sleep(2000);

        Animation solid_green = new Solid(Color.GREEN);
        coord.pushAnimation("Solid Green", solid_green, 1, false);

        Animation stack = new Stack(new Color[]{Color.GREEN, Color.BLUE, Color.RED}, 10, 200);
        coord.pushAnimation("Stack", stack, 5, false);
        sleep(2000);
        
        Animation rainbow_overlay = new ClippedAnimation(Snake.rainbowSnake(200), 5, 25);
        coord.pushAnimation("Rainbow overlay", rainbow_overlay, 20, true);
        sleep(2000);

        coord.popAnimation("Rainbow");
        sleep(2000);

        coord.popAnimation("Stack");
        sleep(2000);


        display.close();
        an.stopAnimation();
    }

    @Test
    public void testAnimationCoordinatorFalseTransparency() {
        SwingDisplay display = new SwingDisplay(80);
        Animator an = new Animator(display);
        AnimationCoordinator coord = new AnimationCoordinator(an);
        sleep(2000);

        Animation solid_blue = new Solid(Color.BLUE);
        coord.pushAnimation("Solid Blue", solid_blue, 1, true);
        sleep(2000);

        Animation rainbow_overlay = new ClippedAnimation(Snake.rainbowSnake(200), 10, 40);
        coord.pushAnimation("Rainbow Overlay", rainbow_overlay, 10, false);
        sleep(2000);

        display.close();
        an.stopAnimation();
    }
}
