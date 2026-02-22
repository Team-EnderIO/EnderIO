package com.enderio.enderio.api.travel;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;

public interface TravelRenderer<T extends TravelTarget> {
    /**
     * @param poseStack Pose relative to the block being rendered
     */
    void render(T travelData, LevelRenderer levelRenderer, PoseStack poseStack, double distanceSquared, boolean active, float partialTick);
}
