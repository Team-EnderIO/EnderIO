package com.enderio.enderio.conduits.tests.network.pathing;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.connection.ConnectionStatus;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathProperty;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.api.conduits.network.node.NodeData;
import com.enderio.enderio.api.conduits.network.node.NodeDataType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Minimal ConduitNode implementation for pathfinding unit tests.
 * <p>
 * Only implements methods required by the pathfinding algorithm.
 * All other methods throw {@link UnsupportedOperationException}.
 * </p>
 */
public class FakeConduitNode implements ConduitNode {
    private final String debugId;
    private final BlockPos position;
    private final Holder<Conduit<?, ?>> conduit;
    private final Map<ConnectionPathProperty<?>, Object> pathProperties = new HashMap<>();
    
    public FakeConduitNode(String debugId, BlockPos position, Holder<Conduit<?, ?>> conduit) {
        this.debugId = debugId;
        this.position = position;
        this.conduit = conduit;
    }
    
    // region Methods Used by Pathfinding
    
    @Override
    public BlockPos pos() {
        return position;
    }
    
    @Override
    public Holder<Conduit<?, ?>> conduit() {
        return conduit;
    }
    
    // endregion
    
    // region Test Helper Methods
    
    /**
     * Set a path property for testing property aggregation.
     * 
     * @param property the property to set
     * @param value the value to assign
     */
    public void setPathProperty(ConnectionPathProperty<?> property, Object value) {
        pathProperties.put(property, value);
    }
    
    /**
     * Get all path properties for this node.
     * Used by MockPathfindingContext.
     * 
     * @return immutable copy of path properties
     */
    public Map<ConnectionPathProperty<?>, Object> getPathProperties() {
        return Map.copyOf(pathProperties);
    }
    
    @Override
    public String toString() {
        return "FakeConduitNode{" + debugId + " @ " + position + "}";
    }
    
    // endregion
    
    // region Unsupported Methods (Not Used by Pathfinding)
    
    @Override
    public boolean isLoaded() {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    @Override
    public boolean isTicking() {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    @Override
    public void markDirty() {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    @Override
    public ConduitNetwork getNetwork() {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    @Override
    public boolean hasNodeData(NodeDataType<?> type) {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    @Override
    @Nullable
    public NodeData getNodeData() {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    @Override
    @Nullable
    public <T extends NodeData> T getNodeData(NodeDataType<T> type) {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    @Override
    public <T extends NodeData> T getOrCreateNodeData(NodeDataType<T> type) {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    @Override
    public <T extends NodeData> void setNodeData(@Nullable T data) {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }

    @Override
    public @Nullable <T, C> T getCapability(BlockCapability<T, C> capability, Direction neighborSide, @Nullable C context) {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }

    @Override
    public boolean hasRedstoneSignal(@Nullable DyeColor signalColor) {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    @Override
    public ConnectionStatus getConnectionStatus(Direction side) {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    @Override
    public boolean isConnectedToBlock(Direction side) {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    @Override
    public boolean isConnectedTo(Direction side) {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    @Override
    public ConnectionConfig getConnectionConfig(Direction side) {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    @Override
    public <T extends ConnectionConfig> T getConnectionConfig(Direction side, ConnectionConfigType<T> type) {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    @Override
    public IItemHandlerModifiable getInventory(Direction side) {
        throw new UnsupportedOperationException("Not needed for pathfinding tests");
    }
    
    // endregion
}
