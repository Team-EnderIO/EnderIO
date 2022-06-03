package com.enderio.base.mixin.client;

import com.enderio.base.client.renderer.item.IItemOverlayRender;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

// TODO: We'll get rid of this mixin one day, waiting on a forge feature.
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(at = @At("HEAD"), method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V")
    public void renderGuiItemDecorations(Font pFr, ItemStack pStack, int pXPosition, int pYPosition, @Nullable String pText, CallbackInfo callbackInfo) {
        if (!pStack.isEmpty()) {
            if (pStack.getItem() instanceof IItemOverlayRender item) {
                PoseStack poseStack = new PoseStack();
                poseStack.translate(pXPosition, pYPosition, asThis().blitOffset);
                item.renderOverlay(pStack, pXPosition, pYPosition, poseStack);
            }
        }
    }
    public ItemRenderer asThis() {
        return (ItemRenderer) (Object) this;
    }
}
