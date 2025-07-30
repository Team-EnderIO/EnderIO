package com.enderio.conduits.integration.ftb_ultimine;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.client.model.conduit.facades.FacadeUtil;
import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.enderio.conduits.common.init.ConduitBlocks;
import dev.ftb.mods.ftbultimine.api.blockselection.BlockSelectionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public enum ConduitBlockSelectionHandler implements BlockSelectionHandler {
    INSTANCE;

    @Override
    public Result customSelectionCheck(Player player, BlockPos origPos, BlockPos pos, BlockState origState, BlockState state) {
        var level = player.level();

        var origBlockEntity = level.getBlockEntity(origPos);
        if (!state.is(ConduitBlocks.CONDUIT) ||
            !(origBlockEntity instanceof ConduitBundleBlockEntity origConduitBundle)) {
            return Result.PASS;
        }

        var blockEntity = level.getBlockEntity(pos);
        if (!state.is(ConduitBlocks.CONDUIT) ||
            !(blockEntity instanceof ConduitBundleBlockEntity conduitBundle)) {
            return Result.PASS;
        }

        if (origConduitBundle.hasFacade() && FacadeUtil.areFacadesVisible(player)) {
            if (!conduitBundle.hasFacade()) {
                return Result.FALSE;
            }

            // We will be aiming at a facade
            if (origConduitBundle.getFacadeBlock().equals(conduitBundle.getFacadeBlock())) {
                return Result.TRUE;
            }

            return Result.FALSE;
        }

        // Find the aimed conduit
        HitResult hitResult = player.pick(player.blockInteractionRange() + 5, 1, false);

        // We're breaking a conduit, make sure the network is shared
        Holder<Conduit<?, ?>> conduit = origConduitBundle.getShape().getConduit(origPos, hitResult);
        if (conduit == null) {
            return Result.FALSE;
        }

        if (!conduitBundle.hasConduitStrict(conduit)) {
            return Result.FALSE;
        }

        var origNetwork = origConduitBundle.getConduitNode(conduit).getNetwork();
        var network = conduitBundle.getConduitNode(conduit).getNetwork();
        if (origNetwork == network) {
            return Result.TRUE;
        }

        return Result.FALSE;
    }
}
