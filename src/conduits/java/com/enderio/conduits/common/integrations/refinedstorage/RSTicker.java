package com.enderio.conduits.common.integrations.refinedstorage;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.misc.ColorControl;
import com.refinedmods.refinedstorage.api.IRSAPI;
import com.refinedmods.refinedstorage.api.RSAPIInject;
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.function.TriFunction;

public class RSTicker implements IConduitTicker {

    @RSAPIInject
    public static IRSAPI RSAPI;

    public static final RSTicker INSTANCE = new RSTicker();

    @Override
    public void tickGraph(IConduitType<?> type, Graph<Mergeable.Dummy> graph, ServerLevel level,
        TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
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
    public boolean canConnectTo(IConduitType<?> thisType, IConduitType<?> other) {
        return other instanceof RSConduitType;
    }
}
