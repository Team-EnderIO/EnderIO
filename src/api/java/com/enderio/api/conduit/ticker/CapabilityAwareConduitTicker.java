package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ColoredRedstoneProvider;
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

public abstract class CapabilityAwareConduitTicker<TType extends ExtendedConduitData<TType>, TCap> implements IOAwareConduitTicker<TType> {

    @Override
    public final void tickColoredGraph(
        ConduitType<TType> type,
        List<Connection<TType>> inserts,
        List<Connection<TType>> extracts,
        ColorControl color,
        ServerLevel level,
        Graph<Mergeable.Dummy> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        List<CapabilityConnection<TType, TCap>> insertCaps = new ArrayList<>();
        for (Connection<TType> insert : inserts) {
            TCap capability = level.getCapability(getCapability(), insert.move(), insert.dir().getOpposite());
            if (capability != null) {
                insertCaps.add(new CapabilityConnection<>(capability, insert.data(), insert.dir(), insert.upgrade(), insert.extractFilter(), insert.insertFilter()));
            }
        }

        if (!insertCaps.isEmpty()) {
            List<CapabilityConnection<TType, TCap>> extractCaps = new ArrayList<>();

            for (Connection<TType> extract : extracts) {
                TCap capability = level.getCapability(getCapability(), extract.move(), extract.dir().getOpposite());
                if (capability != null) {
                    extractCaps.add(new CapabilityConnection<>(capability, extract.data(), extract.dir(), extract.upgrade(), extract.extractFilter(), extract.insertFilter()));
                }
            }
            if (!extractCaps.isEmpty()) {
                tickCapabilityGraph(type, insertCaps, extractCaps, level, graph, coloredRedstoneProvider);
            }
        }
    }

    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        TCap capability = level.getCapability(getCapability(), conduitPos.relative(direction), direction.getOpposite());
        return capability != null;
    }

    protected abstract void tickCapabilityGraph(
        ConduitType<TType> type,
        List<CapabilityConnection<TType, TCap>> inserts,
        List<CapabilityConnection<TType, TCap>> extracts,
        ServerLevel level,
        Graph<Mergeable.Dummy> graph,
        ColoredRedstoneProvider coloredRedstoneProvider);

    protected abstract BlockCapability<TCap,Direction> getCapability();

    public record CapabilityConnection<TType extends ExtendedConduitData<TType>, TCap>(
        TCap capability,
        TType data,
        Direction direction,
        @Nullable
        ConduitUpgrade upgrade,
        @Nullable
        ResourceFilter extractFilter,
        @Nullable
        ResourceFilter insertFilter) {
    }
}
