package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.IExtendedConduitData;
import com.enderio.api.misc.ColorControl;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class CapabilityAwareConduitTicker<T> implements IIOAwareConduitTicker {

    @Override
    public final void tickColoredGraph(IConduitType<?> type, List<Connection> inserts, List<Connection> extracts, ColorControl color, ServerLevel level,
        Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        List<CapabilityConnection> insertCaps = new ArrayList<>();
        for (Connection insert : inserts) {
            BlockEntity blockEntity = level.getBlockEntity(insert.move());
            if (blockEntity != null) {
                LazyOptional<T> capability = blockEntity.getCapability(getCapability(), insert.dir().getOpposite());
                try {
                    capability.resolve().ifPresent(cap -> insertCaps.add(new CapabilityConnection(cap, insert.data(), insert.dir())));
                } catch (NullPointerException npe) {
                    throw new RuntimeException("blockentity provided broken capability: " + blockEntity.getClass() + " for type:" + getCapability().getName(), npe);
                }
            }
        }
        if (!insertCaps.isEmpty()) {
            List<CapabilityConnection> extractCaps = new ArrayList<>();

            for (Connection extract : extracts) {
                Optional
                    .ofNullable(level.getBlockEntity(extract.move()))
                    .flatMap(b -> b.getCapability(getCapability(), extract.dir().getOpposite()).resolve())
                    .ifPresent(cap -> extractCaps.add(new CapabilityConnection(cap, extract.data(), extract.dir())));
            }
            if (!extractCaps.isEmpty()) {
                tickCapabilityGraph(type, insertCaps, extractCaps, level, graph, isRedstoneActive);
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

    protected abstract void tickCapabilityGraph(IConduitType<?> type, List<CapabilityConnection> inserts, List<CapabilityConnection> extracts,
        ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive );

    protected abstract Capability<T> getCapability();

    public class CapabilityConnection {
        public final T cap;
        public final IExtendedConduitData<?> data;
        public final Direction direction;

        private CapabilityConnection(T cap, IExtendedConduitData<?> data, Direction direction) {
            this.cap = cap;
            this.data = data;
            this.direction = direction;
        }
    }
}
