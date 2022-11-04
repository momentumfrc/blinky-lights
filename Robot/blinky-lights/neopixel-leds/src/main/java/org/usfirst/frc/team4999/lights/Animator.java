package org.usfirst.frc.team4999.lights;

import org.usfirst.frc.team4999.lights.animations.Animation;

public interface Animator {
    /**
     * Set the animation to show
     * @param newAnimation The animation to show
     */
    public void setAnimation(Animation newAnimation);
}
