package com.enderio.core.client.rendering;

import net.minecraft.util.Mth;

// TODO: Support more interpolation methods, if required.
/**
 * Class for animating between two values over an amount of time, using
 * {@link Mth.lerp}.
 */
public class Animation {
    private float start;
    private float target;
    private float current;
    private float currentDurationTick;
    private float totalDurationTick;

    /**
     * Default constructor for Animation class.
     * 
     * @param start             Initial value to animate from. Can be negative.
     * @param target            Target value to animate to. Can be negative.
     * @param totalDurationTick Total amount of time for the animation to complete,
     *                          measured in Minecraft ticks. Must be more than zero.
     * @throws Exception Thrown when the time is less than or equal to zero.
     */
    public Animation(float start, float target, float totalDurationTick) throws Exception {
        this.start = start;
        this.target = target;
        if (totalDurationTick <= 0.0f)
            throw new Exception("totalDurationTick cannot be less than zero!");
        this.totalDurationTick = totalDurationTick;

        current = start;
        currentDurationTick = 0.0f;
    }

    /**
     * Advance the animation by the specified amount of time.
     * 
     * @param partialTick Amount of time to advance the animation, measured in
     *                    partial Minecraft ticks.
     */
    public void updateByPartialTick(float partialTick) {
        // Advance the timer, then figure out the current value using lerp.
        currentDurationTick += partialTick;
        current = Mth.lerp(currentDurationTick / totalDurationTick, start, target);

        // Make sure the current value is clamped between the start and target.
        if (start >= target) {
            current = Mth.clamp(current, target, start);
        } else {
            current = Mth.clamp(current, start, target);
        }
    }

    /**
     * Get the current value of the animation, which will be between the start and
     * target.
     */
    public float getCurrent() {
        return current;
    }

    /**
     * Get the start value of the animation.
     */
    public float getStart() {
        return start;
    }

    /**
     * Get the target value of the animation.
     */
    public float getTarget() {
        return target;
    }

    /**
     * Gets whether this animation has completed.
     */
    public boolean isComplete() {
        // Check difference with epsilon, because floating point precision is fun.
        return Mth.abs(current - target) <= Mth.EPSILON;
    }
}
