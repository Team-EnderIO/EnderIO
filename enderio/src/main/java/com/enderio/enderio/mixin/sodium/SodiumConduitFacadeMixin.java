package com.enderio.enderio.mixin.sodium;

import com.enderio.enderio.client.content.conduits.model.facades.ClientFacadeVisibility;
import com.enderio.enderio.compat.ModCompatHelper;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Makes Sodium render an opaque full-block conduit facade in place of the conduit bundle: the facade
 * goes through Sodium's own BlockRenderer, so it gets Iris block ids (shader shading), correct lighting
 * and connected textures for free - instead of being drawn separately via {@code AddSectionGeometryEvent}
 * (which never receives an Iris block id and therefore renders too bright under shaders, and lets conduit
 * connections poke through the cover). Transparent facades keep the existing separate render path.
 */
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer", remap = false)
public class SodiumConduitFacadeMixin {

    @Nullable
    private static BlockState enderio$opaqueFacadeAt(BlockPos pos) {
        // Reaching this method means the injection applied; let the overlay path defer to us.
        ModCompatHelper.markSodiumFacadeMixinActive();

        if (!ClientFacadeVisibility.areFacadesVisible()) {
            return null;
        }

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return null;
        }

        Long2ObjectMap<BlockState> facadesForDim = ConduitBundleBlockEntity.FACADES.get(level.dimension());
        if (facadesForDim == null) {
            return null;
        }

        BlockState facade = facadesForDim.get(pos.asLong());
        if (facade != null && facade.canOcclude()) {
            return facade;
        }

        return null;
    }

    @ModifyVariable(method = "renderModel", at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false)
    private BlockState enderio$mirrorState(BlockState state, BakedModel model, BlockState stateArg, BlockPos pos) {
        BlockState facade = enderio$opaqueFacadeAt(pos);
        return facade != null ? facade : state;
    }

    @ModifyVariable(method = "renderModel", at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false)
    private BakedModel enderio$mirrorModel(BakedModel model, BakedModel modelArg, BlockState state, BlockPos pos) {
        BlockState facade = enderio$opaqueFacadeAt(pos);
        if (facade != null) {
            return Minecraft.getInstance().getBlockRenderer().getBlockModel(facade);
        }
        return model;
    }
}
