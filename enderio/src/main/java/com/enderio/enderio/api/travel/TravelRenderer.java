package com.enderio.enderio.api.travel;

import com.enderio.core.client.AlwaysOnTopSubmitNodeCollector;
import com.mojang.blaze3d.vertex.PoseStack;

public interface TravelRenderer<T extends TravelTarget> {
    /**
     * @param poseStack Pose relative to the block being rendered
     * @param collector Collector for this target, which renders on top of the level
     */
    void submit(T travelData, PoseStack poseStack, AlwaysOnTopSubmitNodeCollector collector, double distanceSquared, boolean active, float partialTick);
}
