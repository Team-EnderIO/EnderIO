package com.enderio.enderio.content.conduits.network;

import com.enderio.core.common.graph.INetworkNode;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.connection.ConnectionStatus;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.api.conduits.network.node.NodeData;
import com.enderio.enderio.api.conduits.network.node.NodeDataType;
import com.enderio.enderio.content.conduits.legacy.ConduitDataContainer;
import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class ConduitNodeImpl implements INetworkNode<ConduitNetworkImpl, ConduitNodeImpl>, ConduitNode {

    // TODO: 1.22 - Remove legacy codec.
    private static final Codec<ConduitNodeImpl> LEGACY_V7_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(BlockPos.CODEC.fieldOf("pos").forGetter(ConduitNodeImpl::pos),
                    ConduitDataContainer.CODEC.fieldOf("data").forGetter(i -> i.legacyDataContainer))
            .apply(instance, ConduitNodeImpl::new));

    private static final Codec<ConduitNodeImpl> NEW_CODEC = RecordCodecBuilder
            .create(instance -> instance.group(
                Conduit.CODEC.optionalFieldOf("conduit", null).forGetter(ConduitNodeImpl::conduit),
                BlockPos.CODEC.fieldOf("pos").forGetter(ConduitNodeImpl::pos),
                    NodeData.GENERIC_CODEC.optionalFieldOf("data")
                            .forGetter(i -> i.nodeData == null || !i.nodeData.type().isPersistent() ? Optional.empty()
                                    : Optional.of(i.nodeData)))
                    .apply(instance, ConduitNodeImpl::new));

    public static final Codec<ConduitNodeImpl> CODEC = Codec.withAlternative(NEW_CODEC, LEGACY_V7_CODEC);

    // TODO: final in 21.11
    private Holder<Conduit<?, ?>> conduit;
    private final BlockPos pos;

    @Nullable
    private NodeData nodeData;

    // TODO: Remove in 21.11
    @Nullable
    private ConduitDataContainer legacyDataContainer = null;

    @Nullable
    private ConduitNetworkImpl network;

    @Nullable
    private IConduitNodeAttachment conduitBundle;

    public ConduitNodeImpl(Holder<Conduit<?, ?>> conduit, BlockPos pos) {
        this(conduit, pos, (NodeData) null);
    }

    public ConduitNodeImpl(Holder<Conduit<?, ?>> conduit, BlockPos pos, @Nullable NodeData nodeData) {
        this.conduit = conduit;
        this.pos = pos;
        this.nodeData = nodeData;
        this.network = new ConduitNetworkImpl(conduit.value().type(), this);
    }

    public ConduitNodeImpl(Holder<Conduit<?, ?>> conduit, BlockPos pos, Optional<NodeData> nodeData) {
        this.conduit = conduit;
        this.pos = pos;
        this.nodeData = nodeData.orElse(null);
        this.network = new ConduitNetworkImpl(conduit.value().type(), this);
    }

    public ConduitNodeImpl(Holder<Conduit<?, ?>> conduit, BlockPos pos, ConduitDataContainer legacyDataContainer) {
        this(conduit, pos, (NodeData) null);

        // Extract node data from legacy data
        var oldData = legacyDataContainer.getData();
        if (oldData != null) {
            // Store for copyLegacyData once we have a network.
            this.legacyDataContainer = legacyDataContainer;
            this.nodeData = oldData.toNodeData();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ConduitNodeImpl(BlockPos pos, Optional<NodeData> nodeData) {
        this.pos = pos;
        this.nodeData = nodeData.orElse(null);
        // Does not create a network because we're loading.
    }

    private ConduitNodeImpl(BlockPos pos, ConduitDataContainer legacyDataContainer) {
        this(pos, Optional.empty());

        // Extract node data from legacy data
        var oldData = legacyDataContainer.getData();
        if (oldData != null) {
            // Store for copyLegacyData once we have a network.
            this.legacyDataContainer = legacyDataContainer;
            this.nodeData = oldData.toNodeData();
        }
    }

    // Intended for loading from the old network format.
    @Deprecated
    public void setConduitIfNull(Holder<Conduit<?, ?>> conduit) {
        if (this.conduit == null) {
            this.conduit = conduit;
        }
    }

    public void attach(IConduitNodeAttachment conduitBundle) {
        Preconditions.checkState(network != null, "Conduit node is not connected to a network.");
        this.conduitBundle = conduitBundle;
        network.onNodeUpdated(this);
        tryCopyLegacyData();
    }

    public void detach() {
        if (conduitBundle == null) {
            return;
        }

        this.conduitBundle = null;

        if (network != null) {
            network.onNodeUpdated(this);
        }
    }

    public void onConfigChanged() {
        if (network != null) {
            network.onNodeUpdated(this);
        }
    }

    public void onRedstoneChanged() {
        if (network != null) {
            network.onNodeUpdated(this);
        }
    }

    @Override
    public Holder<Conduit<?, ?>> conduit() {
        return conduit;
    }

    @Override
    public BlockPos pos() {
        return pos;
    }

    @Override
    public boolean isLoaded() {
        if (!isValid() || conduitBundle == null) {
            return false;
        }

        return conduitBundle.hasLevel() && conduitBundle.getLevel().isLoaded(pos);
    }

    @Override
    public boolean isTicking() {
        return isLoaded() && conduitBundle.getLevel().shouldTickBlocksAt(pos);
    }

    @Override
    public void markDirty() {
        // No-op if we're loading chunks, just in case.
        if (isLoaded()) {
            // noinspection DataFlowIssue
            conduitBundle.markNodesDirty();
        }
    }

    // region Node Data

    @Override
    public boolean hasNodeData(NodeDataType<?> type) {
        return nodeData != null && nodeData.type() == type;
    }

    @Override
    @Nullable
    public NodeData getNodeData() {
        return nodeData;
    }

    @Override
    @Nullable
    public <D extends NodeData> D getNodeData(NodeDataType<D> type) {
        if (nodeData != null && type == nodeData.type()) {
            // noinspection unchecked
            return (D) nodeData;
        }

        return null;
    }

    @Override
    public <D extends NodeData> D getOrCreateNodeData(NodeDataType<D> type) {
        if (nodeData != null && type == nodeData.type()) {
            // noinspection unchecked
            return (D) nodeData;
        }

        nodeData = type.create();
        // noinspection unchecked
        return (D) nodeData;
    }

    @Override
    public <D extends NodeData> void setNodeData(@Nullable D data) {
        nodeData = data;
    }

    // endregion

    // region World Interaction

    @Override
    public <TCapability> TCapability getNeighborSidedCapability(BlockCapability<TCapability, Direction> capability,
            Direction side) {
        ensureValid();
        // noinspection DataFlowIssue
        return conduitBundle.getNeighborSidedCapability(conduit, capability, side);
    }

    @Override
    public <TCapability> TCapability getNeighborVoidCapability(BlockCapability<TCapability, Void> capability,
            Direction side) {
        ensureValid();
        // noinspection DataFlowIssue
        return conduitBundle.getNeighborVoidCapability(conduit, capability, side);
    }

    @Override
    public boolean hasRedstoneSignal(@Nullable DyeColor signalColor) {
        ensureValid();
        // noinspection DataFlowIssue
        return conduitBundle.hasRedstoneSignal(signalColor);
    }

    // endregion

    // region Connections

    @Override
    public ConnectionStatus getConnectionStatus(Direction side) {
        ensureValid();
        // noinspection DataFlowIssue
        return conduitBundle.getConnectionStatus(conduit, side);
    }

    @Override
    public boolean isConnectedToBlock(Direction side) {
        ensureValid();
        // noinspection DataFlowIssue
        return conduitBundle.getConnectionStatus(conduit, side).isEndpoint();
    }

    @Override
    public boolean isConnectedTo(Direction side) {
        ensureValid();
        // noinspection DataFlowIssue
        return conduitBundle.getConnectionStatus(conduit, side).isConnected();
    }

    @Override
    public ConnectionConfig getConnectionConfig(Direction side) {
        ensureValid();
        // noinspection DataFlowIssue
        return conduitBundle.getConnectionConfig(conduit, side);
    }

    @Override
    public <T extends ConnectionConfig> T getConnectionConfig(Direction side, ConnectionConfigType<T> type) {
        ensureValid();
        // noinspection DataFlowIssue
        return conduitBundle.getConnectionConfig(conduit, side, type);
    }

    // endregion

    // region Inventory

    @Override
    public IItemHandlerModifiable getInventory(Direction side) {
        ensureValid();

        // We don't have to do this, but it saves null checks in the tickers.
        // Only tickers that know they have inventories should use this anyway.
        // noinspection DataFlowIssue
        var inventory = conduitBundle.getConnectionInventory(conduit, side);
        if (inventory == null) {
            throw new IllegalStateException("This conduit does not have an inventory!");
        }

        return inventory;
    }

    // endregion

    // region Network Node Impl

    @Override
    public boolean isValid() {
        return network != null;
    }

    @Override
    public ConduitNetworkImpl getNetwork() {
        return Objects.requireNonNull(network, "Node is not valid!");
    }

    @Override
    public void setNetwork(@Nullable ConduitNetworkImpl network) {
        this.network = network;
        tryCopyLegacyData();
    }

    // endregion

    private void ensureValid() {
        Preconditions.checkState(network != null, "Conduit node is not connected to a network.");
        Preconditions.checkState(conduitBundle != null, "Conduit node is detached.");
        Preconditions.checkState(conduitBundle.hasLevel(), "Conduit bundle is not attached to a level");
        Preconditions.checkState(conduitBundle.getLevel().isLoaded(pos), "Conduit bundle is not loaded in a loaded chunk");
        Preconditions.checkState(isLoaded(), "Conduit node is not loaded - more specific error unavailable.");
    }

    private void tryCopyLegacyData() {
        if (this.conduit != null&& legacyDataContainer != null) {
            // We now know what type of conduit we are, so upgrade the connection data then
            // drop legacy data
            this.conduit
                    .value()
                    .copyLegacyData(this, legacyDataContainer,
                            (side, config) -> conduitBundle.setConnectionConfig(conduit, side, config));
            legacyDataContainer = null;
        }
    }
}
