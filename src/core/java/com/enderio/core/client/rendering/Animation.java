package com.enderio.core.client.rendering;

import net.minecraft.util.Mth;

public class Animation {
    private float start;
    private float target;
    private float current;
    private float currentDurationTick;
    private float totalDurationTick;

    public Animation(float start, float target, float totalDurationTick) throws Exception {
        this.start = start;
        this.target = target;
        if (totalDurationTick <= 0.0f)
            throw new Exception("totalDurationTick cannot be less than zero!");
        this.totalDurationTick = totalDurationTick;

        current = start;
        currentDurationTick = 0.0f;
    }

    public void updateByPartialTick(float partialTick) {
        currentDurationTick += partialTick;
        current = Mth.lerp(currentDurationTick / totalDurationTick, start, target);
        if (start >= target) {
            current = Mth.clamp(current, target, start);
        } else {
            current = Mth.clamp(current, start, target);
        }
    }

    public float getCurrent() {
        return current;
    }

    public float getTarget() {
        return target;
    }

    public boolean isComplete() {
        return Mth.abs(current - target) <= Mth.EPSILON;
    }
}
