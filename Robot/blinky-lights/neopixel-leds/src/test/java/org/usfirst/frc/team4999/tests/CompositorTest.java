package org.usfirst.frc.team4999.tests;

import org.junit.Test;
import org.usfirst.frc.team4999.tools.*;
import org.usfirst.frc.team4999.lights.BrightnessFilter;
import org.usfirst.frc.team4999.lights.BufferDisplay;
import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.animations.*;
import org.usfirst.frc.team4999.lights.compositor.AnimationCompositor;
import org.usfirst.frc.team4999.lights.compositor.FullScreenView;
import org.usfirst.frc.team4999.lights.compositor.View;
import org.usfirst.frc.team4999.lights.compositor.WindowView;


public class CompositorTest {

    @Test
    public void testCoordinator() {
        BrightnessFilter.setBrightness(1);

        BufferDisplay display = new BufferDisplay(80);
        PixelComparator comparator = new PixelComparator();
        display.addBufferListener(comparator);

        TestAnimator an = new TestAnimator(display);

        AnimationCompositor coord = new AnimationCompositor(an);

        Animation background = new BounceStack(new Color[] {Color.MOMENTUM_PURPLE, Color.MOMENTUM_PURPLE, Color.MOMENTUM_BLUE, Color.MOMENTUM_BLUE}, 8, 40);

        coord.hideView("DNE Animation");

        coord.showView("Background", new FullScreenView(background), 1);
        an.displayFrames(50);

        View greenWindow = new WindowView(new ClippedAnimation(new Solid(Color.GREEN), 15, 30));
        coord.showView("Green Section", greenWindow, 10);
        an.displayFrames(50);

        Animation rainbow = Snake.rainbowSnake(50);
        coord.showView("RainbowSnake", new FullScreenView(rainbow), 100);
        an.displayFrames(50);

        Animation fader = new Fade(new Color[] {Color.RED, Color.YELLOW, Color.BLUE}, 1000, 100);
        coord.showView("Fader", WindowView.makeClippedWindow(fader, 5, 25), 500);
        an.displayFrames(50);

        coord.hideView("Green Section");
        coord.showView("Green Section", greenWindow, 700);
        an.displayFrames(50);

        coord.hideView("Background");
        coord.showView("Background", new FullScreenView(new Solid(Color.RED)), 1);
        an.displayFrames(50);

        coord.hideView("DNE Animation");

        coord.hideView("Fader");
        coord.hideView("Green Section");
        coord.hideView("RainbowSnake");
        an.displayFrames(10);

        //an.display.writeToFile("Coordinator");
        comparator.compareToFile("Coordinator");
    }

    @Test
    public void testCoordinatorPriority() {
        BrightnessFilter.setBrightness(1);

        BufferDisplay display = new BufferDisplay(80);
        PixelComparator comparator = new PixelComparator();
        display.addBufferListener(comparator);

        TestAnimator an = new TestAnimator(display);

        AnimationCompositor coord = new AnimationCompositor(an);
        an.displayFrames(6);

        Animation solid_blue = new Solid(Color.BLUE);
        coord.showView("Solid Blue", new FullScreenView(solid_blue), 1);
        an.displayFrames(6);

        Animation solid_rainbow = Solid.rainbow();
        coord.showView("Rainbow", new FullScreenView(solid_rainbow), 10);
        an.displayFrames(6);

        Animation solid_green = new Solid(Color.GREEN);
        coord.hideView("Solid Blue");
        coord.showView("Solid Green", new FullScreenView(solid_green), 1);

        Animation stack = new Stack(new Color[]{Color.GREEN, Color.BLUE, Color.RED}, 10, 200);
        coord.showView("Stack", new FullScreenView(stack), 5);
        an.displayFrames(6);

        Animation rainbow_overlay = new ClippedAnimation(Snake.rainbowSnake(200), 5, 25);
        coord.showView("Rainbow overlay", new WindowView(rainbow_overlay), 20);
        an.displayFrames(25);

        coord.hideView("Rainbow");
        an.displayFrames(25);

        coord.hideView("Stack");
        an.displayFrames(25);

        //an.display.writeToFile("CoordinatorPriorities");
        comparator.compareToFile("CoordinatorPriorities");
    }

    @Test
    public void testCoordinatorFalseTransparency() {
        BrightnessFilter.setBrightness(1);

        BufferDisplay display = new BufferDisplay(80);
        PixelComparator comparator = new PixelComparator();
        display.addBufferListener(comparator);

        TestAnimator an = new TestAnimator(display);

        AnimationCompositor coord = new AnimationCompositor(an);
        an.displayFrames(6);

        Animation solid_blue = new Solid(Color.BLUE);
        coord.showView("Solid Blue", new FullScreenView(solid_blue), 1);
        an.displayFrames(6);

        Animation rainbow_overlay = new ClippedAnimation(Snake.rainbowSnake(200), 10, 40);
        coord.showView("Rainbow Overlay", new FullScreenView(rainbow_overlay), 10);
        an.displayFrames(25);

        //an.display.writeToFile("CoordinatorFalseTransparency");
        comparator.compareToFile("CoordinatorFalseTransparency");
    }
}
