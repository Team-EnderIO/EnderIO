package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.filter.ResourceFilter;
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
import java.util.function.Predicate;

public abstract class CapabilityAwareConduitTicker<T> implements IOAwareConduitTicker {

    @Override
    public final void tickColoredGraph(ConduitType<?> type, List<Connection> inserts, List<Connection> extracts, ColorControl color, ServerLevel level,
        Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        List<CapabilityConnection> insertCaps = new ArrayList<>();
        for (Connection insert : inserts) {
            T capability = level.getCapability(getCapability(), insert.move(), insert.dir().getOpposite());
            if (capability != null) {
                insertCaps.add(new CapabilityConnection(capability, insert.data(), insert.dir(), insert.upgrade(), insert.extractFilter(), insert.insertFilter()));
            }

        }
        if (!insertCaps.isEmpty()) {
            List<CapabilityConnection> extractCaps = new ArrayList<>();

            for (Connection extract : extracts) {
                T capability = level.getCapability(getCapability(), extract.move(), extract.dir().getOpposite());
                if (capability != null) {
                    extractCaps.add(new CapabilityConnection(capability, extract.data(), extract.dir(), extract.upgrade(), extract.extractFilter(), extract.insertFilter()));
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

    // TODO: Record?
    public final class CapabilityConnection {
        public final T cap;
        public final ExtendedConduitData<?> data;
        public final Direction direction;
        @Nullable
        public final ConduitUpgrade upgrade;
        @Nullable
        public final ResourceFilter extractFilter;
        @Nullable
        public final ResourceFilter insertFilter;

        private CapabilityConnection(
            T cap,
            ExtendedConduitData<?> data,
            Direction direction,
            @Nullable ConduitUpgrade upgrade,
            @Nullable ResourceFilter extractFilter,
            @Nullable ResourceFilter insertFilter) {
            this.cap = cap;
            this.data = data;
            this.direction = direction;
            this.upgrade = upgrade;
            this.extractFilter = extractFilter;
            this.insertFilter = insertFilter;
        }
    }
}
