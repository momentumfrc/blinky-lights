package org.usfirst.frc.team4999.tests;

import org.junit.Test;
import org.usfirst.frc.team4999.tools.*;
import org.usfirst.frc.team4999.lights.BrightnessFilter;
import org.usfirst.frc.team4999.lights.BufferDisplay;
import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.animations.*;
import org.usfirst.frc.team4999.lights.compositor.AnimationCompositor;


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

        var background_view = coord.getOpaqueView(background, 1);
        an.displayFrames(50);

        var green_section_view = coord.getTransparentView(new ClippedAnimation(new Solid(Color.GREEN), 15, 30), 10);
        an.displayFrames(50);

        Animation rainbow = Snake.rainbowSnake(50);
        var rainbow_snake_view = coord.getOpaqueView(rainbow, 100);
        an.displayFrames(50);

        Animation fader = new Fade(new Color[] {Color.RED, Color.YELLOW, Color.BLUE}, 1000, 100);
        var fader_view = coord.getTransparentView(new ClippedAnimation(fader, 5, 25), 500);
        an.displayFrames(50);

        green_section_view.hide();;
        var green_section_view_2 = coord.getTransparentView(new ClippedAnimation(new Solid(Color.GREEN), 15, 30), 700);
        an.displayFrames(50);

        background_view.changeAnimation(new Solid(Color.RED));
        an.displayFrames(50);


        fader_view.hide();
        green_section_view_2.hide();
        rainbow_snake_view.hide();
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
        AnimationCompositor.View solid_blue_view = coord.getOpaqueView(solid_blue, 1);
        an.displayFrames(6);

        Animation solid_rainbow = Solid.rainbow();
        AnimationCompositor.View solid_rainbow_view = coord.getOpaqueView(solid_rainbow, 10);
        an.displayFrames(6);

        Animation solid_green = new Solid(Color.GREEN);
        solid_blue_view.hide();
        AnimationCompositor.View solid_green_view = coord.getOpaqueView(solid_green, 1);

        Animation stack = new Stack(10, 200, Color.GREEN, Color.BLUE, Color.RED);
        AnimationCompositor.View stack_view = coord.getOpaqueView(stack, 5);
        an.displayFrames(6);

        Animation rainbow_overlay = new ClippedAnimation(Snake.rainbowSnake(200), 5, 25);
        AnimationCompositor.View rainbow_overlay_view = coord.getTransparentView(rainbow_overlay, 20);
        an.displayFrames(25);

        solid_rainbow_view.hide();
        an.displayFrames(25);

        stack_view.hide();
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
        AnimationCompositor.View solid_blue_view = coord.getOpaqueView(solid_blue, 1);
        an.displayFrames(6);

        Animation rainbow_overlay = new ClippedAnimation(Snake.rainbowSnake(200), 10, 40);
        AnimationCompositor.View rainbow_overlay_view = coord.getOpaqueView(rainbow_overlay, 10);
        an.displayFrames(25);

        //an.display.writeToFile("CoordinatorFalseTransparency");
        comparator.compareToFile("CoordinatorFalseTransparency");
    }
}
