package com.enderio.enderio.mixin;

import com.enderio.enderio.common.conduits.bundle.ConduitBundleBlockEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * This fixes the block breaking overlay when a facade is attached to use the model of the facade. Without this, it will use the model of the conduit, and be invisible in most cases.
 */
@Mixin(BlockRenderDispatcher.class)
public class BlockRenderDispatcherMixin {
    @ModifyVariable(method = "renderBreakingTexture(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/neoforged/neoforge/client/model/data/ModelData;)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private BlockState enderio$getFacade(BlockState state, BlockState argState, BlockPos argPos, BlockAndTintGetter argLevel, PoseStack argPoseStack, VertexConsumer argConsumer, ModelData argModelData) {
        BlockState facadeState = ConduitBundleBlockEntity.FACADES.getOrDefault(argPos.asLong(), null);
        return facadeState == null ? state : facadeState;
    }
}
