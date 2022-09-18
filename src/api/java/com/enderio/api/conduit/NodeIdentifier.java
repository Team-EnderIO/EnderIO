package com.enderio.api.conduit;

import com.enderio.api.misc.ColorControl;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class NodeIdentifier<T extends IExtendedConduitData<?>> implements GraphObject<Mergeable.Dummy> {


    private final BlockPos pos;

    @Nullable
    private Graph<Mergeable.Dummy> graph = null;

    private final Map<Direction, IOState> ioStates = new EnumMap<>(Direction.class);
    private final T extendedConduitData;

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

    public void pushState(Direction direction, @Nullable ColorControl insert, @Nullable ColorControl extract) {
        ioStates.put(direction, IOState.of(insert, extract));
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

    public record IOState(Optional<ColorControl> insert, Optional<ColorControl> extract) {

        public boolean isInsert() {
            return insert().isPresent();
        }
        public boolean isExtract() {
            return extract().isPresent();
        }

        private static IOState of(@Nullable ColorControl in, @Nullable ColorControl out) {
            return new IOState(Optional.ofNullable(in), Optional.ofNullable(out));
        }
    }
}
