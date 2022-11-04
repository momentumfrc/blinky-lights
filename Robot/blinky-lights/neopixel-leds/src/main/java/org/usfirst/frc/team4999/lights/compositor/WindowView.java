package org.usfirst.frc.team4999.lights.compositor;

import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.lights.animations.ClippedAnimation;

/**
 * Used by the {@link AnimationCompositor} to show the held
 * {@link org.usfirst.frc.team4999.lights.animations.Animation} over a subsection of the LED
 * strip.
 */
public class WindowView implements View {
    private final Animation animation;

    public WindowView(Animation animation) {
        this.animation = animation;
    }

    @Override
    public Animation getAnimation() {
        return animation;
    }

    @Override
    public boolean hasTransparency() {
        return true;
    }

    public static WindowView makeClippedWindow(Animation animation, int clipStart, int clipLength) {
        return new WindowView(new ClippedAnimation(animation, clipStart, clipLength));
    }
}
