package org.usfirst.frc.team4999.lights.compositor;

import org.usfirst.frc.team4999.lights.animations.Animation;

/**
 * Used by the {@link AnimationCompositor} to show an animation.
 */
public interface View {
    /**
     * Get the held
     * {@link org.usfirst.frc.team4999.lights.animations.Animation Animation}
     * @return the held animation
     */
    Animation getAnimation();

    /**
     * Indicates if the held animation has any transparency. If the held animation is opaque and
     * covers the entire LED strip, then this should return false.
     * <p>
     * If the held animation is opaque and covers the entire LED strip, then any effort spent
     * rendering animations covered by this animation is wasted. Thus, this method serves as an
     * optimization by indicating whether AnimationCompositor should render animations underneath this
     * animation.
     * @return If the held animation has any transparency.
     */
    boolean hasTransparency();
}
