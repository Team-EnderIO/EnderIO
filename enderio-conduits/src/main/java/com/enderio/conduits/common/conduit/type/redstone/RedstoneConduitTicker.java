package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.conduits.api.ConduitCapabilities;
import com.enderio.conduits.api.network.IConduitNetwork;
import com.enderio.conduits.api.ticker.ConduitTicker;
import com.enderio.conduits.common.init.ConduitBlocks;
import net.minecraft.server.level.ServerLevel;

public class RedstoneConduitTicker implements ConduitTicker<RedstoneConduit> {

    public static final RedstoneConduitTicker INSTANCE = new RedstoneConduitTicker();

    @Override
    public void tick(ServerLevel level, RedstoneConduit conduit, IConduitNetwork network) {
        var context = network.getOrCreateContext(RedstoneConduitNetworkContext.TYPE);
        boolean isActiveBeforeTick = context.isActive();
        context.nextTick();

        for (var channel : network.allChannels()) {
            // Receive input signals.
            for (var extractConnection : network.extractConnections(channel)) {
                int signal;

                var redstoneExtractFilter = extractConnection.inventory()
                        .getStackInSlot(RedstoneConduit.EXTRACT_FILTER_SLOT)
                        .getCapability(ConduitCapabilities.REDSTONE_EXTRACT_FILTER);

                if (redstoneExtractFilter != null) {
                    signal = redstoneExtractFilter.getInputSignal(level, extractConnection.connectedBlockPos(),
                            extractConnection.connectionSide());
                } else {
                    signal = level.getSignal(extractConnection.connectedBlockPos(), extractConnection.connectionSide());
                }

                if (signal > 0) {
                    context.setSignal(channel, signal);
                }
            }

            // Fire block updates if the signal changed.
            if (context.isNew() || context.getSignal(channel) != context.getSignalLastTick(channel)) {

                for (var insertConnection : network.insertConnections(channel)) {
                    level.updateNeighborsAt(insertConnection.node().pos(), ConduitBlocks.CONDUIT.get());

                    if (insertConnection.connectionConfig(RedstoneConduitConnectionConfig.TYPE).isStrongOutputSignal()) {
                        level.updateNeighborsAt(insertConnection.connectedBlockPos(), ConduitBlocks.CONDUIT.get());
                    }
                }
            }
        }

        // Mark all nodes as dirty if the active state changes (update block models).
        if (context.isNew() || context.isActive() != isActiveBeforeTick) {
            for (var node : network.tickingNodes()) {
                node.markDirty();
            }
        }
    }

}
