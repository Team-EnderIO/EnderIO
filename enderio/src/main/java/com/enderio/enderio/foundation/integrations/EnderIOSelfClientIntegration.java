package com.enderio.enderio.foundation.integrations;

import com.enderio.enderio.api.integration.ClientIntegration;

public class EnderIOSelfClientIntegration implements ClientIntegration {

    public static final ClientIntegration INSTANCE = new EnderIOSelfClientIntegration();

    // TODO: 1.21.4: Hang gliders lol
//    @Override
//    public void renderHangGlider(PoseStack posestack, MultiBufferSource buffer, int light, int overlay,
//            AbstractClientPlayer player, float partialTick) {
//        Optional<Item> activeGliderItem = EnderIOSelfIntegration.INSTANCE.getActiveGliderItem(player);
//        if (activeGliderItem.isEmpty()) {
//            return;
//        }
//        BakedModel bakedModel = EnderIOClient.GLIDER_MODELS.get(activeGliderItem.get());
//        if (bakedModel == null) {
//            return;
//        }
//
//        posestack.pushPose();
//        posestack.scale(1.5f, 1.5f, 1.5f);
//        posestack.translate(0, -0.6f, 0.7f);
//
//        if (player.isShiftKeyDown()) {
//            posestack.translate(0, 0.05, 0);
//        }
//        Minecraft.getInstance()
//                .getItemRenderer()
//                .render(EIOItems.GLIDER.asItem().getDefaultInstance(), ItemDisplayContext.NONE, false, posestack,
//                        buffer, light, overlay, bakedModel);
//
//        posestack.scale(0.2f, 0.2f, 0.2f);
//        posestack.translate(0, -1f, .5 - 0.06f);
//        posestack.popPose();
//    }

}
