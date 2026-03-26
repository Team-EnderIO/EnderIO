package com.enderio.enderio.mixin;

import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * This fixes the block breaking overlay when a facade is attached to use the model of the facade. Without this, it will use the model of the conduit, and be invisible in most cases.
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @ModifyArg(method = "submitBlockDestroyAnimation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockStateModelSet;get(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;"))
    private BlockState enderio$getFacade(BlockState state, @Local BlockPos pos) {
        BlockState facadeState = null;

        var currentDimension = Minecraft.getInstance().level.dimension();
        var facadesForDim = ConduitBundleBlockEntity.FACADES.get(currentDimension);
        if (facadesForDim != null) {
            facadeState = facadesForDim.get(pos.asLong());
        }

        return facadeState == null ? state : facadeState;
    }
}
