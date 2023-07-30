package com.enderio.base.mixin;

import com.enderio.base.client.renderer.block.EnderSkullRenderer;
import com.enderio.base.common.block.EnderSkullBlock;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

//Needed to stop MC from crashing when showing our skull on an entity
@Mixin(SkullBlockRenderer.class)
abstract public class SkullBlockRendererMixin implements BlockEntityRenderer<SkullBlockEntity> {

    @Inject(method = "lambda$static$0", at = @At("TAIL"))
    private static void addSkullType(HashMap<SkullBlock.Type, ResourceLocation> skinByType, CallbackInfo ci) {
        skinByType.put(EnderSkullBlock.EIOSkulls.ENDERMAN, new ResourceLocation("textures/entity/enderman/enderman.png"));
    }

    @Inject(method = "createSkullRenderers", at = @At("RETURN"), cancellable = true)
    private static void addModel(EntityModelSet entityModelSet, CallbackInfoReturnable<Map<SkullBlock.Type, SkullModelBase>> cir) {
        ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> builder = ImmutableMap.builder();
        builder.putAll(cir.getReturnValue());
        builder.put(EnderSkullBlock.EIOSkulls.ENDERMAN, new EnderSkullRenderer.EnderSkullModel(entityModelSet.bakeLayer(EnderSkullRenderer.ENDER_SKULL)));
        cir.setReturnValue(builder.build());
    }

}
