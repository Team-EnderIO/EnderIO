package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.conduit.ConduitNode;
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
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class NodeIdentifier<T extends ExtendedConduitData<?>> implements GraphObject<Mergeable.Dummy>, ConduitNode<T> {

    public static final Codec<NodeIdentifier<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockPos.CODEC.fieldOf("pos").forGetter(NodeIdentifier::getPos),
        ExtendedConduitData.CODEC.fieldOf("data").forGetter(NodeIdentifier::getExtendedConduitData)
    ).apply(instance, NodeIdentifier::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, NodeIdentifier<?>> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        NodeIdentifier::getPos,
        ExtendedConduitData.STREAM_CODEC,
        NodeIdentifier::getExtendedConduitData,
        NodeIdentifier::new
    );

    private final BlockPos pos;

    @Nullable private Graph<Mergeable.Dummy> graph = null;

    private final Map<Direction, IOState> ioStates = new EnumMap<>(Direction.class);
    private T extendedConduitData;
    private final Map<Direction, DynamicConnectionState> connectionStates = new EnumMap<>(Direction.class);

    public NodeIdentifier(BlockPos pos, T extendedConduitData) {
        this.pos = pos;
        this.extendedConduitData = extendedConduitData;
    }

    @Nullable
    @Override
    public Graph<Mergeable.Dummy> getGraph() {
        return graph;
    }

    @Override
    public void setGraph(Graph<Mergeable.Dummy> graph) {
        this.graph = graph;
    }

    public void pushState(Direction direction, DynamicConnectionState connectionState) {
        this.connectionStates.put(direction, connectionState);
        ioStates.put(direction, IOState.of(connectionState.isInsert() ? connectionState.insertChannel() : null,
            connectionState.isExtract() ? connectionState.extractChannel() : null, connectionState.control(), connectionState.redstoneChannel()));
    }

    public Optional<IOState> getIOState(Direction direction) {
        return Optional.ofNullable(ioStates.get(direction));
    }

    public T getExtendedConduitData() {
        return extendedConduitData;
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
    public @Nullable Predicate<?> getExtractFilter(Direction direction) {
        return connectionStates.get(direction).filterExtract().getCapability(EIOCapabilities.Filter.ITEM_STACK);
    }

    @Override
    public @Nullable Predicate<?> getInsertFilter(Direction direction) {
        return connectionStates.get(direction).filterInsert().getCapability(EIOCapabilities.Filter.ITEM_STACK);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, extendedConduitData, ioStates, connectionStates);
    }
}
