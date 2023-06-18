package com.enderio.base.mixin;

import com.enderio.base.client.renderer.glider.ActiveGliderRenderLayer;
import com.enderio.base.common.hangglider.PlayerMovementHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class GliderRotationMixin {

    @Inject(method = "setupRotations",
            at = @At(
                value = "INVOKE",
                target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V",
                ordinal = 0, shift = At.Shift.AFTER))
    public void enderio$rotatePlayerInGlider(LivingEntity pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks, CallbackInfo ci) {
        if (pEntityLiving instanceof Player player && PlayerMovementHandler.calculateGliderMovementInfo(player, false).isPresent()) {
            ActiveGliderRenderLayer.setupAnim(player, pMatrixStack);
        }
    }
}
