package com.enderio.api.conduit;

import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.RedstoneControl;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class NodeIdentifier<T extends IExtendedConduitData<?>> implements GraphObject<Mergeable.Dummy> {

    private final BlockPos pos;

    @Nullable private Graph<Mergeable.Dummy> graph = null;

    private final Map<Direction, IOState> ioStates = new EnumMap<>(Direction.class);
    private final T extendedConduitData;

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
    public void pushState(Direction direction, @Nullable ColorControl insert, @Nullable ColorControl extract, RedstoneControl control,
        ColorControl redstoneChannel) {
        ioStates.put(direction, IOState.of(insert, extract, control, redstoneChannel));
    }

    public Optional<IOState> getIOState(Direction direction) {
        return Optional.ofNullable(ioStates.get(direction));
    }

    public T getExtendedConduitData() {
        return extendedConduitData;
    }

    @ApiStatus.Internal
    public void clearState(Direction direction) {
        ioStates.remove(direction);
    }

    public BlockPos getPos() {
        return pos;
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
