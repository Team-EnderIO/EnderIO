package com.enderio.base.common.integrations;

import com.enderio.base.api.integration.ClientIntegration;
import com.enderio.base.client.EnderIOBaseClient;
import com.enderio.base.common.init.EIOItems;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;

public class EnderIOSelfClientIntegration implements ClientIntegration {

    public static final ClientIntegration INSTANCE = new EnderIOSelfClientIntegration();

    @Override
    public void renderHangGlider(PoseStack posestack, MultiBufferSource buffer, int light, int overlay,
            AbstractClientPlayer player, float pPartialTick) {
        Optional<Item> activeGliderItem = EnderIOSelfIntegration.INSTANCE.getActiveGliderItem(player);
        if (activeGliderItem.isEmpty()) {
            return;
        }
        BakedModel bakedModel = EnderIOBaseClient.GLIDER_MODELS.get(activeGliderItem.get());
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
