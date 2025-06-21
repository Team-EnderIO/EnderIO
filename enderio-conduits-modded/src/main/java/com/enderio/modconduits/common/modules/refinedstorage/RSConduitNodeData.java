package com.enderio.modconduits.common.modules.refinedstorage;

import com.enderio.conduits.api.network.node.IConduitNode;
import com.enderio.conduits.api.network.node.NodeData;
import com.enderio.conduits.api.network.node.NodeDataType;
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
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class RSConduitNodeData implements NodeData {
    public static final NodeDataType<RSConduitNodeData> TYPE = new NodeDataType<>(null, RSConduitNodeData::new);

    @Nullable
    public NetworkNodeContainerProvider containerProvider;

    @Nullable
    private ConduitRSNodeContainer mainNodeContainer;

    public boolean isAccessible() {
        return containerProvider != null && mainNodeContainer != null && !mainNodeContainer.isRemoved();
    }

    public void initialize(IConduitNode conduitNode, Level level, BlockPos pos) {
        containerProvider = RefinedStorageApi.INSTANCE.createNetworkNodeContainerProvider();
        mainNodeContainer = new ConduitRSNodeContainer(level, pos);

        mainNodeContainer.setConnectedSides(Arrays.stream(Direction.values()).filter(conduitNode::isConnectedTo).collect(Collectors.toSet()));

        containerProvider.addContainer(mainNodeContainer);

        containerProvider.initialize(level, () -> {});
        level.blockUpdated(pos, level.getBlockState(pos).getBlock());

        // TODO: is this necessary?
        var state = level.getBlockState(pos);
        state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);

        containerProvider.update(level);
    }

    public void update(Level level, Set<Direction> connectedSides) {
        if (containerProvider != null && mainNodeContainer != null) {
            mainNodeContainer.setConnectedSides(connectedSides);
            containerProvider.update(level);
        }
    }

    public void remove(Level level) {
        if (containerProvider != null && mainNodeContainer != null) {
            // Remove from RS networks
            mainNodeContainer.setRemoved(true);
            containerProvider.remove(level);

            // Clear out the containers
            containerProvider = null;
            mainNodeContainer = null;
        }
    }

    @Override
    public NodeDataType<?> type() {
        return TYPE;
    }

    public static class ConduitRSNodeContainer implements InWorldNetworkNodeContainer {

        private final BlockState blockState;
        private final GlobalPos globalPos;
        private final NetworkNode node;

        private Set<Direction> connectedSides = Set.of();
        private boolean removed;

        public ConduitRSNodeContainer(Level level, BlockPos pos) {
            this.blockState = level.getBlockState(pos);
            this.globalPos = GlobalPos.of(level.dimension(), pos);
            // TODO: Config for energy use of RS conduits? Either on the conduit or in mod
            // config.
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
