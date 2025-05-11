package com.enderio.base.mixin;

import com.enderio.base.client.renderer.glider.ActiveGliderRenderLayer;
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

    @Inject(method = "setupRotations", at = @At(value = "RETURN"), cancellable = true, remap = false)
    public void test(LivingEntity entity, PoseStack poseStack, float bob, float yBodyRot, float partialTick,
            float scale, CallbackInfo ci) {
        if (entity instanceof Player player) {
            ActiveGliderRenderLayer.setupAnim(player, poseStack);
        }
    }
}
