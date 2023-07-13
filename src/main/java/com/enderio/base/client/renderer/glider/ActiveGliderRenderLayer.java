package com.enderio.base.client.renderer.glider;

import com.enderio.api.integration.ClientIntegration;
import com.enderio.api.integration.Integration;
import com.enderio.api.integration.IntegrationManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ActiveGliderRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public ActiveGliderRenderLayer(PlayerRenderer pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack posestack, MultiBufferSource pBuffer, int pPackedLight, AbstractClientPlayer player, float pLimbSwing, float pLimbSwingAmount,
        float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        List<ClientIntegration> workingGliders = IntegrationManager.getIf(
             integration -> integration.getGliderMovementInfo(player).isPresent(),
            Integration::getClientIntegration);
        if (!workingGliders.isEmpty()) {
            posestack.pushPose();
            posestack.mulPose(Axis.ZP.rotationDegrees(180));
            posestack.translate(0, 0.5, -0.2);
            int overlay = LivingEntityRenderer.getOverlayCoords(player, 0.0F);
            workingGliders.forEach(workingGlider -> workingGlider.renderHangGlider(posestack, pBuffer, pPackedLight, overlay, player, pPartialTick));
            posestack.popPose();
        }
    }
    public static void setupAnim(Player player, PoseStack poseStack) {
        player.oAttackAnim = 0;
        player.attackAnim = 0;
        player.walkAnimation.position(0);
        player.walkAnimation.setSpeed(0);
        player.walkAnimation.update(0, 0);
        poseStack.mulPose(Axis.ZN.rotationDegrees(Mth.clamp(player.yHeadRot - player.yBodyRot, -360, 360)));
        poseStack.mulPose(Axis.XP.rotationDegrees(-100));
        player.xCloakO = 0;
        player.xCloak = 0;
        player.yCloakO = 0;
        player.yCloak = 0;
        player.zCloakO = 0;
        player.zCloak = 0;
    }
}
