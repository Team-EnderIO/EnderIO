package com.enderio.conduits.mixin;

import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * This fixes the block breaking overlay when a facade is attached to use the model of the facade. Without this, it will use the model of the conduit, and be invisible in most cases.
 */
@Mixin(BlockRenderDispatcher.class)
public class BlockRenderDispatcherMixin {
    @WrapOperation(method = "renderBreakingTexture(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/neoforged/neoforge/client/model/data/ModelData;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockModelShaper;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/BakedModel;"))
    public BakedModel enderio$checkFacades(BlockModelShaper instance, BlockState state, Operation<BakedModel> original,
            BlockState localState, BlockPos pos, BlockAndTintGetter level) {
        BlockState facadeState = ConduitBundleBlockEntity.FACADES.getOrDefault(pos.asLong(), null);

        return original.call(instance, facadeState == null ? state : facadeState);
    }
}
