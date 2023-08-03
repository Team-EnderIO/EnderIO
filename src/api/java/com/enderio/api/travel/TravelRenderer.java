package com.enderio.api.travel;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;

public interface TravelRenderer<T extends ITravelTarget> {
    void render(T travelData, LevelRenderer levelRenderer, PoseStack poseStack, double distanceSquared);
}