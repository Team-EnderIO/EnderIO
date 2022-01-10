package com.enderio.base.client.renderer.teleportation;

import com.enderio.base.common.handler.travel.ITravelTarget;
import com.enderio.base.common.util.API;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;

@API
public interface TeleportationRenderer<T extends ITravelTarget> {

    void render(T travelData, LevelRenderer levelRenderer, PoseStack poseStack);
}
