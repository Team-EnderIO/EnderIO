package com.enderio.armory.client.renderer;

import com.enderio.base.api.integration.ClientIntegration;
import com.enderio.base.client.EnderIOBaseClient;
import com.enderio.base.common.init.EIOItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

public class GliderIntegrationClient implements ClientIntegration {

    public static final GliderIntegrationClient INSTANCE = new GliderIntegrationClient();

    @Override
    public void renderHangGlider(PoseStack posestack, MultiBufferSource buffer, int light, int overlay,
            AbstractClientPlayer player, float pPartialTick) {

        BakedModel bakedModel = EnderIOBaseClient.GLIDER_MODELS.get(EIOItems.GLIDER.asItem());
        if (bakedModel == null) {
            return;
        }

        posestack.pushPose();
        posestack.scale(1.5f, 1.5f, 1.5f);
        posestack.translate(0, -0.6f, 0.7f);

        if (player.isShiftKeyDown()) {
            posestack.translate(0, 0.05, 0);
        }
        Minecraft.getInstance()
                .getItemRenderer()
                .render(EIOItems.GLIDER.asItem().getDefaultInstance(), ItemDisplayContext.NONE, false, posestack,
                        buffer, light, overlay, bakedModel);

        posestack.scale(0.2f, 0.2f, 0.2f);
        posestack.translate(0, -1f, .5 - 0.06f);
        posestack.popPose();
    }

}
