package com.enderio.conduits.common.conduit;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.conduit.connection.DynamicConnectionState;
import com.enderio.conduits.common.init.ConduitCapabilities;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ConduitGraphObject<T extends ConduitData<T>> implements GraphObject<Mergeable.Dummy>, ConduitNode<T> {

    private final BlockPos pos;

    @Nullable private Graph<Mergeable.Dummy> graph = null;
    @Nullable private WrappedConduitGraph<T> wrappedGraph = null;

    private final Map<Direction, IOState> ioStates = new EnumMap<>(Direction.class);
    private final T conduitData;
    private final Map<Direction, DynamicConnectionState> connectionStates = new EnumMap<>(Direction.class);

    public ConduitGraphObject(BlockPos pos, T conduitData) {
        this.pos = pos;
        this.conduitData = conduitData;
    }

    @Nullable
    @Override
    public Graph<Mergeable.Dummy> getGraph() {
        return graph;
    }

    @Override
    public void setGraph(@Nullable Graph<Mergeable.Dummy> graph) {
        this.graph = graph;
        this.wrappedGraph = graph == null ? null : new WrappedConduitGraph<>(graph);
    }

    @Nullable
    @Override
    public ConduitGraph<T> getParentGraph() {
        return wrappedGraph;
    }

    public void pushState(Direction direction, DynamicConnectionState connectionState) {
        this.connectionStates.put(direction, connectionState);
        ioStates.put(direction, IOState.of(connectionState.isInsert() ? connectionState.insertChannel() : null,
            connectionState.isExtract() ? connectionState.extractChannel() : null, connectionState.control(), connectionState.redstoneChannel()));
    }

    public Optional<IOState> getIOState(Direction direction) {
        return Optional.ofNullable(ioStates.get(direction));
    }

    public T getConduitData() {
        return conduitData;
    }

    public void clearState(Direction direction) {
        ioStates.remove(direction);
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public @Nullable ConduitUpgrade getUpgrade(Direction direction) {
        return connectionStates.get(direction).upgradeExtract().getCapability(ConduitCapabilities.CONDUIT_UPGRADE).orElse(null);
    }

    @Override
    public @Nullable ResourceFilter getExtractFilter(Direction direction) {
        return connectionStates.get(direction).filterExtract().getCapability(EIOCapabilities.FILTER).orElse(null);
    }

    @Override
    public @Nullable ResourceFilter getInsertFilter(Direction direction) {
        return connectionStates.get(direction).filterInsert().getCapability(EIOCapabilities.FILTER).orElse(null);
    }

    @UseOnly(LogicalSide.CLIENT)
    public ConduitGraphObject<T> deepCopy() {
        return new ConduitGraphObject<>(pos, conduitData.deepCopy());
    }

    // Separate method to avoid breaking the graph
    public int hashContents() {
        return Objects.hash(pos, conduitData, ioStates, connectionStates);
    }
}
