package org.usfirst.frc.team4999.lights.compositor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.usfirst.frc.team4999.lights.Animator;
import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.lights.animations.Overlay;
import org.usfirst.frc.team4999.lights.animations.Solid;

/**
 * Composes component {@link View}s into one {@link org.usfirst.frc.team4999.lights.animations.Animation},
 * and sends the composed Animation to the {@link org.usfirst.frc.team4999.lights.Animator}.
 */
public class AnimationCompositor {

    /**
     * The animation which will be shown if no other View occupies that part of the LED strip.
     */
    private static final Animation base = new Solid(Color.BLACK);

    private static class ViewHolder implements Comparable<ViewHolder> {
        public final String key;
        public final View view;
        public final int z_idx;
        
        public ViewHolder(String key, View view, int z_idx) {
            this.key = key;
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
                return ovh.key.equals(key)
                    && ovh.view.equals(ovh)
                    && ovh.z_idx == z_idx;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(key, view, z_idx);
        }
    }

    private Animator animator;

    private HashMap<String, ViewHolder> animationTable;

    // NOTE: The animationStack is ordered with highest z_idx first
    private TreeSet<ViewHolder> animationStack;

    public AnimationCompositor(Animator animator) {
        animationTable = new HashMap<>();
        animationStack = new TreeSet<>();

        this.animator = animator;

        updateAnimator();
    }

    public AnimationCompositor() {
        this(null);
    }

    /**
     * Show a view in the compositor on top of all other views
     * @param key The key referring to this view
     * @param view The view to show
     */
    public void showView(String key, View view) {
        showView(key, view, animationStack.first().z_idx + 1);
    }

    /**
     * Show a view in the compositor.
     * <p>
     * The z_idx value controls the vertical stacking of overlapping views. Views with
     * a higher z_idx will be shown over views with a lower z_idx. NOTE: The order of 
     * overlapping views with equal z_idx values is undefined.
     * @param key The key referring to this view
     * @param view The view to show
     * @param z_idx The value controlling the vertical stacking of overlapping views
     */
    public void showView(String key, View view, int z_idx) {
        if(animationTable.containsKey(key))
            hideView(key);
        ViewHolder holder = new ViewHolder(key, view, z_idx);
        animationTable.put(key, holder);
        animationStack.add(holder);

        updateAnimator();
    }

    /**
     * Remove a view from the compositor.
     * @param key The key referring to the view to remove
     */
    public void hideView(String key) {
        if(!animationTable.containsKey(key))
            return;
        ViewHolder holder = animationTable.get(key);
        animationTable.remove(key);
        animationStack.remove(holder);

        updateAnimator();
    }

    /**
     * Get an Animation showing the composited Views. The resulting Animation is ready to be passed
     * to the {@link org.usfirst.frc.team4999.lights.Animator}
     * @return The composited Animation
     */
    public Animation getCurrentAnimation() {
        ArrayList<Animation> visibleAnimations = new ArrayList<>();
        
        for(ViewHolder vh : animationStack) {
            visibleAnimations.add(vh.view.getAnimation());
            if(!vh.view.hasTransparency())
                break;
        }

        visibleAnimations.add(base);

        Animation[] animationsArray = new Animation[visibleAnimations.size()];
        for(int i = 0; i < animationsArray.length; i++) {
            animationsArray[i] = visibleAnimations.get(animationsArray.length - 1 - i);
        }

        return new Overlay(animationsArray);
    }

    public void updateAnimator() {
        if(animator != null) {
            animator.setAnimation(getCurrentAnimation());
        }
    }

    /**
     * Get a shown view
     * @param key The key referring to the view
     * @return The view
     */
    public View getView(String key) {
        return animationTable.get(key).view;
    }

}
