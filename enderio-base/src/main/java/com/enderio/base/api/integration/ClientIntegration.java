package com.enderio.base.api.integration;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;

public interface ClientIntegration {

    ClientIntegration NOOP = new ClientIntegration() {};

    /**
     * render your hangglider. Only called if {@linkplain Integration#getGliderMovementInfo(Player)} returns a non empty optional
     */
    default void renderHangGlider(PoseStack posestack, MultiBufferSource buffer, int light, int overlay, AbstractClientPlayer player, float pPartialTick) {
    }
}
