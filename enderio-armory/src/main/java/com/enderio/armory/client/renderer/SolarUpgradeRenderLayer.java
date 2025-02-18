package com.enderio.armory.client.renderer;

import com.enderio.armory.common.item.darksteel.upgrades.solar.SolarUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.solar.SolarUpgradeHandler;
import com.enderio.machines.common.init.MachineBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SolarUpgradeRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public SolarUpgradeRenderLayer(
            RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight, AbstractClientPlayer player,
            float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw,
            float pHeadPitch) {

        Optional<SolarUpgrade> solarUpgradeOpt = SolarUpgradeHandler.getUpgrade(player);
        if (solarUpgradeOpt.isEmpty()) {
            return;
        }
        ItemStack solItem = MachineBlocks.SOLAR_PANELS.get(solarUpgradeOpt.get().getPanelTier()).toStack();

        poseStack.pushPose();
        getParentModel().getHead().translateAndRotate(poseStack);
        translateToTopOfHead(poseStack);
        Minecraft.getInstance()
                .getEntityRenderDispatcher()
                .getItemInHandRenderer()
                .renderItem(player, solItem, ItemDisplayContext.HEAD, false, poseStack, pBuffer, pPackedLight);
        poseStack.popPose();
    }

    private void translateToTopOfHead(PoseStack poseStack) {
        poseStack.translate(0.0F, -0.25F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(0.625F, -0.625F, -0.625F);
        poseStack.translate(0.0F, 1, 0.0F);
    }
}
