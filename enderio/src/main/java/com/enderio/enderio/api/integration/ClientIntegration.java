package com.enderio.enderio.api.integration;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;

@Deprecated(forRemoval = true, since = "8.0.5")
public interface ClientIntegration {

    ClientIntegration NOOP = new ClientIntegration() {};

    /**
     * render your hangglider. Only called if {@linkplain Integration#getGliderMovementInfo(Player)} returns a non empty optional
     */
    // 26.2-port: MultiBufferSource was removed; the hang glider integration is disabled
    default void renderHangGlider(PoseStack posestack, Object buffer, int light, int overlay, AbstractClientPlayer player, float partialTick) {
    }
}
