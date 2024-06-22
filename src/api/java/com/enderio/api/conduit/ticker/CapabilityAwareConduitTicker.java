package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.api.misc.ColorControl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class CapabilityAwareConduitTicker<TData extends ConduitData<TData>, TCap> implements IOAwareConduitTicker<TData> {

    @Override
    public final void tickColoredGraph(ServerLevel level, ConduitType<TData> type, List<Connection<TData>> inserts, List<Connection<TData>> extracts,
        ColorControl color, ConduitGraph<TData> graph, ColoredRedstoneProvider coloredRedstoneProvider) {

        List<CapabilityConnection> insertCaps = new ArrayList<>();
        for (Connection<TData> insert : inserts) {
            Optional
                .ofNullable(level.getBlockEntity(insert.move()))
                .flatMap(b -> b.getCapability(getCapability(), insert.dir().getOpposite()).resolve())
                .ifPresent(cap -> insertCaps.add(new CapabilityConnection(cap, insert.data(), insert.dir(), insert.upgrade(), insert.extractFilter(), insert.insertFilter())));
        }

        if (!insertCaps.isEmpty()) {
            List<CapabilityConnection> extractCaps = new ArrayList<>();

            for (Connection<TData> extract : extracts) {
                Optional
                    .ofNullable(level.getBlockEntity(extract.move()))
                    .flatMap(b -> b.getCapability(getCapability(), extract.dir().getOpposite()).resolve())
                    .ifPresent(cap -> extractCaps.add(new CapabilityConnection(cap, extract.data(), extract.dir(), extract.upgrade(), extract.extractFilter(), extract.insertFilter())));
            }

            if (!extractCaps.isEmpty()) {
                tickCapabilityGraph(level, type, insertCaps, extractCaps, graph, coloredRedstoneProvider);
            }
        }
    }

    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        return Optional
            .ofNullable(level.getBlockEntity(conduitPos.relative(direction)))
            .flatMap(be -> be.getCapability(getCapability(), direction.getOpposite()).resolve())
            .isPresent();
    }

    protected abstract void tickCapabilityGraph(ServerLevel level, ConduitType<TData> type, List<CapabilityConnection> inserts,
        List<CapabilityConnection> extracts, ConduitGraph<TData> graph, ColoredRedstoneProvider coloredRedstoneProvider);

    protected abstract Capability<TCap> getCapability();

    public final class CapabilityConnection {
        public final TCap capability;
        public final TData data;
        public final Direction direction;
        public final @Nullable ConduitUpgrade upgrade;
        public final @Nullable ResourceFilter extractFilter;
        public final @Nullable ResourceFilter insertFilter;

        public CapabilityConnection(TCap capability, TData data, Direction direction, @Nullable ConduitUpgrade upgrade, @Nullable ResourceFilter extractFilter,
            @Nullable ResourceFilter insertFilter) {
            this.capability = capability;
            this.data = data;
            this.direction = direction;
            this.upgrade = upgrade;
            this.extractFilter = extractFilter;
            this.insertFilter = insertFilter;
        }
    }
}
