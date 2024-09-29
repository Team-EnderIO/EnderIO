package com.enderio.base.api.travel;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;

public interface TravelRenderer<T extends TravelTarget> {
    void render(T travelData, LevelRenderer levelRenderer, PoseStack poseStack, double distanceSquared, boolean active, float partialTick);
}
