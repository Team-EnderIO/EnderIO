package com.enderio.enderio.mixin;

import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * This fixes the block breaking overlay when a facade is attached to use the model of the facade. Without this, it will use the model of the conduit, and be invisible in most cases.
 */
@Mixin(BlockRenderDispatcher.class)
public class BlockRenderDispatcherMixin {
    @ModifyVariable(method = "renderBreakingTexture", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private BlockState enderio$getFacade(BlockState state, BlockState argState, BlockPos argPos, BlockAndTintGetter argLevel, PoseStack argPoseStack, VertexConsumer argConsumer) {
        BlockState facadeState = ConduitBundleBlockEntity.FACADES.getOrDefault(argPos.asLong(), null);
        return facadeState == null ? state : facadeState;
    }
}
