package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.conduit.ticker.IOAwareConduitTicker;
import com.enderio.api.misc.ColorControl;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiCapabilityAwareConduitTicker<T> implements IOAwareConduitTicker {

    private final BlockCapability<? extends T, Direction>[] capabilities;

    public MultiCapabilityAwareConduitTicker(BlockCapability<? extends T, Direction>[] capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        for (BlockCapability<? extends T, Direction> cap : capabilities) {
            T capability = level.getCapability(cap, conduitPos.relative(direction), direction.getOpposite());
            if (capability != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void tickColoredGraph(ConduitType<?> type, List<Connection> inserts, List<Connection> extracts, ColorControl color, ServerLevel level,
        Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {

        List<CapabilityConnection> insertCaps = new ArrayList<>();
        for (Connection insert : inserts) {
            for (BlockCapability<? extends T, Direction> cap : capabilities) {
                T capability = level.getCapability(cap, insert.move(), insert.dir().getOpposite());
                if (capability != null) {
                    insertCaps.add(new CapabilityConnection(capability, insert.data(), insert.dir()));
                }
            }

        }
        if (!insertCaps.isEmpty()) {
            List<CapabilityConnection> extractCaps = new ArrayList<>();

            for (Connection extract : extracts) {
                for (BlockCapability<? extends T, Direction> cap : capabilities) {
                    T capability = level.getCapability(cap, extract.move(), extract.dir().getOpposite());
                    if (capability != null) {
                        extractCaps.add(new CapabilityConnection(capability, extract.data(), extract.dir()));
                    }
                }
            }
            if (!extractCaps.isEmpty()) {
                tickCapabilityGraph(type, insertCaps, extractCaps, level, graph, isRedstoneActive);
            }
        }
    }

    protected abstract void tickCapabilityGraph(ConduitType<?> type, List<CapabilityConnection> insertCaps, List<CapabilityConnection> extractCaps, ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive);

    public class CapabilityConnection {
        public final T cap;
        public final ExtendedConduitData<?> data;
        public final Direction direction;

        private CapabilityConnection(T cap, ExtendedConduitData<?> data, Direction direction) {
            this.cap = cap;
            this.data = data;
            this.direction = direction;
        }
    }
}
