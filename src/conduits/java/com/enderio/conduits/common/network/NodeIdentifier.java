package com.enderio.conduits.common.network;

import com.enderio.core.common.blockentity.ColorControl;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class NodeIdentifier implements GraphObject<Mergeable.Dummy> {

    private final BlockPos pos;

    @Nullable
    private Graph<Mergeable.Dummy> graph = null;

    private final Map<Direction, IOState> ioStates = new EnumMap<>(Direction.class);

    public NodeIdentifier(BlockPos pos) {
        this.pos = pos;
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

    public void pushState(Direction direction, @Nullable ColorControl input, @Nullable ColorControl output) {
        Optional<IOState> ioState = IOState.of(input, output);
        if (ioState.isPresent()) {
            ioStates.put(direction, ioState.get());
        } else {
            ioStates.remove(direction);
        }
    }

    public Optional<IOState> getIOState(Direction direction) {
        return Optional.ofNullable(ioStates.get(direction));
    }

    public void clearState(Direction direction) {
        ioStates.remove(direction);
    }

    public BlockPos getPos() {
        return pos;
    }

    public record IOState(Optional<ColorControl> in, Optional<ColorControl> out) {

        public boolean isInput() {
            return in().isPresent();
        }
        public boolean isOutput() {
            return out().isPresent();
        }

        private static Optional<IOState> of(@Nullable ColorControl in, @Nullable ColorControl out) {
            if (in == null && out == null)
                return Optional.empty();
            return Optional.of(new IOState(Optional.ofNullable(in), Optional.ofNullable(out)));
        }
    }
}
