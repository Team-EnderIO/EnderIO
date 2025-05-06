package com.enderio.modconduits.common.modules.refinedstorage;

import com.enderio.conduits.api.network.node.ConduitNode;
import com.enderio.conduits.api.network.node.NodeData;
import com.enderio.conduits.api.network.node.NodeDataType;
import com.mojang.serialization.MapCodec;
import com.refinedmods.refinedstorage.api.network.impl.node.grid.GridNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.network.ConnectionSink;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class RSConduitNodeData implements NodeData {
    public static final MapCodec<RSConduitNodeData> CODEC = MapCodec.unit(RSConduitNodeData::new);

    public static NodeDataType<RSConduitNodeData> TYPE = new NodeDataType<>(CODEC, RSConduitNodeData::new);

    public final NetworkNodeContainerProvider container;
    private ConduitRSNode mainNode;

    public RSConduitNodeData() {
        container = RefinedStorageApi.INSTANCE.createNetworkNodeContainerProvider();
    }

    public boolean isInitialized() {
        return mainNode != null && !mainNode.isRemoved();
    }

    public void initialize(ConduitNode conduitNode, Level level, BlockPos pos) {
        mainNode = new ConduitRSNode(level, pos);

        // Gather initially connected sides.
        mainNode.setConnectedSides(Arrays.stream(Direction.values()).filter(conduitNode::isConnectedTo).collect(Collectors.toSet()));

        container.addContainer(mainNode);
        container.initialize(level, () -> {});
        level.blockUpdated(pos, level.getBlockState(pos).getBlock());

        // TODO: is this necessary?
        var state = level.getBlockState(pos);
        state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);

        container.update(level);
    }

    public void update(Level level, Set<Direction> connectedSides) {
        mainNode.setConnectedSides(connectedSides);
        container.update(level);
    }

    public void remove(Level level) {
        if (mainNode != null) {
            mainNode.setRemoved(true);
            container.remove(level);
            mainNode = null;
        }
    }

    @Override
    public NodeDataType<?> type() {
        return TYPE;
    }

    public static class ConduitRSNode implements InWorldNetworkNodeContainer {

        private final BlockState blockState;
        private final GlobalPos globalPos;
        private final NetworkNode node;

        private Set<Direction> connectedSides;
        private boolean removed;

        public ConduitRSNode(Level level, BlockPos pos) {
            this.blockState = level.getBlockState(pos);
            this.globalPos = GlobalPos.of(level.dimension(), pos);
            // TODO: Config for energy use of RS conduits? Either on the conduit or in mod config.
            this.node = new GridNetworkNode(0);
            this.removed = false;
        }

        public void setConnectedSides(Set<Direction> connectedSides) {
            this.connectedSides = connectedSides;
        }

        @Override
        public BlockState getBlockState() {
            return this.blockState;
        }

        @Override
        public boolean isRemoved() {
            return removed;
        }

        public void setRemoved(boolean removed) {
            this.removed = removed;
        }

        @Override
        public GlobalPos getPosition() {
            return this.globalPos;
        }

        @Override
        public BlockPos getLocalPosition() {
            return this.globalPos.pos();
        }

        @Override
        public String getName() {
            return "RS Conduit";
        }

        @Override
        public NetworkNode getNode() {
            return this.node;
        }

        @Override
        public void addOutgoingConnections(ConnectionSink connectionSink) {
            for (Direction direction : connectedSides) {
                connectionSink.tryConnectInSameDimension(this.globalPos.pos().relative(direction), direction.getOpposite());
            }
        }

        @Override
        public boolean canAcceptIncomingConnection(Direction direction, BlockState blockState) {
            return connectedSides.contains(direction);
        }
    }
}
