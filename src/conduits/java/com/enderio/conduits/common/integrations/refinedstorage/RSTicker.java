package com.enderio.conduits.common.integrations.refinedstorage;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.refinedmods.refinedstorage.api.IRSAPI;
import com.refinedmods.refinedstorage.api.RSAPIInject;
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RSTicker implements ConduitTicker<RSNodeHost> {

    @RSAPIInject
    public static IRSAPI RSAPI;

    public static final RSTicker INSTANCE = new RSTicker();

    @Override
    public void tickGraph(ServerLevel level, ConduitType<RSNodeHost> type, ConduitGraph<RSNodeHost> graph, ColoredRedstoneProvider coloredRedstoneProvider) {
        return; // Do nothing
    }

    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        BlockEntity te = level.getBlockEntity(conduitPos.relative(direction));
        if (te == null)
            return false;

        // check if has network proxy node capability
        return te.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY, direction.getOpposite()).isPresent();
    }

    @Override
    public boolean canConnectTo(ConduitType<?> thisType, ConduitType<?> other) {
        return other instanceof RSConduitType;
    }
}
