package com.enderio.conduits.common.conduit;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.conduit.connection.DynamicConnectionState;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.init.ConduitCapabilities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ConduitGraphObject<T extends ConduitData<T>> implements GraphObject<Mergeable.Dummy>, ConduitNode<T> {

    public static final Codec<ConduitGraphObject<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockPos.CODEC.fieldOf("pos").forGetter(ConduitGraphObject::getPos),
        ConduitData.CODEC.fieldOf("data").forGetter(ConduitGraphObject::getConduitData)
    ).apply(instance, ConduitGraphObject::of));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConduitGraphObject<?>> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ConduitGraphObject::getPos,
        ConduitData.STREAM_CODEC,
        ConduitGraphObject::getConduitData,
        ConduitGraphObject::of
    );

    // Performs a dirty cast during deserialization, but it's safe because the cast is only done on the same type.
    private static <T extends ConduitData<T>, Z extends ConduitData<?>> ConduitGraphObject<T> of(BlockPos pos, Z cast) {
        //noinspection unchecked
        return new ConduitGraphObject<>(pos, (T) cast);
    }

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
    public void setGraph(Graph<Mergeable.Dummy> graph) {
        this.graph = graph;
        this.wrappedGraph = new WrappedConduitGraph<>(graph);
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
        return connectionStates.get(direction).upgradeExtract().getCapability(ConduitCapabilities.ConduitUpgrade.ITEM);
    }

    @Override
    public @Nullable ResourceFilter getExtractFilter(Direction direction) {
        return connectionStates.get(direction).filterExtract().getCapability(EIOCapabilities.Filter.ITEM);
    }

    @Override
    public @Nullable ResourceFilter getInsertFilter(Direction direction) {
        return connectionStates.get(direction).filterInsert().getCapability(EIOCapabilities.Filter.ITEM);
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
