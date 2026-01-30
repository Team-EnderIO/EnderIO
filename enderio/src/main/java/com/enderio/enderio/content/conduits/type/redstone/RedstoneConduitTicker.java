package com.enderio.enderio.content.conduits.type.redstone;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.ticker.ConduitTickerBase;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOConduitTypes;
import net.minecraft.server.level.ServerLevel;

public class RedstoneConduitTicker extends ConduitTickerBase<RedstoneConduit> {

    public static final RedstoneConduitTicker INSTANCE = new RedstoneConduitTicker();

    private RedstoneConduitTicker() {}

    @Override
    protected ConduitType<RedstoneConduit> conduitType() {
        return EIOConduitTypes.REDSTONE.get();
    }

    @Override
    public int tickRate() {
        return 2;
    }

    @Override
    protected void tickNetwork(ServerLevel level, ConduitNetwork network, int tickOffset) {
        var context = network.getOrCreateContext(RedstoneConduitNetworkContext.TYPE);
        boolean isActiveBeforeTick = context.isActive();
        context.nextTick();

        boolean didAnyChannelChange = false;
        for (var channel : network.allChannels()) {
            // Receive input signals.
            for (var extractConnection : network.extractConnections(channel)) {
                int signal;

                var redstoneExtractFilter = extractConnection.inventory()
                    .getStackInSlot(RedstoneConduit.EXTRACT_FILTER_SLOT)
                    .getCapability(EnderIOCapabilities.REDSTONE_EXTRACT_FILTER);

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
                didAnyChannelChange = true;
            }
        }

        // If *any* channel changed, update all insert connections.
        // Some insert connections may change even if their channel's signal value did not change.
        if (didAnyChannelChange) {
            for (var insertConnection : network.insertConnections()) {
                level.updateNeighborsAt(insertConnection.node().pos(), EIOBlocks.CONDUIT_BUNDLE.get());

                if (insertConnection.connectionConfig(RedstoneConduitConnectionConfig.TYPE).isStrongOutputSignal()) {
                    level.updateNeighborsAt(insertConnection.connectedBlockPos(), EIOBlocks.CONDUIT_BUNDLE.get());
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
