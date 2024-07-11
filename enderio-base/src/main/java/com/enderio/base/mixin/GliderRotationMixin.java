package com.enderio.base.mixin;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntityRenderer.class)
public class GliderRotationMixin {

    // TODO: NEO-PORT: Glider mixin
    /*@Inject(method = "setupRotations",
            at = @At(
                value = "INVOKE",
                target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V",
                ordinal = 0, shift = At.Shift.AFTER),
            remap = false)
    public void enderio$rotatePlayerInGlider(LivingEntity pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks, CallbackInfo ci) {
        if (pEntityLiving instanceof Player player && PlayerMovementHandler.calculateGliderMovementInfo(player, false).isPresent()) {
            ActiveGliderRenderLayer.setupAnim(player, pMatrixStack);
        }
    }*/
}
