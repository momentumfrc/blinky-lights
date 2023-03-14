package org.usfirst.frc.team4999.lights.compositor;

import java.util.*;

import org.usfirst.frc.team4999.lights.Animator;
import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.lights.animations.Overlay;
import org.usfirst.frc.team4999.lights.animations.Solid;

/**
 * Composes component {@link View Views} into one
 * {@link org.usfirst.frc.team4999.lights.animations.Animation Animation},
 * and sends the composed Animation to the
 * {@link org.usfirst.frc.team4999.lights.Animator Animator}.
 */
public class AnimationCompositor {
    /**
     * The animation which will be shown if no other View occupies that part of the LED strip.
     */
    private static final Animation BASE_ANIMATION = new Solid(Color.BLACK);

    /**
     * A view represents a layer in the animation compositor stack. Every view holds an animation and, when
     * enabled, displays that animation at its associated layer.
     * <br>
     * Views are considered either "transparent" or "opaque" depending on the held animation. If a view is opaque, its
     * held animation is expected to fill all pixels on the LED strip, and any views with a lower z-index priority will
     * not be rendered. The converse is true if the view is transparent.
     * <br>
     * The expected use of this class is for context-sensitive animations in robot code. A subsystem should get a view
     * from the animation compositor when the subsystem is instantiated. Then, whenever the correct context is detected,
     * the subsystem will call the {@link #show()} method to show the view's associated animation. Likewise, when the
     * correct context is not present, the subsystem will call the {@link #hide()} method to hide the view's associated
     * animation.
     */
    public class View {
        private Animation displayedAnimation;
        public final boolean hasTransparency;

        private boolean isVisible = true;
        private View(Animation displayedAnimation, boolean hasTransparency){
            this.displayedAnimation = displayedAnimation;
            this.hasTransparency = hasTransparency;
        }

        public boolean getIsVisible() {
            return isVisible;
        }

        public void show() {
            if(isVisible)
                return;
            isVisible = true;
            updateAnimator();
        }

        public void hide() {
            if(!isVisible)
                return;
            isVisible = false;
            updateAnimator();
        }

        public Animation getDisplayedAnimation() {
            return displayedAnimation;
        }

        public void changeAnimation(Animation animation) {
            this.displayedAnimation = animation;
            updateAnimator();
        }
    }

    private static class ViewHolder implements Comparable<ViewHolder> {
        public final View view;
        public final int z_idx;

        public ViewHolder(View view, int z_idx) {
            this.view = view;
            this.z_idx = z_idx;
        }

        @Override
        public int compareTo(ViewHolder o) {
            return o.z_idx - z_idx;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof ViewHolder) {
                ViewHolder ovh = (ViewHolder) o;
                return ovh.view.equals(view)
                    && ovh.z_idx == z_idx;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(view, z_idx);
        }
    }

    private final Optional<Animator> animator;

    // NOTE: The animationStack is ordered with highest z_idx first
    private final TreeSet<ViewHolder> animationStack = new TreeSet<>();
    private final HashMap<Integer, ViewHolder> viewsByZIndex = new HashMap<>();

    private int defaultZIndex = 1;

    /**
     * Instantiate an AnimationCompositor with an associated Animator. The AnimationCompositor will automatically
     * propagate changes to the LEDs via the associated Animator.
     */
    public AnimationCompositor(Animator animator) {
        this.animator = Optional.of(animator);

        updateAnimator();
    }

    /**
     * Instantiate an AnimationCompositor without an associated Animator.
     * <br>
     * NOTE: AnimationCompositors not associated with an animator will not automatically propagate changes
     * to the LEDs. The caller is expected to use {@link #getCurrentAnimation()} whenever an animation changes.
     */
    public AnimationCompositor() {
        this.animator = Optional.empty();
    }

    /**
     * Get an opaque view for an animation at a default z-index.
     * <br>
     * NOTE: the default z-index is an incrementing integer starting at 1. Be sure not to create conflicts with
     * manually defined z-indices.
     * <br>
     * Refer to {@link #getView(Animation, int, boolean)} for more details.
     * @param animation The animation to show
     * @return The view
     */
    public View getOpaqueView(Animation animation) {
        return getOpaqueView(animation, defaultZIndex++);
    }

    /**
     * Get a transparent view for an animation at a default z-index.
     * <br>
     * NOTE: the default z-index is an incrementing integer starting at 1. Be sure not to create conflicts with
     * manually defined z-indices.
     * <br>
     * Refer to {@link #getView(Animation, int, boolean)} for more details.
     * @param animation The animation to show
     * @return The view
     */
    public View getTransparentView(Animation animation) {
        return getTransparentView(animation, defaultZIndex++);
    }

    /**
     * Get an opaque view for an animation at a specified z-index.
     * <br>
     * Refer to {@link #getView(Animation, int, boolean)} for more details.
     * @param animation The animation to show
     * @param zIndex The z-index
     * @return The view
     */
    public View getOpaqueView(Animation animation, int zIndex) {
        return getView(animation, zIndex, false);
    }

    /**
     * Get a transparent view for an animation at a specified z-index.
     * <br>
     * Refer to {@link #getView(Animation, int, boolean)} for more details.
     * @param animation The animation to show
     * @param zIndex The z-index
     * @return The view
     */
    public View getTransparentView(Animation animation, int zIndex) {
        return getView(animation, zIndex, true);
    }

    /**
     * Get a view associated with this compositor at the specified z-index.
     * <br>
     * NOTE: Views are unique per z-index. If conflicting z-indices are detected, the more recent view will
     * be kept and the older view will be discarded.
     * <br>
     * Refer to {@link AnimationCompositor.View} for more details about views.
     * @see AnimationCompositor.View
     * @param animation The animation to display in the view
     * @param zIndex The z-index priority of the view in the animation stack
     * @param isTransparent Whether the animation is expected to fill all pixels
     * @return The view holding the specified animation at the specified z-index
     */
    public View getView(Animation animation, int zIndex, boolean isTransparent) {
        if(viewsByZIndex.containsKey(zIndex)) {
            viewsByZIndex.remove(zIndex);
            animationStack.removeIf(it -> it.z_idx == zIndex);
        }
        View view = new View(animation, isTransparent);
        ViewHolder holder = new ViewHolder(view, zIndex);
        animationStack.add(holder);
        viewsByZIndex.put(zIndex, holder);
        updateAnimator();
        return view;
    }


    /**
     * Get an Animation showing the composited Views. The resulting Animation is ready to be passed
     * to the {@link org.usfirst.frc.team4999.lights.AsyncAnimator}
     * @return The composited Animation
     */
    public Animation getCurrentAnimation() {
        ArrayList<Animation> visibleAnimations = new ArrayList<>();

        boolean showBase = true;
        for(ViewHolder vh : animationStack) {
            if(vh.view.getIsVisible()) {
                visibleAnimations.add(vh.view.getDisplayedAnimation());
                if(!vh.view.hasTransparency) {
                    showBase = false;
                    break;
                }
            }
        }

        visibleAnimations.add(BASE_ANIMATION);

        Animation[] animationsArray = new Animation[visibleAnimations.size()];
        for(int i = 0; i < animationsArray.length; i++) {
            animationsArray[i] = visibleAnimations.get(animationsArray.length - 1 - i);
        }

        return new Overlay(animationsArray);
    }

    private void updateAnimator() {
        animator.ifPresent(value -> value.setAnimation(getCurrentAnimation()));
    }
}
