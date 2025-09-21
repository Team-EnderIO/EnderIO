package com.enderio.conduits.common.conduit.bundle;

import com.enderio.base.api.UseOnly;
import com.enderio.base.common.blockentity.Wrenchable;
import com.enderio.conduits.ConduitNBTKeys;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitCapabilities;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.bundle.AddConduitResult;
import com.enderio.conduits.api.bundle.ConduitBundle;
import com.enderio.conduits.api.bundle.SlotType;
import com.enderio.conduits.api.connection.ConnectionStatus;
import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.facade.FacadeType;
import com.enderio.conduits.api.network.node.NodeData;
import com.enderio.conduits.client.model.conduit.bundle.ConduitBundleRenderState;
import com.enderio.conduits.common.conduit.ConduitBlockItem;
import com.enderio.conduits.common.conduit.ConduitSorter;
import com.enderio.conduits.common.conduit.legacy.ConduitDataContainer;
import com.enderio.conduits.common.conduit.legacy.ConnectionState;
import com.enderio.conduits.common.conduit.legacy.DynamicConnectionState;
import com.enderio.conduits.common.conduit.legacy.StaticConnectionStates;
import com.enderio.conduits.common.conduit.menu.ConduitMenu;
import com.enderio.conduits.common.conduit.network.ConduitNetworkSavedData;
import com.enderio.conduits.common.conduit.network.ConduitNode;
import com.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.conduits.common.init.ConduitTypes;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public final class ConduitBundleBlockEntity extends EnderBlockEntity
        implements ConduitBundle, Wrenchable, ConduitMenu.ConnectionAccessor {

    public static final int MAX_CONDUITS = 9;

    @UseOnly(LogicalSide.CLIENT)
    public static final Long2ObjectMap<BlockState> FACADES = new Long2ObjectOpenHashMap<>();

    @UseOnly(LogicalSide.CLIENT)
    public static final Long2ObjectMap<LongSet> CHUNK_FACADES = new Long2ObjectOpenHashMap<>();

    private ItemStack facadeProvider = ItemStack.EMPTY;

    private List<Holder<Conduit<?, ?>>> conduits = new ArrayList<>();

    private Map<Holder<Conduit<?, ?>>, ConnectionContainer> conduitConnections = new HashMap<>();

    // Map of all conduit nodes for this bundle.
    private final Map<Holder<Conduit<?, ?>>, ConduitNode> conduitNodes = new HashMap<>();

    // Capability caches
    private final Map<Holder<Conduit<?, ?>>, NeighboringCapabilityCaches> neighbouringCapabilityCaches = new HashMap<>();

    // Data recovery mechanism
    private final Map<Holder<Conduit<?, ?>>, ConduitNode> lazyNodes = new HashMap<>();
    private ListTag lazyNodeNBT = null;
    private Map<Holder<Conduit<?, ?>>, NodeData> lazyNodeData = null;

    // Client-side extra render data
    @UseOnly(LogicalSide.CLIENT)
    private final Map<Holder<Conduit<?, ?>>, CompoundTag> clientConduitExtraWorldData = new HashMap<>();

    private final ConduitShape shape = new ConduitShape();

    private boolean hasDirtyNodes = false;

    // Deferred connection check
    private UpdateState checkConnection = UpdateState.NONE;

    // NBT Keys
    private static final String FACADE_PROVIDER_KEY = "FacadeProvider";
    private static final String CONDUITS_KEY = "Conduits";
    private static final String CONNECTIONS_KEY = "Connections";
    private static final String NODE_DATA_KEY = "NodeData";

    private static final String CONDUIT_CLIENT_WORLD_DATA_KEY = "ConduitWorldData";

    // Set by ConduitBlockItem#placeBlock to set the side the block was placed off
    // of.
    // This is used to determine which side to prioritise connections to.
    // TODO: Its this or a block state property...
    @Nullable
    public Direction primaryConnectionSide;

    public ConduitBundleBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ConduitBlockEntities.CONDUIT.get(), worldPosition, blockState);
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if (level != null) {
            checkConnection = checkConnection.next();
            if (checkConnection.isInitialized()) {
                updateConnections(level, getBlockPos(), null, false);
            }

            if (hasDirtyNodes) {
                // This is for sending updates to clients when the nodes are dirty
                // as such we only fire a block update
                // TODO: We're also saving here, but maybe we shouldn't bother?
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
                setChanged();
                hasDirtyNodes = false;
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();

        updateShape();
        updateNeighborRedstone();

        if (level != null && !level.isClientSide()) {
            // Fire on-created events
            for (var conduit : conduits) {
                conduit.value().onCreated(conduitNodes.get(conduit), level, getBlockPos(), null);
            }

            // Attempt to make connections for recovered nodes.
            for (var entry : lazyNodes.entrySet()) {
                Holder<Conduit<?, ?>> conduit = entry.getKey();

                for (Direction dir : Direction.values()) {
                    tryConnectTo(conduit, dir, false);
                }
            }
        }

        // Update lighting engine now that the bundle is loaded
        if (level != null && hasFacade()) {
            level.getLightEngine().checkBlock(getBlockPos());
        }
    }

    /**
     * Fire all relevant updates when the conduits or connections change.
     */
    private void bundleChanged() {
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        level.invalidateCapabilities(getBlockPos());
        setChanged();
        updateShape();

        if (level.isClientSide()) {
            updateModel();
        }
    }

    // region Shape and Model

    public ConduitShape getShape() {
        return shape;
    }

    public void updateShape() {
        shape.updateConduit(this);
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    public void updateModel() {
        requestModelDataUpdate();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }

        if (hasFacade()) {
            FACADES.put(worldPosition.asLong(), getFacadeBlock().defaultBlockState());
            CHUNK_FACADES.computeIfAbsent(SectionPos.asLong(worldPosition), p -> new LongOpenHashSet())
                    .add(worldPosition.asLong());
        } else {
            FACADES.remove(worldPosition.asLong());
            LongSet chunkList = CHUNK_FACADES.getOrDefault(SectionPos.asLong(worldPosition), null);
            if (chunkList != null) {
                chunkList.remove(worldPosition.asLong());
            }
        }
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(ConduitBundleRenderState.PROPERTY, ConduitBundleRenderState.of(this)).build();
    }

    // endregion

    // region Menu

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public List<Holder<Conduit<?, ?>>> getAllOpenableConduits(Direction side) {
        return conduits.stream().filter(c -> canOpenScreen(c, side)).toList();
    }

    public boolean canOpenScreen(Holder<Conduit<?, ?>> conduit, Direction side) {
        if (level == null) {
            return false;
        }

        if (!conduit.value().hasMenu()) {
            return false;
        }

        // If we've lost the conduit
        if (!hasConduitStrict(conduit)) {
            return false;
        }

        // Cannot create a connection to a bundle
        if (level.getBlockEntity(getBlockPos().relative(side)) instanceof ConduitBundleBlockEntity) {
            return false;
        }

        // TODO: This should be cached and updated whenever neighbors change...
        return conduit.value().canForceConnectToBlock(level, getBlockPos(), side);
    }

    @Override
    public ItemInteractionResult onWrenched(UseOnContext context) {
        if (level == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Get hit conduit
        var side = context.getClickedFace();
        var conduit = shape.getConduit(context.getClickedPos(), context.getHitResult());
        if (conduit == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        var player = context.getPlayer();
        if (player != null && player.isSteppingCarefully()) {
            removeConduit(conduit, player);
            if (isEmpty()) {
                level.setBlock(getBlockPos(), getBlockState().getFluidState().createLegacyBlock(),
                        level.isClientSide ? Block.UPDATE_ALL_IMMEDIATE : Block.UPDATE_ALL);
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        // Get connection
        var conduitConnection = shape.getConnectionFromHit(context.getClickedPos(), context.getHitResult());
        if (conduitConnection != null) {

            // Disable the connection
            setConnectionStatus(conduitConnection.getSecond(), conduitConnection.getFirst(), ConnectionStatus.DISABLED);

            // If we were connected to another bundle, we need to sever the graph
            if (level.getBlockEntity(getBlockPos()
                    .relative(conduitConnection.getFirst())) instanceof ConduitBundleBlockEntity neighborBundle) {
                neighborBundle.setConnectionStatus(conduitConnection.getSecond(),
                        conduitConnection.getFirst().getOpposite(), ConnectionStatus.DISABLED);

                if (level instanceof ServerLevel serverLevel) {
                    ConduitNode thisNode = getConduitNode(conduitConnection.getSecond());
                    ConduitNode otherNode = neighborBundle.getConduitNode(conduitConnection.getSecond());

                    if (thisNode.isValid() && otherNode.isValid()) {
                        var thisNetwork = thisNode.getNetwork();
                        var otherNetwork = otherNode.getNetwork();

                        if (thisNetwork == otherNetwork) {
                            thisNetwork.disconnect(thisNode, otherNode,
                                    n -> ConduitNetworkSavedData.onNetworkCreated(serverLevel, n));
                        }

                        bundleChanged();
                    } else {
                        // TODO: Warn, this is a bad place to be.
                    }
                }
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        // Attempt to make a new forced connection
        var status = getConnectionStatus(conduit, side);
        if (!status.isConnected()) {
            tryConnectTo(conduit, side, true);
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    // endregion

    // region Capability Proxies

    public static <TCap, TContext> ICapabilityProvider<ConduitBundleBlockEntity, TContext, TCap> createCapabilityProvider(
            BlockCapability<TCap, TContext> cap) {
        return (be, context) -> {
            for (Holder<Conduit<?, ?>> conduit : be.getConduits()) {
                var proxiedCap = getProxiedCapability(cap, be, conduit, context);
                if (proxiedCap != null) {
                    return proxiedCap;
                }
            }

            return null;
        };
    }

    @Nullable
    private static <TCap, TContext> TCap getProxiedCapability(BlockCapability<TCap, TContext> capability, ConduitBundleBlockEntity blockEntity,
        Holder<Conduit<?, ?>> conduit, @Nullable TContext context) {
        if (blockEntity.level == null) {
            return null;
        }

        var node = blockEntity.conduitNodes.get(conduit);

        // Forbid unloaded nodes from being queried
        if (node != null && !node.isLoaded()) {
            return null;
        }

        return conduit.value().proxyCapability(blockEntity.level, node, capability, context);
    }

    // endregion

    // region Conduits

    public List<Holder<Conduit<?, ?>>> getConduits() {
        return Collections.unmodifiableList(conduits);
    }

    @Override
    public boolean hasConduitByType(Holder<Conduit<?, ?>> conduit) {
        return conduits.stream().anyMatch(c -> c.value().canConnectToConduit(conduit));
    }

    @Override
    public boolean hasConduitByType(ConduitType<?> conduitType) {
        return conduits.stream().anyMatch(c -> c.value().type() == conduitType);
    }

    @Override
    public boolean hasConduitStrict(Holder<Conduit<?, ?>> conduit) {
        return conduits.contains(conduit);
    }

    @Nullable
    public Holder<Conduit<?, ?>> getConduitByType(ConduitType<?> conduitType) {
        return conduits.stream().filter(c -> c.value().type() == conduitType).findFirst().orElse(null);
    }

    @Override
    public boolean isEmpty() {
        return conduits.isEmpty() && !hasFacade();
    }

    @Override
    public boolean isFull() {
        return conduits.size() == MAX_CONDUITS;
    }

    /**
     * Finds a conduit which is replaceable by the given conduit.
     * @param possibleReplacement the conduit that may replace another.
     * @return the conduit that can be replaced, or empty if none can be replaced.
     */
    private Optional<Holder<Conduit<?, ?>>> findReplacementCandidate(Holder<Conduit<?, ?>> possibleReplacement) {
        return conduits.stream()
                .filter(existingConduit -> existingConduit.value().canBeReplacedBy(possibleReplacement))
                .findFirst();
    }

    /**
     * @param conduit the conduit to check for.
     * @return whether the provided conduit is compatible with the other conduits in the bundle.
     */
    private boolean isConduitCompatibleWithExisting(Holder<Conduit<?, ?>> conduit) {
        return conduits.stream().allMatch(existingConduit -> existingConduit.value().canBeInSameBundle(conduit));
    }

    @Override
    public boolean canAddConduit(Holder<Conduit<?, ?>> conduit) {
        if (level == null) {
            return false;
        }

        if (isFull()) {
            return false;
        }

        if (hasConduitStrict(conduit)) {
            return false;
        }

        if (findReplacementCandidate(conduit).isPresent()) {
            return true;
        }

        // If there are no replacement opportunities, we cannot have a conduit of this
        // type.
        if (hasConduitByType(conduit)) {
            return false;
        }

        return isConduitCompatibleWithExisting(conduit);
    }

    @Override
    public AddConduitResult addConduit(Holder<Conduit<?, ?>> conduit, @Nullable Direction primaryConnectionSide,
            @Nullable Player player) {
        if (level == null) {
            return new AddConduitResult.Blocked();
        }

        if (isFull()) {
            return new AddConduitResult.Blocked();
        }

        if (hasConduitStrict(conduit)) {
            return new AddConduitResult.Blocked();
        }

        // Attempt to upgrade an existing conduit.
        AddConduitResult result;
        var replacementCandidate = findReplacementCandidate(conduit);
        if (replacementCandidate.isPresent()) {
            int replacementIndex = conduits.indexOf(replacementCandidate.get());
            conduits.set(replacementIndex, conduit);

            // Add connections entry
            var oldConnectionContainer = conduitConnections.remove(replacementCandidate.get());
            conduitConnections.put(conduit, oldConnectionContainer.copyFor(conduit));

            // Remove caches for the replaced conduit
            neighbouringCapabilityCaches.remove(replacementCandidate.get());

            if (!level.isClientSide()) {
                ConduitNode oldNode = conduitNodes.remove(replacementCandidate.get());

                ConduitNode newNode;
                if (oldNode != null) {
                    // Copy data into the node
                    newNode = new ConduitNode(conduit, getBlockPos(), oldNode.getNodeData());
                    conduit.value().onRemoved(oldNode, level, getBlockPos());
                    oldNode.getNetwork().remove(oldNode);
                    oldNode.detach();
                } else {
                    newNode = new ConduitNode(conduit, getBlockPos());
                }

                setNode(conduit, newNode);
                conduit.value().onCreated(newNode, level, getBlockPos(), player);
            }

            result = new AddConduitResult.Upgrade(replacementCandidate.get());
        } else {
            // If there are no replacement opportunities, we cannot have a conduit of this
            // type.
            if (hasConduitByType(conduit)) {
                return new AddConduitResult.Blocked();
            }

            // Ensure there are no incompatible conduits.
            if (!isConduitCompatibleWithExisting(conduit)) {
                return new AddConduitResult.Blocked();
            }

            // Ensure the conduits list is sorted correctly.
            int id = ConduitSorter.getSortIndex(conduit);
            var addBefore = conduits.stream().filter(c -> ConduitSorter.getSortIndex(c) > id).findFirst();
            if (addBefore.isPresent()) {
                conduits.add(conduits.indexOf(addBefore.get()), conduit);
            } else {
                conduits.add(conduit);
            }

            // Add connections entry
            conduitConnections.put(conduit, new ConnectionContainer(conduit));

            if (!level.isClientSide()) {
                // Create the new node
                ConduitNode node = new ConduitNode(conduit, getBlockPos());

                // Add the node
                setNode(conduit, node);

                // NeoForge contains a patch that calls onLoad after the conduit has been placed
                // if it's the first one, so onCreated would be called twice. it's easier to
                // detect here
                if (conduits.size() != 1) {
                    conduit.value().onCreated(node, level, getBlockPos(), player);
                }
            }

            result = new AddConduitResult.Insert();
        }

        // Now attempt to make connections, starting from the "primary" side (clicked or
        // facing direction)
        if (primaryConnectionSide != null) {
            tryConnectTo(conduit, primaryConnectionSide, false);
        }

        for (Direction side : Direction.values()) {
            if (side != primaryConnectionSide) {
                tryConnectTo(conduit, side, false);
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            ConduitNetworkSavedData.onNetworkCreated(serverLevel, getConduitNode(conduit).getNetwork());
        }

        if (result instanceof AddConduitResult.Upgrade(Holder<Conduit<?, ?>> replacedConduit)
                && !replacedConduit.value().canConnectToConduit(conduit)) {
            removeNeighborConnections(replacedConduit);
        }

        bundleChanged();
        return result;
    }

    @Override
    public void removeConduit(Holder<Conduit<?, ?>> conduit, @Nullable Player player) {
        removeConduit(conduit, player, getBlockPos());
    }

    // Intended for internal use
    // TODO: Can this be done better?
    public void removeConduit(Holder<Conduit<?, ?>> conduit, @Nullable Player player, BlockPos dropItemPos) {
        if (level == null) {
            return;
        }

        if (!hasConduitStrict(conduit)) {
            if (!FMLLoader.isProduction()) {
                throw new IllegalArgumentException(
                        "Conduit: " + conduit.getRegisteredName() + " is not present in conduit bundle "
                                + Arrays.toString(conduits.stream().map(Holder::getRegisteredName).toArray()));
            }

            return;
        }

        // Drop the conduit and it's inventory items.
        if (!level.isClientSide()) {
            if (player != null && !player.getAbilities().instabuild) {
                dropItem(ConduitBlockItem.getStackFor(conduit, 1), dropItemPos);
                for (Direction side : Direction.values()) {
                    dropConnectionItems(conduit, side, dropItemPos);
                }
            }
        }

        // Node remove event
        if (!level.isClientSide()) {
            var node = getConduitNode(conduit);
            conduit.value().onRemoved(node, level, getBlockPos());
            node.detach();

            // Remove from the graph.
            if (node.isValid()) {
                node.getNetwork().remove(node, n -> ConduitNetworkSavedData.onNetworkCreated((ServerLevel) level, n));
            }
        }

        // Remove from the bundle
        conduits.remove(conduit);
        conduitConnections.remove(conduit);
        conduitNodes.remove(conduit);
        neighbouringCapabilityCaches.remove(conduit);

        // Remove neighbour connections
        removeNeighborConnections(conduit);

        // Fire redstone updates, if applicable.
        if (conduit.value().type() == ConduitTypes.REDSTONE.get()) {
            for (Direction side : Direction.values()) {
                redstoneConduitChanged(side);
            }
        }

        bundleChanged();
    }

    /**
     * Removes connections to neigbouring bundles to the given conduit.
     * @param conduit The conduit in this conduit that should be disconnected from other conduits.
     */
    public void removeNeighborConnections(Holder<Conduit<?, ?>> conduit) {
        for (Direction dir : Direction.values()) {
            removeNeighborConnection(conduit, dir);
        }
    }

    private void removeNeighborConnection(Holder<Conduit<?, ?>> conduit, Direction side) {
        if (level == null) {
            return;
        }

        if (!(level.getBlockEntity(getBlockPos().relative(side)) instanceof ConduitBundleBlockEntity neighborBundle)) {
            return;
        }

        neighborBundle.disconnect(conduit, side.getOpposite());

        // TODO: Do we need an equivalent to this?
//        if (level instanceof ServerLevel serverLevel) {
//            if (neighborBundle.hasConduitByType(conduit)) {
//                Optional.of(neighborBundle.getConduitNode(conduit))
//                        .map(ConduitGraphObject::getGraph)
//                        .filter(Objects::nonNull)
//                        .ifPresent(graph -> ConduitSavedData.addPotentialGraph(conduit, graph, serverLevel));
//            }
//        }
    }

    private void dropItem(ItemStack stack) {
        dropItem(stack, getBlockPos());
    }

    private void dropItem(ItemStack stack, BlockPos pos) {
        if (level != null) {
            level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(),
                    stack.copy()));
        }
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    public ConduitNode getConduitNode(Holder<Conduit<?, ?>> conduit) {
        if (!hasConduitByType(conduit)) {
            throw new IllegalStateException("Conduit not found in bundle.");
        }

        return conduitNodes.get(conduit);
    }

    @Override
    @Nullable
    public CompoundTag getConduitExtraWorldData(Holder<Conduit<?, ?>> conduit) {
        if (level != null && !level.isClientSide()) {
            return conduit.value().getExtraWorldData(this, getConduitNode(conduit));
        }

        return clientConduitExtraWorldData.get(conduit);
    }

    // Synced by the GUI, only available on the server BE.
    @EnsureSide(EnsureSide.Side.SERVER)
    @Override
    @Nullable
    public CompoundTag getConduitExtraGuiData(Holder<Conduit<?, ?>> conduit, Direction side) {
        return conduit.value().getExtraGuiData(this, getConduitNode(conduit), side);
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private void setNode(Holder<Conduit<?, ?>> conduit, ConduitNode loadedNode) {
        conduitNodes.put(conduit, loadedNode);

        // Attach to the node to provide connection data and inventory.
        loadedNode.attach(this, conduit);
    }

    // endregion

    // region Connections

    @Override
    public List<Holder<Conduit<?, ?>>> getConnectedConduits(Direction side) {
        return conduitConnections.entrySet()
                .stream()
                .filter(e -> e.getValue().getStatus(side).isConnected())
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparingInt(ConduitSorter::getSortIndex))
                .toList();
    }

    @Override
    @Nullable
    public IItemHandlerModifiable getConnectionInventory(Holder<Conduit<?, ?>> conduit, Direction side) {
        if (!hasConduitStrict(conduit)) {
            throw new IllegalStateException("Conduit not found in bundle.");
        }

        return conduitConnections.computeIfAbsent(conduit, ConnectionContainer::new).getInventory(side);
    }

    @Override
    public ConnectionStatus getConnectionStatus(Holder<Conduit<?, ?>> conduit, Direction side) {
        return conduitConnections.computeIfAbsent(conduit, ConnectionContainer::new).getStatus(side);
    }

    @Override
    public ConnectionConfig getConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side) {
        return conduitConnections.get(conduit).getConfig(side);
    }

    @Override
    public <T extends ConnectionConfig> T getConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side,
            ConnectionConfigType<T> type) {
        var config = conduitConnections.get(conduit).getConfig(side);
        if (config.type() != type) {
            throw new IllegalStateException("Connection config type mismatch.");
        }

        // noinspection unchecked
        return (T) config;
    }

    @Override
    public void setConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side, ConnectionConfig config) {
        if (config.type() != conduit.value().connectionConfigType()) {
            throw new IllegalArgumentException("Connection config is not the right type for this conduit.");
        }

        conduitConnections.get(conduit).setConfig(side, config);
        if (config.isConnected() && getConnectionStatus(conduit, side) != ConnectionStatus.CONNECTED_BLOCK) {
            setConnectionStatus(conduit, side, ConnectionStatus.CONNECTED_BLOCK);
        } else if (!config.isConnected()) {
            setConnectionStatus(conduit, side, ConnectionStatus.DISABLED);
        } else {
            // Fire on config changed manually if we've not changed any connections
            if (level != null && !level.isClientSide()) {
                getConduitNode(conduit).onConfigChanged();
            }
        }

        bundleChanged();
    }

    public void setConnectionStatus(Holder<Conduit<?, ?>> conduit, Direction side, ConnectionStatus status) {
        if (!hasConduitStrict(conduit)) {
            throw new IllegalArgumentException("Conduit is not present in this bundle.");
        }

        conduitConnections.get(conduit).setStatus(side, status);
        onConnectionsUpdated(conduit);

        bundleChanged();
    }

    // TODO: This needs a better name or to handle blocks as well as conduits before
    // it can be exposed via the interface.
    public boolean canConnectTo(Holder<Conduit<?, ?>> conduit, Direction side, ConduitNode otherNode,
            boolean isForcedConnection) {
        if (level == null) {
            return false;
        }

        if (!doTypesMatch(conduit)) {
            return false;
        }

        if (conduit.value().hasServerConnectionChecks()) {
            if (level.isClientSide()) {
                // If this has server-side logic, don't continue locally.
                return false;
            }

            // Gated behind hasServerConnectionChecks to ensure conduit devs do not forget
            // to override both.
            if (!conduit.value().canConnectConduits(conduitNodes.get(conduit), otherNode)) {
                return false;
            }
        }

        return isForcedConnection || conduitConnections.get(conduit).getStatus(side) != ConnectionStatus.DISABLED;
    }

    private boolean doTypesMatch(Holder<Conduit<?, ?>> conduitToMatch) {
        for (Holder<Conduit<?, ?>> conduit : conduits) {
            if (conduit.value().canConnectToConduit(conduitToMatch)) {
                return true;
            }
        }

        return false;
    }

    public boolean tryConnectTo(Holder<Conduit<?, ?>> conduit, Direction side, boolean isForcedConnection) {
        if (level == null) {
            return false;
        }

        if (!hasConduitStrict(conduit)) {
            throw new IllegalArgumentException("Conduit is not present in this bundle.");
        }

        // Do not attempt to connect if we're not forcing a disabled connection
        ConnectionStatus currentStatus = conduitConnections.get(conduit).getStatus(side);
        if ((!isForcedConnection && currentStatus == ConnectionStatus.DISABLED)) {
            return false;
        }

        if (level.getBlockEntity(
                getBlockPos().relative(side)) instanceof ConduitBundleBlockEntity neighbourConduitBundle) {
            var node = conduitNodes.get(conduit);

            // Connect to another bundle which has a compatible conduit.
            if (neighbourConduitBundle.canConnectTo(conduit, side.getOpposite(), node, isForcedConnection)) {
                // Make connections to both sides
                connectConduit(conduit, side);
                neighbourConduitBundle.connectConduit(conduit, side.getOpposite());

                // Fire node connection events
                if (!level.isClientSide()) {
                    var neighbourNode = neighbourConduitBundle.getConduitNode(conduit);
                    conduit.value().onConnectTo(node, neighbourNode);
                    conduit.value().onConnectTo(neighbourNode, node);

                    // Connect the neighbor to our node.
                    node.getNetwork()
                            .connect(node, neighbourNode,
                                    n -> ConduitNetworkSavedData.onNetworkDiscarded((ServerLevel) level, n));
                }
                return true;
            }

            disconnect(conduit, side);
            return false;
        } else if (conduit.value().canConnectToBlock(level, getBlockPos(), side)
                || (isForcedConnection && conduit.value().canForceConnectToBlock(level, getBlockPos(), side))) {
            connectBlock(conduit, side);
            return true;
        }

        return false;
    }

    public void onConnectionsUpdated(Holder<Conduit<?, ?>> conduit) {
        if (level != null && !level.isClientSide) {
            var node = getConduitNode(conduit);

            Set<Direction> connectedSides = Arrays.stream(Direction.values())
                    .filter(direction -> getConnectionStatus(conduit, direction).isConnected())
                    .collect(Collectors.toSet());

            conduit.value().onConnectionsUpdated(node, level, getBlockPos(), connectedSides);
            node.getNetwork().onNodeUpdated(node);
        }
    }

    private void connectConduit(Holder<Conduit<?, ?>> conduit, Direction side) {
        conduitConnections.computeIfAbsent(conduit, ConnectionContainer::new)
                .setStatus(side, ConnectionStatus.CONNECTED_CONDUIT);
        onConnectionsUpdated(conduit);

        bundleChanged();
    }

    private void connectBlock(Holder<Conduit<?, ?>> conduit, Direction side) {
        conduitConnections.computeIfAbsent(conduit, ConnectionContainer::new)
                .setStatus(side, ConnectionStatus.CONNECTED_BLOCK);
        onConnectionsUpdated(conduit);

        bundleChanged();
    }

    // TODO: poorly named, we're disconnecting from another conduit on the given
    // side.
    private void disconnect(Holder<Conduit<?, ?>> conduit, Direction side) {
        boolean hasChanged = false;
        for (var c : conduits) {
            if (c.value().canConnectToConduit(conduit)) {
                conduitConnections.computeIfAbsent(c, ConnectionContainer::new)
                        .setStatus(side, ConnectionStatus.DISCONNECTED);
                onConnectionsUpdated(c);
                hasChanged = true;
            }
        }

        if (hasChanged) {
            bundleChanged();
        }
    }

    private void dropConnectionItems(Holder<Conduit<?, ?>> conduit, Direction side, BlockPos pos) {
        var inventory = getConnectionInventory(conduit, side);
        if (inventory == null) {
            return;
        }

        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                dropItem(stack, pos);
                inventory.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    // TODO: I've not properly reviewed this method.
    public void updateConnections(Level level, BlockPos pos, @Nullable BlockPos fromPos, boolean shouldActivate) {
        if (fromPos != null && level.getBlockEntity(fromPos) instanceof ConduitBundleBlockEntity) {
            return;
        }

        for (Direction side : Direction.values()) {
            for (var conduit : conduits) {
                if (shouldActivate && conduit.value().hasConnectionDelay()) {
                    checkConnection = checkConnection.activate();
                    continue;
                }

                var currentStatus = getConnectionStatus(conduit, side);

                if (currentStatus.canConnect()) {
                    tryConnectTo(conduit, side, false);
                } else if (currentStatus.isEndpoint()) {
                    if (!conduit.value().canForceConnectToBlock(level, getBlockPos(), side)) {
                        disconnect(conduit, side);
                        onConnectionsUpdated(conduit);
                    }
                }
            }
        }
    }

    // endregion

    // region Node Interactions

    public void markNodesDirty() {
        hasDirtyNodes = true;
    }

    @Nullable
    public <TCapability> TCapability getNeighborSidedCapability(Holder<Conduit<?, ?>> conduit,
            BlockCapability<TCapability, Direction> capability, Direction side) {
        // Doesn't use EnderBlockEntity's capability cache so that we can bin capability
        // caches that aren't needed when conduits are removed.
        // Probably an "early optimization" but I don't think this really hurts.
        if (level instanceof ServerLevel serverLevel) {
            var capabilityCache = neighbouringCapabilityCaches.computeIfAbsent(conduit,
                    c -> new NeighboringCapabilityCaches());
            return capabilityCache.getSidedCapability(capability, serverLevel, getBlockPos(), side);
        }

        return null;
    }

    @Nullable
    public <TCapability> TCapability getNeighborVoidCapability(Holder<Conduit<?, ?>> conduit,
            BlockCapability<TCapability, Void> capability, Direction side) {
        // Doesn't use EnderBlockEntity's capability cache so that we can bin capability
        // caches that aren't needed when conduits are removed.
        // Probably an "early optimization" but I don't think this really hurts.
        if (level instanceof ServerLevel serverLevel) {
            var capabilityCache = neighbouringCapabilityCaches.computeIfAbsent(conduit,
                    c -> new NeighboringCapabilityCaches());
            return capabilityCache.getVoidCapability(capability, serverLevel, getBlockPos(), side);
        }

        return null;
    }

    // region Redstone Cache

    private boolean hasRedstoneSignal;

    public void updateNeighborRedstone() {
        if (level == null) {
            hasRedstoneSignal = false;
        } else {
            hasRedstoneSignal = level.hasNeighborSignal(getBlockPos());
        }

        if (level != null && !level.isClientSide()) {
            for (var node : conduitNodes.values()) {
                node.onRedstoneChanged();
            }
        }
    }

    public boolean hasRedstoneSignal(@Nullable DyeColor signalColor) {
        if (hasRedstoneSignal) {
            return true;
        }

        // If we have no signal color, do not attempt to query a redstone conduit
        if (signalColor == null) {
            return false;
        }

        var redstoneConduit = getConduitByType(ConduitTypes.REDSTONE.get());
        if (redstoneConduit == null) {
            return false;
        }

        var node = getConduitNode(redstoneConduit);
        var network = node.getNetwork();
        if (network == null) {
            return false;
        }

        var context = network.getContext(ConduitTypes.ContextTypes.REDSTONE.get());
        if (context == null) {
            return false;
        }

        return context.isActive(signalColor);
    }

    // endregion

    // endregion

    // region Facades

    @Override
    public boolean hasFacade() {
        return !facadeProvider.isEmpty()
                && facadeProvider.getCapability(ConduitCapabilities.CONDUIT_FACADE_PROVIDER) != null;
    }

    @Override
    public Block getFacadeBlock() {
        if (facadeProvider.isEmpty()) {
            throw new IllegalStateException("This bundle has no facade provider.");
        }

        var provider = facadeProvider.getCapability(ConduitCapabilities.CONDUIT_FACADE_PROVIDER);
        if (provider == null) {
            // TODO: How to handle this error gracefully?
            // For now default to a bedrock facade.
            return Blocks.BEDROCK;
        }

        return provider.block();
    }

    @Override
    public FacadeType getFacadeType() {
        if (facadeProvider.isEmpty()) {
            throw new IllegalStateException("This bundle has no facade provider.");
        }

        var provider = facadeProvider.getCapability(ConduitCapabilities.CONDUIT_FACADE_PROVIDER);
        if (provider == null) {
            return FacadeType.BASIC;
        }

        return provider.type();
    }

    @Override
    public ItemStack getFacadeProvider() {
        return facadeProvider;
    }

    @Override
    public void setFacadeProvider(ItemStack facadeProvider) {
        this.facadeProvider = facadeProvider.copyWithCount(1);
        bundleChanged();
    }

    public void dropFacadeItem() {
        dropItem(facadeProvider);
    }

    public void dropFacadeItem(BlockPos pos) {
        dropItem(facadeProvider, pos);
    }

    // endregion

    // region Network Sync

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag updateTag = super.getUpdateTag(registries);

        // TODO: Do not use saveAdditional, sync less data than it does...

        // Send conduit sync data
        ListTag nodeDataList = new ListTag();

        for (var conduit : conduits) {
            var node = getConduitNode(conduit);
            var clientDataTag = conduit.value().getExtraWorldData(this, node);
            if (clientDataTag != null && !clientDataTag.isEmpty()) {
                CompoundTag tag = new CompoundTag();
                tag.put("Conduit",
                        Conduit.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), conduit)
                                .getOrThrow());
                tag.put("Data", clientDataTag);
                nodeDataList.add(tag);
            }
        }

        updateTag.put(CONDUIT_CLIENT_WORLD_DATA_KEY, nodeDataList);
        return updateTag;
    }

    @Override
    public void handleUpdateTag(CompoundTag syncData, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(syncData, lookupProvider);

        // Load synced node data
        if (syncData.contains(CONDUIT_CLIENT_WORLD_DATA_KEY)) {
            clientConduitExtraWorldData.clear();

            ListTag nodeDataList = syncData.getList(CONDUIT_CLIENT_WORLD_DATA_KEY, Tag.TAG_COMPOUND);
            var serializationContext = lookupProvider.createSerializationContext(NbtOps.INSTANCE);
            for (int i = 0; i < nodeDataList.size(); i++) {
                CompoundTag nodeTag = nodeDataList.getCompound(i);
                var conduit = Conduit.CODEC.parse(serializationContext, nodeTag.get("Conduit")).getOrThrow();
                clientConduitExtraWorldData.put(conduit, nodeTag.getCompound("Data"));
            }
        }

        updateShape();
        updateModel();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt,
            HolderLookup.Provider lookupProvider) {
        CompoundTag compoundtag = pkt.getTag();
        if (!compoundtag.isEmpty()) {
            handleUpdateTag(compoundtag, lookupProvider);
        }
    }

    // endregion

    // region Serialization

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        // TODO: Do this in clear removed instead?
        if (!level.isClientSide()) {
            loadFromSavedData();
        }
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private void loadFromSavedData() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ConduitNetworkSavedData savedData = ConduitNetworkSavedData.get(serverLevel);
        for (int i = 0; i < conduits.size(); i++) {
            Holder<Conduit<?, ?>> type = conduits.get(i);
            loadConduitFromSavedData(savedData, type, i);
        }

        lazyNodeData = null;
        lazyNodeNBT = null;
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private void loadConduitFromSavedData(ConduitNetworkSavedData savedData, Holder<Conduit<?, ?>> conduit,
            int typeIndex) {
        if (level == null || level.isClientSide()) {
            return;
        }

        ConduitNode node = savedData.claimNode(conduit, this.worldPosition);
        if (node == null && conduitNodes.get(conduit) == null) {
            // Attempt to recover node data
            NodeData nodeData = null;
            if (lazyNodeData != null && lazyNodeData.containsKey(conduit)) {
                nodeData = lazyNodeData.remove(conduit);
            }

            if (nodeData == null) {
                // Attempt to load legacy recovery data.
                ConduitDataContainer dataContainer = null;
                if (lazyNodeNBT != null && typeIndex < lazyNodeNBT.size()) {
                    dataContainer = ConduitDataContainer.parse(level.registryAccess(),
                            lazyNodeNBT.getCompound(typeIndex));
                }

                if (dataContainer != null) {
                    node = new ConduitNode(conduit, getBlockPos(), dataContainer);
                } else {
                    node = new ConduitNode(conduit, getBlockPos());
                }
            } else {
                node = new ConduitNode(conduit, getBlockPos(), nodeData);
            }

            setNode(conduit, node);
            lazyNodes.put(conduit, node);

            ConduitNetworkSavedData.onNetworkCreated((ServerLevel) level, node.getNetwork());
        } else if (node != null) {
            setNode(conduit, node);
        }
    }

    private boolean isChunkUnload = false;

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        isChunkUnload = true;

        if (level == null) {
            return;
        }

        if (level instanceof ServerLevel serverLevel) {
            var savedData = ConduitNetworkSavedData.get(serverLevel);

            for (var conduit : conduits) {
                var node = conduitNodes.get(conduit);
                conduit.value().onRemoved(node, level, getBlockPos());
                node.detach();
                savedData.returnNode(conduit, this.worldPosition, node);
            }
        } else {
            CHUNK_FACADES.remove(SectionPos.asLong(worldPosition));
            FACADES.remove(worldPosition.asLong());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        // Remove all conduits and the facade if this block is being destroyed (not
        // unloaded).
        if (!isChunkUnload) {
            var allConduits = List.copyOf(getConduits());
            for (var conduit : allConduits) {
                removeConduit(conduit, null);
            }

            setFacadeProvider(ItemStack.EMPTY);
        }

        if (level != null && level.isClientSide()) {
            FACADES.remove(worldPosition.asLong());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        var serializationContext = registries.createSerializationContext(NbtOps.INSTANCE);

        // NEW: Save node data in case of need for recovery
        ListTag nodeData = new ListTag();
        for (Holder<Conduit<?, ?>> conduit : conduits) {
            if (!conduitNodes.containsKey(conduit)) {
                continue;
            }

            var data = conduitNodes.get(conduit).getNodeData();

            if (data != null && data.type().isPersistent()) {
                CompoundTag nodeTag = new CompoundTag();
                nodeTag.put("Conduit", Conduit.CODEC.encodeStart(serializationContext, conduit).getOrThrow());
                nodeTag.put("Data", NodeData.GENERIC_CODEC.encodeStart(serializationContext, data).getOrThrow());
                nodeData.add(nodeTag);
            }
        }
        tag.put(NODE_DATA_KEY, nodeData);
    }

    @Override
    protected void saveAdditionalSynced(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditionalSynced(tag, registries);

        if (!conduits.isEmpty()) {
            ListTag conduitList = new ListTag();
            for (var conduit : conduits) {
                conduitList
                        .add(Conduit.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), conduit)
                                .getOrThrow());
            }
            tag.put(CONDUITS_KEY, conduitList);

            // Save connections
            ListTag conduitConnectionsList = new ListTag();
            for (var conduit : conduits) {
                ListTag connectionsList = new ListTag();
                for (Direction side : Direction.values()) {
                    CompoundTag connectionTag = new CompoundTag();
                    connectionTag.putString("Side", side.getSerializedName());
                    connectionTag.putString("Status", getConnectionStatus(conduit, side).getSerializedName());

                    // Raw access to ensure we save the true data.
                    var config = conduitConnections.get(conduit).configs.get(side);
                    if (config != null && !config.equals(config.type().getDefault())) {
                        connectionTag.put("Config",
                                ConnectionConfig.GENERIC_CODEC
                                        .encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), config)
                                        .getOrThrow());
                    }

                    var inventory = conduitConnections.get(conduit).inventories.get(side);
                    if (inventory != null) {
                        ListTag inventoryListTag = new ListTag();

                        boolean shouldSave = false;
                        for (int i = 0; i < inventory.getSlots(); i++) {
                            ItemStack stack = inventory.getStackInSlot(i);
                            shouldSave |= !stack.isEmpty();
                            inventoryListTag.add(stack.saveOptional(registries));
                        }

                        if (shouldSave) {
                            connectionTag.put("Inventory", inventoryListTag);
                        }
                    }

                    connectionsList.add(connectionTag);
                }

                conduitConnectionsList.add(connectionsList);
            }

            tag.put(CONNECTIONS_KEY, conduitConnectionsList);
        }

        if (!facadeProvider.isEmpty()) {
            tag.put(FACADE_PROVIDER_KEY, facadeProvider.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains(ConduitNBTKeys.CONDUIT_BUNDLE)) {
            // Convert the legacy bundle to the new format
            var bundle = LegacyConduitBundle.parse(registries, tag.getCompound(ConduitNBTKeys.CONDUIT_BUNDLE));
            loadFromLegacyBundle(bundle);
        } else {
            // New save format
            conduits.clear();
            if (tag.contains(CONDUITS_KEY, Tag.TAG_LIST)) {
                // Get untyped list tag.
                ListTag conduitList = (ListTag) tag.get(CONDUITS_KEY);
                for (var conduitTag : conduitList) {
                    conduits.add(Conduit.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), conduitTag)
                            .getOrThrow());
                }
            }

            // Load connections
            conduitConnections.clear();
            if (tag.contains(CONNECTIONS_KEY)) {
                ListTag conduitConnectionsList = tag.getList(CONNECTIONS_KEY, Tag.TAG_LIST);

                for (int i = 0; i < conduitConnectionsList.size(); i++) {
                    ListTag connectionsList = conduitConnectionsList.getList(i);
                    Holder<Conduit<?, ?>> conduit = conduits.get(i);

                    ConnectionContainer connections = new ConnectionContainer(conduit);
                    for (int j = 0; j < connectionsList.size(); j++) {
                        CompoundTag connectionTag = connectionsList.getCompound(j);
                        Direction side = Direction.byName(connectionTag.getString("Side"));
                        ConnectionStatus status = ConnectionStatus.byName(connectionTag.getString("Status"));

                        if (status == null) {
                            status = ConnectionStatus.DISCONNECTED;
                        }

                        if (side != null) {
                            connections.setStatus(side, status);

                            if (connectionTag.contains("Config")) {
                                ConnectionConfig config = ConnectionConfig.GENERIC_CODEC
                                        .parse(registries.createSerializationContext(NbtOps.INSTANCE),
                                                connectionTag.get("Config"))
                                        .getOrThrow();
                                connections.setConfig(side, config);
                            }

                            if (connectionTag.contains("Inventory")) {
                                ListTag inventoryListTag = connectionTag.getList("Inventory", Tag.TAG_COMPOUND);
                                var inventory = connections.getInventory(side);

                                if (inventory != null) {
                                    if (inventory.getSlots() < inventoryListTag.size()) {
                                        // TODO: Log a warning
                                    }

                                    for (int k = 0; k < inventoryListTag.size() && k < inventory.getSlots(); k++) {
                                        ItemStack stack = ItemStack.parseOptional(registries,
                                                inventoryListTag.getCompound(k));
                                        inventory.setStackInSlot(k, stack);
                                    }
                                }
                            }
                        }
                    }

                    conduitConnections.put(conduit, connections);
                }
            }

            if (tag.contains(FACADE_PROVIDER_KEY)) {
                facadeProvider = ItemStack.parseOptional(registries, tag.getCompound(FACADE_PROVIDER_KEY));
            } else {
                facadeProvider = ItemStack.EMPTY;
            }
        }

        // Load node data used for recovery
        if (tag.contains(ConduitNBTKeys.CONDUIT_EXTRA_DATA)) {
            lazyNodeNBT = tag.getList(ConduitNBTKeys.CONDUIT_EXTRA_DATA, Tag.TAG_COMPOUND);
        } else if (tag.contains(NODE_DATA_KEY)) {
            var list = tag.getList(NODE_DATA_KEY, Tag.TAG_COMPOUND);
            lazyNodeData = new HashMap<>();

            var serializationContext = registries.createSerializationContext(NbtOps.INSTANCE);

            for (int i = 0; i < list.size(); i++) {
                var nodeTag = list.getCompound(i);
                var conduitParseResult = Conduit.CODEC.parse(serializationContext, nodeTag.get("Conduit"));
                if (conduitParseResult.isError()) {
                    continue;
                }

                var dataParseResult = NodeData.GENERIC_CODEC.parse(serializationContext, nodeTag.get("Data"));
                if (dataParseResult.isError()) {
                    continue;
                }

                lazyNodeData.put(conduitParseResult.getOrThrow(), dataParseResult.getOrThrow());
            }
        }
    }

    // endregion

    // Special casing for redstone conduits.
    private void redstoneConduitChanged(Direction side) {
        if (level != null) {
            BlockPos neighborPos = getBlockPos().relative(side);
            if (!level.getBlockState(neighborPos).is(getBlockState().getBlock())) {
                level.updateNeighborsAt(getBlockPos().relative(side), getBlockState().getBlock());
            }
        }
    }

    private class ConnectionContainer {
        private final Holder<Conduit<?, ?>> conduit;
        private final Map<Direction, ConnectionStatus> statuses = new EnumMap<>(Direction.class);
        private final Map<Direction, ConnectionConfig> configs = new EnumMap<>(Direction.class);
        private final Map<Direction, ConnectionInventory> inventories = new EnumMap<>(Direction.class);

        public ConnectionContainer(Holder<Conduit<?, ?>> conduit) {
            this.conduit = conduit;

            var defaultConfig = conduit.value().connectionConfigType().getDefault();
            for (Direction dir : Direction.values()) {
                statuses.put(dir, ConnectionStatus.DISCONNECTED);
                configs.put(dir, defaultConfig);
            }
        }

        public ConnectionContainer copyFor(Holder<Conduit<?, ?>> conduit) {
            var copy = new ConnectionContainer(conduit);
            copy.statuses.putAll(statuses);

            // Only copy connection config if compatible.
            if (this.conduit.value().connectionConfigType() == conduit.value().connectionConfigType()) {
                copy.configs.putAll(configs);
            }

            if (this.conduit.value().getInventorySize() > 0 && conduit.value().getInventorySize() > 0) {
                for (Direction side : Direction.values()) {
                    if (inventories.containsKey(side)) {
                        var inventory = inventories.get(side);
                        var inventoryCopy = Objects.requireNonNull(copy.getInventory(side));
                        for (int i = 0; i < Math.max(inventory.getSlots(), inventoryCopy.getSlots()); i++) {
                            inventoryCopy.setStackInSlot(i, inventory.getStackInSlot(i));
                        }
                    }
                }
            }

            return copy;
        }

        @Nullable
        public IItemHandlerModifiable getInventory(Direction side) {
            if (conduit.value().getInventorySize() <= 0) {
                return null;
            }

            return inventories.computeIfAbsent(side, s -> new ConnectionInventory());
        }

        public ConnectionStatus getStatus(Direction side) {
            return statuses.getOrDefault(side, ConnectionStatus.DISCONNECTED);
        }

        public void setStatus(Direction side, ConnectionStatus status) {
            statuses.put(side, status);

            if (status == ConnectionStatus.CONNECTED_BLOCK) {
                if (configs.containsKey(side)) {
                    var config = configs.get(side);
                    if (!config.isConnected()) {
                        configs.put(side, config.reconnected());
                    }
                }
            }

            if (conduit.value().type() == ConduitTypes.REDSTONE.get()) {
                redstoneConduitChanged(side);
            }
        }

        public ConnectionConfig getConfig(Direction side) {
            var defaultConfig = conduit.value().connectionConfigType().getDefault();
            var config = configs.getOrDefault(side, defaultConfig);

            // Ensure the connection type is correct.
            // If it isn't, revert to the default.
            if (config.type() != conduit.value().connectionConfigType()) {
                config = defaultConfig;
                configs.put(side, config);
                bundleChanged();
            }

            // We keep the old state in case the wrench is used, but UI will need to show
            // empty arrows.
            if (statuses.get(side) != ConnectionStatus.CONNECTED_BLOCK && config.isConnected()) {
                return config.disconnected();
            }

            return config;
        }

        public void setConfig(Direction side, ConnectionConfig config) {
            configs.put(side, config);

            if (conduit.value().type() == ConduitTypes.REDSTONE.get()) {
                redstoneConduitChanged(side);
            }
        }

        private class ConnectionInventory extends ItemStackHandler {
            public ConnectionInventory() {
                super(conduit.value().getInventorySize());
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return conduit.value().isItemValid(slot, stack);
            }

            @Override
            protected void onContentsChanged(int slot) {
                if (level != null) {
                    bundleChanged();
                }
            }
        }
    }

    private static class NeighboringCapabilityCaches {
        private final Map<Direction, Map<BlockCapability<?, Direction>, BlockCapabilityCache<?, Direction>>> directionalCaches = new EnumMap<>(
                Direction.class);
        private final Map<Direction, Map<BlockCapability<?, Void>, BlockCapabilityCache<?, Void>>> voidCaches = new EnumMap<>(
                Direction.class);

        /**
         * Get a capability for the given side of the node
         */
        @Nullable
        public <TCapability> TCapability getSidedCapability(BlockCapability<TCapability, Direction> capability,
                ServerLevel level, BlockPos conduitPos, Direction side) {
            var cacheMap = directionalCaches.computeIfAbsent(side, s -> new HashMap<>());
            var cache = cacheMap.computeIfAbsent(capability,
                    c -> BlockCapabilityCache.create(c, level, conduitPos.relative(side), side.getOpposite()));

            // noinspection unchecked
            return (TCapability) cache.getCapability();
        }

        /**
         * Get a capability for the given side of the node
         */
        @Nullable
        public <TCapability> TCapability getVoidCapability(BlockCapability<TCapability, Void> capability,
                ServerLevel level, BlockPos conduitPos, Direction side) {
            var cacheMap = voidCaches.computeIfAbsent(side, s -> new HashMap<>());
            var cache = cacheMap.computeIfAbsent(capability,
                    c -> BlockCapabilityCache.create(c, level, conduitPos.relative(side), null));

            // noinspection unchecked
            return (TCapability) cache.getCapability();
        }
    }

    public enum UpdateState {
        NONE, NEXT_NEXT, NEXT, INITIALIZED;

        public boolean isInitialized() {
            return this == INITIALIZED;
        }

        public UpdateState next() {
            return switch (this) {
            case NONE, INITIALIZED -> NONE;
            case NEXT_NEXT -> NEXT;
            case NEXT -> INITIALIZED;
            };
        }

        public UpdateState activate() {
            return NEXT_NEXT;
        }
    }

    // region Legacy Bundle Conversion

    // TODO: Ender IO 8 - Remove.

    // Matches the same data format as the original conduit bundle.
    // Enables us to convert between the new and old formats easily.
    private record LegacyConduitBundle(BlockPos pos, List<Holder<Conduit<?, ?>>> conduits,
            Map<Direction, ConduitConnection> connections, ItemStack facadeItem,
            Map<Holder<Conduit<?, ?>>, ConduitNode> conduitNodes) {

        public static final Codec<LegacyConduitBundle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(i -> i.pos),
                Conduit.CODEC.listOf().fieldOf("conduits").forGetter(i -> i.conduits),
                Codec.unboundedMap(Direction.CODEC, ConduitConnection.CODEC)
                        .fieldOf("connections")
                        .forGetter(i -> i.connections),
                ItemStack.OPTIONAL_CODEC.optionalFieldOf("facade", ItemStack.EMPTY).forGetter(i -> i.facadeItem),
                Codec.unboundedMap(Conduit.CODEC, ConduitNode.CODEC).fieldOf("nodes").forGetter(i -> i.conduitNodes))
                .apply(instance, LegacyConduitBundle::new));

        public static LegacyConduitBundle parse(HolderLookup.Provider lookupProvider, Tag tag) {
            return CODEC.decode(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag)
                    .getOrThrow()
                    .getFirst();
        }

        public static final class ConduitConnection {

            public static final Codec<ConduitConnection> CODEC = ConnectionState.CODEC.listOf(0, MAX_CONDUITS)
                    .xmap(ConduitConnection::new, i -> Arrays.stream(i.connectionStates).toList());

            private final ConnectionState[] connectionStates = Util.make(() -> {
                var states = new ConnectionState[MAX_CONDUITS];
                Arrays.fill(states, StaticConnectionStates.DISCONNECTED);
                return states;
            });

            private ConduitConnection(List<ConnectionState> connectionStates) {
                if (connectionStates.size() > MAX_CONDUITS) {
                    throw new IllegalArgumentException(
                            "Cannot store more than " + MAX_CONDUITS + " conduit types per bundle.");
                }

                for (var i = 0; i < connectionStates.size(); i++) {
                    this.connectionStates[i] = connectionStates.get(i);
                }
            }

            public ConnectionState getConnectionState(int index) {
                return connectionStates[index];
            }
        }
    }

    private void loadFromLegacyBundle(LegacyConduitBundle bundle) {
        // Copy the conduit list
        conduits = new ArrayList<>();
        conduits.addAll(bundle.conduits);

        // Copy facade provider
        facadeProvider = bundle.facadeItem.copy();

        // Copy legacy connections into the new bundle
        conduitConnections = new HashMap<>();
        for (var conduit : conduits) {
            int conduitIndex = conduits.indexOf(conduit);
            var connections = conduitConnections.computeIfAbsent(conduit, ConnectionContainer::new);

            for (Direction side : Direction.values()) {
                var legacySide = bundle.connections.get(side);

                var state = legacySide.getConnectionState(conduitIndex);

                if (state == StaticConnectionStates.CONNECTED || state == StaticConnectionStates.CONNECTED_ACTIVE) {
                    connections.setStatus(side, ConnectionStatus.CONNECTED_CONDUIT);
                } else if (state == StaticConnectionStates.DISCONNECTED) {
                    connections.setStatus(side, ConnectionStatus.DISCONNECTED);
                } else if (state == StaticConnectionStates.DISABLED) {
                    connections.setStatus(side, ConnectionStatus.DISABLED);
                } else if (state instanceof DynamicConnectionState dynamicState) {
                    connections.setStatus(side, ConnectionStatus.CONNECTED_BLOCK);

                    connections.setConfig(side,
                            conduit.value()
                                    .convertConnection(dynamicState.isInsert(), dynamicState.isExtract(),
                                            dynamicState.insertChannel(), dynamicState.extractChannel(),
                                            dynamicState.control(), dynamicState.redstoneChannel()));

                    // Import the inventory.
                    var inventory = getConnectionInventory(conduit, side);
                    if (inventory == null) {
                        continue;
                    }

                    int insertFilterSlot = conduit.value().getIndexForLegacySlot(SlotType.FILTER_INSERT);
                    int extractFilterSlot = conduit.value().getIndexForLegacySlot(SlotType.FILTER_EXTRACT);

                    if (insertFilterSlot >= 0) {
                        inventory.setStackInSlot(insertFilterSlot, dynamicState.filterInsert());
                    }

                    if (extractFilterSlot >= 0) {
                        inventory.setStackInSlot(extractFilterSlot, dynamicState.filterExtract());
                    }
                }
            }
        }
    }

    // endregion
}
