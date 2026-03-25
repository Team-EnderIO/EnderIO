package com.enderio.enderio.content.conduits.bundle;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.UseOnly;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.ConduitUtility;
import com.enderio.enderio.api.conduits.bundle.AddConduitResult;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.api.conduits.connection.ConnectionStatus;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.facade.FacadeType;
import com.enderio.enderio.api.conduits.network.node.NodeData;
import com.enderio.enderio.client.content.conduits.model.bundle.ConduitBundleRenderState;
import com.enderio.enderio.content.conduits.ConduitBlockItem;
import com.enderio.enderio.content.conduits.ConduitSorter;
import com.enderio.enderio.content.conduits.menu.ConduitMenu;
import com.enderio.enderio.content.conduits.network.ConduitNetworkSavedData;
import com.enderio.enderio.content.conduits.network.ConduitNodeImpl;
import com.enderio.enderio.content.conduits.network.IConduitNodeAttachment;
import com.enderio.enderio.foundation.block.entity.Wrenchable;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOConduitTypes;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

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
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class ConduitBundleBlockEntity extends EnderBlockEntity
        implements ConduitBundle, Wrenchable, ConduitMenu.ConnectionAccessor, IConduitNodeAttachment {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int MAX_CONDUITS = 9;

    @UseOnly(LogicalSide.CLIENT)
    public static final Map<ResourceKey<Level>, Long2ObjectMap<BlockState>> FACADES = new HashMap<>();

    @UseOnly(LogicalSide.CLIENT)
    public static final Map<ResourceKey<Level>, Long2ObjectMap<LongSet>> CHUNK_FACADES = new HashMap<>();

    private ItemStack facadeProvider = ItemStack.EMPTY;
    private List<Holder<Conduit<?, ?>>> conduits = new ArrayList<>();
    private Map<Holder<Conduit<?, ?>>, ConnectionContainer> conduitConnections = new HashMap<>();
    private final Map<Holder<Conduit<?, ?>>, ConduitNodeImpl> conduitNodes = new HashMap<>();

    // Capability caches
    private final Map<Holder<Conduit<?, ?>>, NeighboringCapabilityCaches> neighbouringCapabilityCaches = new HashMap<>();

    // Data recovery mechanism
    private final Map<Holder<Conduit<?, ?>>, ConduitNodeImpl> lazyNodes = new HashMap<>();
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

    // TODO: Temporary fix for GH-1140
    private boolean isLoading = false;

    public ConduitBundleBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.CONDUIT.get(), worldPosition, blockState);
    }

    // region Static Facade Access

    @UseOnly(LogicalSide.CLIENT)
    private static Long2ObjectMap<BlockState> getFacadesForDimension(ResourceKey<Level> dimension) {
        return FACADES.computeIfAbsent(dimension, k -> new Long2ObjectOpenHashMap<>());
    }

    @UseOnly(LogicalSide.CLIENT)
    private static Long2ObjectMap<LongSet> getChunkFacadesForDimension(ResourceKey<Level> dimension) {
        return CHUNK_FACADES.computeIfAbsent(dimension, k -> new Long2ObjectOpenHashMap<>());
    }

    // endregion

    @Override
    public void serverTick() {
        super.serverTick();

        if (level != null) {
            checkConnection = checkConnection.next();
            if (checkConnection.isInitialized()) {
                updateConnections(level, false);
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
        if (isLoading) {
            return;
        }

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
            ResourceKey<Level> dimension = level.dimension();
            getFacadesForDimension(dimension).put(worldPosition.asLong(), getFacadeBlock().defaultBlockState());
            getChunkFacadesForDimension(dimension).computeIfAbsent(SectionPos.asLong(worldPosition), p -> new LongOpenHashSet())
                    .add(worldPosition.asLong());
        } else {
            ResourceKey<Level> dimension = level.dimension();
            getFacadesForDimension(dimension).remove(worldPosition.asLong());
            LongSet chunkList = getChunkFacadesForDimension(dimension).getOrDefault(SectionPos.asLong(worldPosition), null);
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
    public InteractionResult onWrenched(UseOnContext context) {
        if (level == null) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        // Get hit conduit
        var side = context.getClickedFace();
        var conduit = shape.getConduit(context.getClickedPos(), context.getHitResult());
        if (conduit == null) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        var player = context.getPlayer();
        if (player != null && player.isSteppingCarefully()) {
            removeConduit(conduit, droppedItem -> {
                if (!player.getAbilities().instabuild) {
                    dropItem(droppedItem);
                }
            });

            if (isEmpty()) {
                level.setBlock(getBlockPos(), getBlockState().getFluidState().createLegacyBlock(),
                        level.isClientSide() ? Block.UPDATE_ALL_IMMEDIATE : Block.UPDATE_ALL);
            }

            return InteractionResult.SUCCESS;
        }

        // Get connection
        var conduitConnection = shape.getConnectionFromHit(context.getClickedPos(), context.getHitResult());
        if (conduitConnection != null) {
            disableNeighborConnection(conduitConnection);
            return InteractionResult.SUCCESS;
        }

        // Attempt to make a new forced connection
        var status = getConnectionStatus(conduit, side);
        if (!status.isConnected()) {
            tryConnectTo(conduit, side, true);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    // Exposed publicly for use in tests.
    public void disableNeighborConnection(Pair<Direction, Holder<Conduit<?, ?>>> conduitConnection) {
        // Disable the connection
        setConnectionStatus(conduitConnection.getSecond(), conduitConnection.getFirst(), ConnectionStatus.DISABLED);

        // If we were connected to another bundle, we need to sever the graph
        if (!(level.getBlockEntity(getBlockPos().relative(conduitConnection.getFirst())) instanceof ConduitBundleBlockEntity neighborBundle)) {
            return;
        }

        // Get the other conduit in the neighbor. May not be the exact same as the one we've just disconnected.
        var otherConduit = neighborBundle.getCompatibleConduit(conduitConnection.getSecond());
        if (otherConduit == null) {
            return;
        }

        neighborBundle.setConnectionStatus(otherConduit, conduitConnection.getFirst().getOpposite(), ConnectionStatus.DISABLED);

        if (level instanceof ServerLevel serverLevel) {
            ConduitNodeImpl thisNode = getConduitNode(conduitConnection.getSecond());
            ConduitNodeImpl otherNode = neighborBundle.getConduitNode(otherConduit);

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
    public boolean hasCompatibleConduit(Holder<Conduit<?, ?>> conduit) {
        return conduits.stream().anyMatch(c -> ConduitUtility.canConnectConduits(conduit, c));
    }

    @Override
    public boolean hasConduitOfType(ConduitType<?, ?> conduitType) {
        return conduits.stream().anyMatch(c -> c.value().type() == conduitType);
    }

    @Override
    public boolean hasConduitStrict(Holder<Conduit<?, ?>> conduit) {
        return conduits.contains(conduit);
    }

    @Nullable
    public Holder<Conduit<?, ?>> getConduitByType(ConduitType<?, ?> conduitType) {
        return conduits.stream().filter(c -> c.value().type() == conduitType).findFirst().orElse(null);
    }

    @Override
    public @Nullable Holder<Conduit<?, ?>> getCompatibleConduit(Holder<Conduit<?, ?>> neighbourConduit) {
        return conduits.stream()
            .filter(c -> ConduitUtility.canConnectConduits(c, neighbourConduit))
            .findFirst()
            .orElse(null);
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
                .filter(existingConduit -> ConduitUtility.canConduitReplace(possibleReplacement, existingConduit))
                .findFirst();
    }

    /**
     * @param conduit the conduit to check for.
     * @return whether the provided conduit is compatible with the other conduits in the bundle.
     */
    private boolean isConduitCompatibleWithExisting(Holder<Conduit<?, ?>> conduit) {
        // Ensure the incoming conduit can exist with other conduits *and* cannot connect to any inside the bundle
        return conduits.stream().allMatch(existingConduit ->
            existingConduit.value().type() != conduit.value().type() &&
            !ConduitUtility.canConnectConduits(conduit, existingConduit));
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
        if (hasCompatibleConduit(conduit)) {
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
                ConduitNodeImpl oldNode = conduitNodes.remove(replacementCandidate.get());

                ConduitNodeImpl newNode;
                if (oldNode != null) {
                    // Copy data into the node
                    newNode = new ConduitNodeImpl(conduit, getBlockPos(), oldNode.getNodeData());
                    conduit.value().onRemoved(oldNode, level, getBlockPos());
                    oldNode.getNetwork().remove(oldNode);
                    oldNode.detach();
                } else {
                    newNode = new ConduitNodeImpl(conduit, getBlockPos());
                }

                setNode(conduit, newNode);
                conduit.value().onCreated(newNode, level, getBlockPos(), player);
            }

            result = new AddConduitResult.Upgrade(replacementCandidate.get());
        } else {
            // If there are no replacement opportunities, we cannot have a conduit of this
            // type.
            if (hasCompatibleConduit(conduit)) {
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
                ConduitNodeImpl node = new ConduitNodeImpl(conduit, getBlockPos());

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

        if (result instanceof AddConduitResult.Upgrade(Holder<Conduit<?, ?>> replacedConduit) &&
            !ConduitUtility.canConnectConduits(conduit, replacedConduit)) {
            removeNeighborConnections(replacedConduit);
        }

        bundleChanged();
        return result;
    }

    @Override
    public void removeConduit(Holder<Conduit<?, ?>> conduit, @Nullable Consumer<ItemStack> droppedItemConsumer) {
        if (level == null) {
            return;
        }

        if (!hasConduitStrict(conduit)) {
            if (!FMLEnvironment.isProduction()) {
                throw new IllegalArgumentException(
                        "Conduit: " + conduit.getRegisteredName() + " is not present in conduit bundle "
                                + Arrays.toString(conduits.stream().map(Holder::getRegisteredName).toArray()));
            }

            return;
        }

        // Drop the conduit and it's inventory items.
        if (!level.isClientSide() && droppedItemConsumer != null) {
            // Drop the conduit item.
            droppedItemConsumer.accept(ConduitBlockItem.getStackFor(conduit, 1));

            // Empty connection inventories.
            for (Direction side : Direction.values()) {
                var inventory = getConnectionInventory(conduit, side);
                if (inventory == null) {
                    continue;
                }

                for (int i = 0; i < inventory.getSlots(); i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        droppedItemConsumer.accept(stack);
                        inventory.setStackInSlot(i, ItemStack.EMPTY);
                    }
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
        if (conduit.value().type() == EIOConduitTypes.REDSTONE.get()) {
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

        Holder<Conduit<?, ?>> neighborConduit = neighborBundle.getConduitByType(conduit.value().type());
        if (neighborConduit == null) {
            return;
        }

        neighborBundle.disconnect(neighborConduit, side.getOpposite());
    }

    private void dropItem(ItemStack stack) {
        if (level != null) {
            var center = getBlockPos().getCenter();
            level.addFreshEntity(new ItemEntity(level, center.x, center.y, center.z, stack.copy()));
        }
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    public ConduitNodeImpl getConduitNode(Holder<Conduit<?, ?>> conduit) {
        if (!hasConduitStrict(conduit)) {
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
    private void setNode(Holder<Conduit<?, ?>> conduit, ConduitNodeImpl loadedNode) {
        conduitNodes.put(conduit, loadedNode);

        // Attach to the node to provide connection data and inventory.
        loadedNode.attach(this);
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
        return conduitConnections.computeIfAbsent(conduit, ConnectionContainer::new).getConfig(side);
    }

    @Override
    public <T extends ConnectionConfig> T getConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side,
            ConnectionConfigType<T> type) {
        var config = conduitConnections.computeIfAbsent(conduit, ConnectionContainer::new).getConfig(side);
        if (config.type() != type) {
            throw new IllegalStateException("Connection config type mismatch.");
        }

        // noinspection unchecked
        return (T) config;
    }

    @Override
    public void setConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side, ConnectionConfig config) {
        if (config.type() != conduit.value().type().connectionConfigType()) {
            throw new IllegalArgumentException("Connection config is not the right type for this conduit.");
        }

        conduitConnections.computeIfAbsent(conduit, ConnectionContainer::new).setConfig(side, config);
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

        conduitConnections.computeIfAbsent(conduit, ConnectionContainer::new).setStatus(side, status);
        onConnectionsUpdated(conduit);

        bundleChanged();
    }

    // TODO: This needs a better name or to handle blocks as well as conduits before
    // it can be exposed via the interface.
    public boolean canConnectTo(Holder<Conduit<?, ?>> conduit, Direction side, ConduitNodeImpl otherNode,
            boolean isForcedConnection) {
        if (level == null) {
            return false;
        }

        Holder<Conduit<?, ?>> compatibleConduit = getCompatibleConduit(conduit);
        if (compatibleConduit == null) {
            return false;
        }

        if (compatibleConduit.value().hasServerConnectionChecks() ||
            conduit.value().hasServerConnectionChecks()) {
            if (level.isClientSide()) {
                // If this has server-side logic, don't continue locally.
                return false;
            }

            // Gated behind hasServerConnectionChecks to ensure conduit devs do not forget to override both.
            if (!compatibleConduit.value().canConnectConduits(conduitNodes.get(compatibleConduit), otherNode)) {
                return false;
            }
        }

        return isForcedConnection || getConnectionStatus(compatibleConduit, side) != ConnectionStatus.DISABLED;
    }

    public boolean tryConnectTo(Holder<Conduit<?, ?>> conduit, Direction side, boolean isForcedConnection) {
        if (level == null) {
            return false;
        }

        if (!hasConduitStrict(conduit)) {
            throw new IllegalArgumentException("Conduit is not present in this bundle.");
        }

        // Do not attempt to connect if we're not forcing a disabled connection
        ConnectionStatus currentStatus = getConnectionStatus(conduit, side);
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
                    // Find compatible conduit in neighbor
                    var neighbourConduit = neighbourConduitBundle.getCompatibleConduit(conduit);
                    if (neighbourConduit != null) {
                        var neighbourNode = neighbourConduitBundle.getConduitNode(neighbourConduit);
                        conduit.value().onConnectTo(node, neighbourNode);
                        neighbourConduit.value().onConnectTo(neighbourNode, node);

                        // Connect the neighbor to our node.
                        node.getNetwork()
                            .connect(node, neighbourNode,
                                n -> ConduitNetworkSavedData.onNetworkDiscarded((ServerLevel) level, n));
                    }
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
        if (level != null && !level.isClientSide()) {
            var node = getConduitNode(conduit);

            Set<Direction> connectedSides = Arrays.stream(Direction.values())
                    .filter(direction -> getConnectionStatus(conduit, direction).isConnected())
                    .collect(Collectors.toSet());

            conduit.value().onConnectionsUpdated(node, level, getBlockPos(), connectedSides);
            node.getNetwork().onNodeUpdated(node);
        }
    }

    private void connectConduit(Holder<Conduit<?, ?>> conduit, Direction side) {
        Holder<Conduit<?, ?>> compatibleConduit = getCompatibleConduit(conduit);
        if (compatibleConduit == null) {
            return;
        }

        setConnectionStatus(compatibleConduit, side, ConnectionStatus.CONNECTED_CONDUIT);
    }

    private void connectBlock(Holder<Conduit<?, ?>> conduit, Direction side) {
        setConnectionStatus(conduit, side, ConnectionStatus.CONNECTED_BLOCK);
    }

    // TODO: poorly named, we're disconnecting from another conduit on the given side.
    private void disconnect(Holder<Conduit<?, ?>> conduit, Direction side) {
        for (var c : conduits) {
            if (ConduitUtility.canConnectConduits(conduit, c)) {
                setConnectionStatus(c, side, ConnectionStatus.DISCONNECTED);
            }
        }
    }

    // TODO: I've not properly reviewed this method.
    public void updateConnections(Level level, boolean shouldActivate) {
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

    // endregion

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

        var redstoneConduit = getConduitByType(EIOConduitTypes.REDSTONE.get());
        if (redstoneConduit == null) {
            return false;
        }

        var node = getConduitNode(redstoneConduit);
        var network = node.getNetwork();
        if (network == null) {
            return false;
        }

        var context = network.getContext(EIOConduitTypes.ContextTypes.REDSTONE.get());
        if (context == null) {
            return false;
        }

        return context.isActive(signalColor);
    }

    // endregion

    // region Facades

    @Override
    public boolean hasFacade() {
        return !facadeProvider.isEmpty()
                && facadeProvider.getCapability(EnderIOCapabilities.CONDUIT_FACADE_PROVIDER) != null;
    }

    @Override
    public Block getFacadeBlock() {
        if (facadeProvider.isEmpty()) {
            throw new IllegalStateException("This bundle has no facade provider.");
        }

        var provider = facadeProvider.getCapability(EnderIOCapabilities.CONDUIT_FACADE_PROVIDER);
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

        var provider = facadeProvider.getCapability(EnderIOCapabilities.CONDUIT_FACADE_PROVIDER);
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

    // endregion

    // region Network Sync

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag updateTag = super.getUpdateTag(registries);

        try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
            var output = TagValueOutput.createWithContext(problemReporter, registries);

            // Write super's update tag
            output.store(updateTag);

            // Write client data.
            var nodeDataList = output.list(CONDUIT_CLIENT_WORLD_DATA_KEY, ConduitClientData.CODEC);

            for (var conduit : conduits) {
                var node = getConduitNode(conduit);
                var clientDataTag = conduit.value().getExtraWorldData(this, node);
                if (clientDataTag != null && !clientDataTag.isEmpty()) {
                    nodeDataList.add(new ConduitClientData(conduit, clientDataTag));
                }
            }

            return output.buildResult();
        }
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);

        var nodeDataList = input.listOrEmpty(CONDUIT_CLIENT_WORLD_DATA_KEY, ConduitClientData.CODEC);

        for (ConduitClientData clientData : nodeDataList) {
            clientConduitExtraWorldData.put(clientData.conduit(), clientData.clientDataTag());
        }

        updateShape();
        ensureModelsAreCorrect();
    }

    private record ConduitClientData(Holder<Conduit<?, ?>> conduit, CompoundTag clientDataTag) {
        public static Codec<ConduitClientData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Conduit.CODEC.fieldOf("Conduit").forGetter(ConduitClientData::conduit),
            CompoundTag.CODEC.fieldOf("Data").forGetter(ConduitClientData::clientDataTag)
        ).apply(inst, ConduitClientData::new));
    }

    @Override
    public void onDataPacket(Connection net, ValueInput valueInput) {
        super.onDataPacket(net, valueInput);
        handleUpdateTag(valueInput);
    }

    // endregion

    // region Serialization

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        // TODO: Do this in clear removed instead?
        if (!level.isClientSide()) {
            isLoading = true;
            loadFromSavedData();
            isLoading = false;
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
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private void loadConduitFromSavedData(ConduitNetworkSavedData savedData, Holder<Conduit<?, ?>> conduit,
            int typeIndex) {
        if (level == null || level.isClientSide()) {
            return;
        }

        ConduitNodeImpl node = savedData.claimNode(conduit, this.worldPosition);
        if (node == null && conduitNodes.get(conduit) == null) {
            // Attempt to recover node data
            NodeData nodeData = null;
            if (lazyNodeData != null && lazyNodeData.containsKey(conduit)) {
                nodeData = lazyNodeData.remove(conduit);
            }

            node = new ConduitNodeImpl(conduit, getBlockPos(), nodeData);

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
            ResourceKey<Level> dimension = level.dimension();
            getChunkFacadesForDimension(dimension).remove(SectionPos.asLong(worldPosition));
            getFacadesForDimension(dimension).remove(worldPosition.asLong());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        // Remove all conduits and the facade if this block is being destroyed (not unloaded).
        if (!isChunkUnload) {
            var allConduits = List.copyOf(getConduits());
            for (var conduit : allConduits) {
                removeConduit(conduit, this::dropItem);
            }

            setFacadeProvider(ItemStack.EMPTY);
        }

        if (level != null && level.isClientSide()) {
            ResourceKey<Level> dimension = level.dimension();
            getChunkFacadesForDimension(dimension).get(SectionPos.asLong(worldPosition))
                .remove(worldPosition.asLong()); // TODO: Cleanup mapping if list becomes empty?
            getFacadesForDimension(dimension).remove(worldPosition.asLong());
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        var nodeList = output.list(NODE_DATA_KEY, ConduitAndNodeData.CODEC);
        for (Holder<Conduit<?, ?>> conduit : conduits) {
            if (!conduitNodes.containsKey(conduit)) {
                continue;
            }

            var data = conduitNodes.get(conduit).getNodeData();

            if (data != null && data.type().isPersistent()) {
                nodeList.add(new ConduitAndNodeData(conduit, data));
            }
        }
    }

    @Override
    protected void saveAdditionalSynced(ValueOutput output) {
        super.saveAdditionalSynced(output);

        if (!conduits.isEmpty()) {
            var conduitList = output.list(CONDUITS_KEY, Conduit.CODEC);
            for (var conduit : conduits) {
                conduitList.add(conduit);
            }

            // Save connections
            var conduitConnectionsList = output.childrenList(CONNECTIONS_KEY);
            for (var conduit : conduits) {
                var conduitConnectionsListEntry = conduitConnectionsList.addChild();

                conduitConnectionsListEntry.store("Conduit", Conduit.CODEC, conduit);
                var connectionsList = conduitConnectionsListEntry.childrenList("Connections");

                for (Direction side : Direction.values()) {
                    var connection = connectionsList.addChild();
                    connection.store("Side", Direction.CODEC, side);
                    connection.store("Status", ConnectionStatus.CODEC, getConnectionStatus(conduit, side));

                    // Raw access to ensure we save the true data.
                    var config = conduitConnections.get(conduit).configs.get(side);
                    if (config != null && !config.equals(config.type().getDefault())) {
                        connection.store("Config", ConnectionConfig.GENERIC_CODEC, config);
                    }

                    var inventory = conduitConnections.get(conduit).inventories.get(side);
                    if (inventory != null) {
                        var inventoryContents = connection.list("Inventory", ItemStackWithSlot.CODEC);

                        for (int i = 0; i < inventory.getSlots(); i++) {
                            ItemStack stack = inventory.getStackInSlot(i);
                            if (!stack.isEmpty()) {
                                inventoryContents.add(new ItemStackWithSlot(i, stack));
                            }
                        }
                    }
                }
            }
        }

        if (!facadeProvider.isEmpty()) {
            output.store(FACADE_PROVIDER_KEY, ItemStack.CODEC, facadeProvider);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        // Load conduits from input and sort them correctly.
        conduits = new ArrayList<>();
        conduits.addAll(input.listOrEmpty(CONDUITS_KEY, Conduit.CODEC)
            .stream()
            .sorted(Comparator.comparingInt(ConduitSorter::getSortIndex))
            .toList());

        // Load connections
        conduitConnections.clear();

        var conduitConnectionsList = input.childrenListOrEmpty(CONNECTIONS_KEY);
        for (ValueInput conduitConnectionsListEntry : conduitConnectionsList) {
            // TODO: We should probably avoid throwing exceptions here.
            //       Consider using a codec for the connections record?
            Holder<Conduit<?, ?>> conduit = conduitConnectionsListEntry.read("Conduit", Conduit.CODEC).orElseThrow();

            ConnectionContainer connections = new ConnectionContainer(conduit);
            conduitConnections.put(conduit, connections);

            var connectionsList = conduitConnectionsListEntry.childrenListOrEmpty("Connections");
            for (ValueInput connection : connectionsList) {
                Direction side = connection.read("Side", Direction.CODEC).orElseThrow();
                ConnectionStatus status = connection.read("Status", ConnectionStatus.CODEC).orElseThrow();

                connections.setStatus(side, status);

                connection.read("Config", ConnectionConfig.GENERIC_CODEC)
                    .ifPresent(config -> connections.setConfig(side, config));

                var inventoryContents = connection.listOrEmpty("Inventory", ItemStackWithSlot.CODEC);

                var inventory = connections.getInventory(side);

                for (ItemStackWithSlot itemWithSlot : inventoryContents) {
                    if (itemWithSlot.isValidInContainer(inventory.getSlots())) {
                        inventory.setStackInSlot(itemWithSlot.slot(), itemWithSlot.stack());
                    }
                }
            }
        }

        facadeProvider = input.read(FACADE_PROVIDER_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY);

        // Load node data used for recovery
        var nodeDataList = input.listOrEmpty(NODE_DATA_KEY, ConduitAndNodeData.CODEC);
        lazyNodeData = new HashMap<>();

        for (ConduitAndNodeData nodeData : nodeDataList) {
            lazyNodeData.put(nodeData.conduit(), nodeData.data());
        }

        ensureModelsAreCorrect();
    }

    private record ConduitAndNodeData(Holder<Conduit<?, ?>> conduit, NodeData data) {
        public static final Codec<ConduitAndNodeData> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                Conduit.CODEC.fieldOf("Conduit").forGetter(ConduitAndNodeData::conduit),
                NodeData.GENERIC_CODEC.fieldOf("Data").forGetter(ConduitAndNodeData::data)
            )
            .apply(instance, ConduitAndNodeData::new));
    }

    // This method ensures that any missing client-side connections render properly after
    // the possibility of a block break cancellation rolls back a removed conduit.
    private void ensureModelsAreCorrect() {
        if (level == null || !level.isClientSide()) {
            return;
        }

        // Ensure neighbors remain connected if they should be.
        // This is to handle canceled conduit removals from the server
        for (Direction side : Direction.values()) {
            if (!(level.getBlockEntity(getBlockPos().relative(side)) instanceof ConduitBundleBlockEntity neighbourConduitBundle)) {
                continue;
            }

            for (var conduit : conduits) {
                var currentStatus = getConnectionStatus(conduit, side);
                var neighborStatus = neighbourConduitBundle.getConnectionStatus(conduit, side.getOpposite());

                if (currentStatus == ConnectionStatus.CONNECTED_CONDUIT &&
                    neighborStatus == ConnectionStatus.DISCONNECTED) {
                    neighbourConduitBundle.connectConduit(conduit, side.getOpposite());
                }
            }
        }

        // Ensure model is up to date
        updateModel();
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

            var defaultConfig = conduit.value().type().connectionConfigType().getDefault();
            for (Direction dir : Direction.values()) {
                statuses.put(dir, ConnectionStatus.DISCONNECTED);
                configs.put(dir, defaultConfig);
            }
        }

        public ConnectionContainer copyFor(Holder<Conduit<?, ?>> conduit) {
            var copy = new ConnectionContainer(conduit);
            copy.statuses.putAll(statuses);

            // Only copy connection config if compatible.
            if (this.conduit.value().type().connectionConfigType() == conduit.value().type().connectionConfigType()) {
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

            if (conduit.value().type() == EIOConduitTypes.REDSTONE.get()) {
                redstoneConduitChanged(side);
            }
        }

        public ConnectionConfig getConfig(Direction side) {
            var defaultConfig = conduit.value().type().connectionConfigType().getDefault();
            var config = configs.getOrDefault(side, defaultConfig);

            // Ensure the connection type is correct.
            // If it isn't, revert to the default.
            if (config.type() != conduit.value().type().connectionConfigType()) {
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

            if (conduit.value().type() == EIOConduitTypes.REDSTONE.get()) {
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
}
