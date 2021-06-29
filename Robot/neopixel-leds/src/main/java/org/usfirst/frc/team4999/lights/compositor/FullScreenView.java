package org.usfirst.frc.team4999.lights.compositor;

import org.usfirst.frc.team4999.lights.animations.Animation;

/**
 * Used by the {@link AnimationCompositor} to show the held
 * {@link org.usfirst.frc.team4999.lights.animations.Animation} over the whole LED strip.
 */
public class FullScreenView implements View {
    private final Animation animation;
    
    public FullScreenView(Animation animation) {
        this.animation = animation;
    }

    @Override
    public Animation getAnimation() {
        return animation;
    }

    @Override
    public boolean hasTransparency() {
        return false;
    }
    
}
