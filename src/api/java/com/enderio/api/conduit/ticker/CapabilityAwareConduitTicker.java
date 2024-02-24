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
import net.neoforged.neoforge.capabilities.BlockCapability;
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
            BlockEntity b = level.getBlockEntity(insert.move());
            T capability = level.getCapability(getCapability(), b.getBlockPos(), b.getBlockState(), b, insert.dir().getOpposite());
            if (capability != null) {
                insertCaps.add(new CapabilityConnection(capability, insert.data(), insert.dir()));
            }

        }
        if (!insertCaps.isEmpty()) {
            List<CapabilityConnection> extractCaps = new ArrayList<>();

            for (Connection extract : extracts) {
                BlockEntity b = level.getBlockEntity(extract.move());
                T capability = level.getCapability(getCapability(), b.getBlockPos(), b.getBlockState(), b, extract.dir().getOpposite());
                if (capability != null) {
                    insertCaps.add(new CapabilityConnection(capability, extract.data(), extract.dir()));
                }
            }
            if (!extractCaps.isEmpty()) {
                tickCapabilityGraph(type, insertCaps, extractCaps, level, graph, isRedstoneActive);
            }
        }
    }

    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        BlockEntity b = level.getBlockEntity(conduitPos.relative(direction));
        T capability = level.getCapability(getCapability(), b.getBlockPos(), b.getBlockState(), b, direction.getOpposite());
        return capability != null;
    }

    protected abstract void tickCapabilityGraph(IConduitType<?> type, List<CapabilityConnection> inserts, List<CapabilityConnection> extracts,
        ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive );

    protected abstract BlockCapability<T,Direction> getCapability();

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
