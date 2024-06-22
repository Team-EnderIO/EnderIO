package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.IOAwareConduitTicker;
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

public abstract class MultiCapabilityAwareConduitTicker<TType extends ConduitData<TType>, TCap> implements IOAwareConduitTicker<TType> {

    private final Capability<? extends TCap>[] capabilities;

    public MultiCapabilityAwareConduitTicker(Capability<? extends TCap>[] capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        for (Capability<? extends TCap> cap : capabilities) {
            boolean hasCap = Optional.ofNullable(level.getBlockEntity(conduitPos.relative(direction)))
                .flatMap(be -> be.getCapability(cap, direction.getOpposite()).resolve())
                .isPresent();

            if (hasCap) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void tickColoredGraph(
        ServerLevel level,
        ConduitType<TType> type,
        List<Connection<TType>> inserts,
        List<Connection<TType>> extracts,
        ColorControl color,
        ConduitGraph<TType> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        List<CapabilityConnection<TType, TCap>> insertCaps = new ArrayList<>();
        for (Connection<TType> insert : inserts) {
            for (Capability<? extends TCap> cap : capabilities) {
                Optional
                    .ofNullable(level.getBlockEntity(insert.move()))
                    .flatMap(b -> b.getCapability(cap, insert.dir().getOpposite()).resolve())
                    .ifPresent(c -> insertCaps.add(new CapabilityConnection<>(c, insert.data(), insert.dir(), insert.upgrade(), insert.extractFilter(), insert.insertFilter())));
            }

        }
        if (!insertCaps.isEmpty()) {
            List<CapabilityConnection<TType, TCap>> extractCaps = new ArrayList<>();

            for (Connection<TType> extract : extracts) {
                for (Capability<? extends TCap> cap : capabilities) {
                    Optional
                        .ofNullable(level.getBlockEntity(extract.move()))
                        .flatMap(b -> b.getCapability(cap, extract.dir().getOpposite()).resolve())
                        .ifPresent(c -> extractCaps.add(new CapabilityConnection<>(c, extract.data(), extract.dir(), extract.upgrade(), extract.extractFilter(), extract.insertFilter())));
                }
            }
            if (!extractCaps.isEmpty()) {
                tickCapabilityGraph(type, insertCaps, extractCaps, level, graph, coloredRedstoneProvider);
            }
        }
    }

    protected abstract void tickCapabilityGraph(
        ConduitType<TType> type,
        List<CapabilityConnection<TType, TCap>> insertCaps,
        List<CapabilityConnection<TType, TCap>> extractCaps,
        ServerLevel level,
        ConduitGraph<TType> graph,
        ColoredRedstoneProvider coloredRedstoneProvider);

    public record CapabilityConnection<TType extends ConduitData<TType>, TCap>(
        TCap capability,
        TType data,
        Direction direction,
        @Nullable ConduitUpgrade upgrade,
        @Nullable ResourceFilter extractFilter,
        @Nullable ResourceFilter insertFilter) {
    }
}
