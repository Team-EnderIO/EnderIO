package com.enderio.api.conduit;

import com.enderio.api.conduit.connection.DynamicConnectionState;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.RedstoneControl;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class NodeIdentifier<T extends ExtendedConduitData<?>> implements GraphObject<Mergeable.Dummy> {

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

    @ApiStatus.Internal
    public NodeIdentifier(BlockPos pos, T extendedConduitData) {
        this.pos = pos;
        this.extendedConduitData = extendedConduitData;
    }

    @Nullable
    @Override
    @ApiStatus.Internal
    public Graph<Mergeable.Dummy> getGraph() {
        return graph;
    }

    @Override
    @ApiStatus.Internal
    public void setGraph(Graph<Mergeable.Dummy> graph) {
        this.graph = graph;
    }

    @ApiStatus.Internal
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

    public void setExtendedConduitData(T extendedConduitData) {
        this.extendedConduitData = extendedConduitData;
    }

    @ApiStatus.Internal
    public void clearState(Direction direction) {
        ioStates.remove(direction);
    }

    public BlockPos getPos() {
        return pos;
    }

    @Nullable
    public DynamicConnectionState getConnectionState(Direction direction) {
        return connectionStates.get(direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, extendedConduitData, ioStates, connectionStates);
    }

    public record IOState(Optional<ColorControl> insert, Optional<ColorControl> extract, RedstoneControl control, ColorControl redstoneChannel) {

        public boolean isInsert() {
            return insert().isPresent();
        }

        public boolean isExtract() {
            return extract().isPresent();
        }

        private static IOState of(@Nullable ColorControl in, @Nullable ColorControl extract, RedstoneControl control, ColorControl redstoneChannel) {
            return new IOState(Optional.ofNullable(in), Optional.ofNullable(extract), control, redstoneChannel);
        }
    }
}
