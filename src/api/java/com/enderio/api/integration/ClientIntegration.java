package com.enderio.api.integration;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;

public interface ClientIntegration {

    ClientIntegration NOOP = new ClientIntegration() {};

    /**
     * render your hangglider. Only called if {@link Integration#canUseHangGlider(Player)} returns true
     */
    default void renderHangGlider(PoseStack posestack, MultiBufferSource buffer, int light, int overlay, AbstractClientPlayer player, float pPartialTick) {
    }
}
