package com.enderio.conduits.client;

import com.enderio.conduits.api.bundle.ConduitBundle;
import com.enderio.conduits.client.model.conduit.facades.ClientFacadeVisibility;
import com.enderio.conduits.client.model.conduit.facades.FacadeUtil;
import com.enderio.conduits.client.particle.ConduitBreakParticle;
import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;

import java.util.Optional;

public class ConduitBundleExtension implements IClientBlockExtensions {

    public static final ConduitBundleExtension INSTANCE = new ConduitBundleExtension();

    private ConduitBundleExtension() {
    }

    @Override
    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
        if (!(target instanceof BlockHitResult blockHitResult)) {
            return false;
        }

        if (level.getBlockEntity(blockHitResult.getBlockPos()) instanceof ConduitBundleBlockEntity conduitBundle) {
            if (conduitBundle.hasFacade() && ClientFacadeVisibility.areFacadesVisible()) {
                return false;
            }

            var conduit = conduitBundle.getShape().getConduit(blockHitResult.getBlockPos(), target);
            if (conduit != null) {
                ConduitBreakParticle.addCrackEffects(blockHitResult.getBlockPos(), state, conduit.value(),
                        blockHitResult.getDirection());
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
        // Use vanilla particles if we have a visible facade.
        // Conduit break particles are handled by the BE.
        if (level.getBlockEntity(pos) instanceof ConduitBundle conduitBundle) {
            return !(conduitBundle.hasFacade() && ClientFacadeVisibility.areFacadesVisible());
        }

        // Not a bundle
        return false;
    }

    @Override
    public boolean playBreakSound(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ConduitBundleBlockEntity conduitBundle) {
            // Get default conduit sound
            var player = Minecraft.getInstance().player;
            SoundType soundType = state.getSoundType(level, pos, player);

            // Get facade sound.
            if (conduitBundle.hasFacade()) {
                var facadeBlock = conduitBundle.getFacadeBlock();
                if (ClientFacadeVisibility.areFacadesVisible()) {
                    soundType = facadeBlock.getSoundType(facadeBlock.defaultBlockState(), level, pos, player);
                }
            }

            level.playSound(player, pos, soundType.getBreakSound(), SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            return true;
        }

        return false;
    }
}
