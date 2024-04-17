package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.misc.ColorControl;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class CapabilityAwareConduitTicker<T> implements IOAwareConduitTicker {

    @Override
    public final void tickColoredGraph(ConduitType<?> type, List<Connection> inserts, List<Connection> extracts, ColorControl color, ServerLevel level,
        Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        List<CapabilityConnection> insertCaps = new ArrayList<>();
        for (Connection insert : inserts) {
            T capability = level.getCapability(getCapability(), insert.move(), insert.dir().getOpposite());
            if (capability != null) {
                insertCaps.add(new CapabilityConnection(capability, insert.data(), insert.dir(), insert.connectionState()));
            }

        }
        if (!insertCaps.isEmpty()) {
            List<CapabilityConnection> extractCaps = new ArrayList<>();

            for (Connection extract : extracts) {
                T capability = level.getCapability(getCapability(), extract.move(), extract.dir().getOpposite());
                if (capability != null) {
                    extractCaps.add(new CapabilityConnection(capability, extract.data(), extract.dir(), extract.connectionState()));
                }
            }
            if (!extractCaps.isEmpty()) {
                tickCapabilityGraph(type, insertCaps, extractCaps, level, graph, isRedstoneActive);
            }
        }
    }

    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        T capability = level.getCapability(getCapability(), conduitPos.relative(direction), direction.getOpposite());
        return capability != null;
    }

    protected abstract void tickCapabilityGraph(ConduitType<?> type, List<CapabilityConnection> inserts, List<CapabilityConnection> extracts,
        ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive );

    protected abstract BlockCapability<T,Direction> getCapability();

    public class CapabilityConnection {
        public final T cap;
        public final ExtendedConduitData<?> data;
        public final Direction direction;
        @Nullable
        public final DynamicConnectionState connectionState;

        private CapabilityConnection(T cap, ExtendedConduitData<?> data, Direction direction, @Nullable DynamicConnectionState connectionState) {
            this.cap = cap;
            this.data = data;
            this.direction = direction;
            this.connectionState = connectionState;
        }
    }
}
