package com.enderio.enderio.api.integration;

@Deprecated(forRemoval = true, since = "8.0.5")
public interface ClientIntegration {

    ClientIntegration NOOP = new ClientIntegration() {};

//    /**
//     * render your hangglider. Only called if {@linkplain Integration#getGliderMovementInfo(Player)} returns a non empty optional
//     */
//    default void renderHangGlider(PoseStack posestack, MultiBufferSource buffer, int light, int overlay, AbstractClientPlayer player, float partialTick) {
//    }
}
