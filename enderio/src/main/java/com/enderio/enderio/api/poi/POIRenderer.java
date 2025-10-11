package com.enderio.enderio.api.poi;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;

public interface POIRenderer<T extends EnderPOI> {
    void render(T pOIData, LevelRenderer levelRenderer, PoseStack poseStack, double distanceSquared, boolean active, float partialTick);
}
