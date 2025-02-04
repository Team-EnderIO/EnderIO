package com.enderio.base.client.renderer.glider;

import com.enderio.base.api.integration.ClientIntegration;
import com.enderio.base.api.integration.Integration;
import com.enderio.base.api.integration.IntegrationManager;
import com.enderio.base.common.hangglider.PlayerMovementHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ActiveGliderRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public ActiveGliderRenderLayer(PlayerRenderer pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack posestack, MultiBufferSource pBuffer, int pPackedLight, AbstractClientPlayer player,
            float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw,
            float pHeadPitch) {
        List<ClientIntegration> workingGliders = getActiveGliders(player);
        if (!workingGliders.isEmpty()) { // && PlayerMovementHandler.isGliding(player)) {
            posestack.pushPose();
            posestack.mulPose(Axis.ZP.rotationDegrees(180));
            posestack.translate(0, 0.5, -0.2);
            int overlay = LivingEntityRenderer.getOverlayCoords(player, 0.0F);
            workingGliders.forEach(workingGlider -> workingGlider.renderHangGlider(posestack, pBuffer, pPackedLight,
                    overlay, player, pPartialTick));
            posestack.popPose();
        }
    }

    private static @NotNull List<ClientIntegration> getActiveGliders(Player player) {
        return IntegrationManager.getIf(integration -> integration.getGliderMovementInfo(player).isPresent(),
                Integration::getClientIntegration);
    }

    public static void setupAnim(Player player, PoseStack poseStack) {
        if (getActiveGliders(player).isEmpty() || !PlayerMovementHandler.isGliding(player)) {
            return;
        }
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
