package com.enderio.conduits.common.conduit.block;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.conduit.SlotType;
import com.enderio.api.conduit.connection.ConnectionState;
import com.enderio.api.conduit.connection.DynamicConnectionState;
import com.enderio.api.conduit.connection.StaticConnectionStates;
import com.enderio.conduits.ConduitNBTKeys;
import com.enderio.conduits.common.ConduitShape;
import com.enderio.conduits.common.conduit.RightClickAction;
import com.enderio.conduits.common.conduit.SlotData;
import com.enderio.conduits.common.conduit.ConduitBundle;
import com.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.conduits.common.menu.ConduitMenu;
import com.enderio.conduits.common.network.ConduitSavedData;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConduitBlockEntity extends EnderBlockEntity {

    public static final ModelProperty<ConduitBundle> BUNDLE_MODEL_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<BlockPos> POS = new ModelProperty<>();
    public static final String CONDUIT_INV_KEY = "ConduitInv";

    private final ConduitShape shape = new ConduitShape();

    private ConduitBundle bundle;
    @UseOnly(LogicalSide.CLIENT) private ConduitBundle clientBundle;

    private UpdateState checkConnection = UpdateState.NONE;

    private final Map<ConduitType<?>,NodeIdentifier<?>> lazyNodes = new HashMap<>();
    private ListTag lazyNodeNBT = new ListTag();
    private ConduitItemHandler conduitItemHandler = new ConduitItemHandler();

    public ConduitBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ConduitBlockEntities.CONDUIT.get(), worldPosition, blockState);
        bundle = new ConduitBundle(this::scheduleTick, worldPosition);
        clientBundle = bundle.deepCopy();

        addDataSlot(ConduitBundle.DATA_SLOT_TYPE.create(this::getBundle, b -> bundle = b));
        addAfterSyncRunnable(this::updateClient);
    }

    public void updateClient() {
        clientBundle = bundle.deepCopy();
        updateShape();
        requestModelDataUpdate();
        level.setBlocksDirty(getBlockPos(), Blocks.AIR.defaultBlockState(), getBlockState());
    }

    // region Network Sync

    /**
     * Handle a connection state update from the client.
     */
    @UseOnly(LogicalSide.SERVER)
    public void handleConnectionStateUpdate(Direction direction, ConduitType<?> conduitType, DynamicConnectionState connectionState) {
        // Sanity check, the client shouldn't do this, but just to make sure there's no confusion.
        if (bundle.getConnectionState(direction, conduitType) instanceof DynamicConnectionState) {
            bundle.setConnectionState(direction, conduitType, connectionState);

            pushIOState(direction, bundle.getNodeFor(conduitType), connectionState);
            level.invalidateCapabilities(worldPosition);
        }

        updateClient();
        updateConnectionToData(conduitType);
    }

    @UseOnly(LogicalSide.SERVER)
    public <T extends ExtendedConduitData<T>> void handleExtendedDataUpdate(ConduitType<T> conduitType, T data) {
        var node = getBundle().getNodeFor(conduitType);
        node.getExtendedConduitData().applyGuiChanges(data);
    }

    // endregion

    private void scheduleTick() {
        setChanged();
    }

    @Override
    public void onLoad() {
        updateShape();
        if (level instanceof ServerLevel serverLevel) {
            sync();
            bundle.onLoad(level, getBlockPos());
            for (var entry: lazyNodes.entrySet()) {
                NodeIdentifier<?> node = entry.getValue();
                for (Direction dir : Direction.values()) {
                    tryConnectTo(dir, entry.getKey(), false, false).ifPresent(otherNode -> Graph.connect(node, otherNode));
                }
                for (GraphObject<Mergeable.Dummy> object : node.getGraph().getObjects()) {
                    if (object instanceof NodeIdentifier<?> otherNode) {
                        node.getExtendedConduitData().onConnectTo(otherNode.getExtendedConduitData().cast());
                    }
                }
                ConduitSavedData.addPotentialGraph(entry.getKey(), Objects.requireNonNull(node.getGraph()), serverLevel);
            }
        }
    }

    public boolean stillValid(Player pPlayer) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }

        return pPlayer.canInteractWithBlock(this.worldPosition, 1.5);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (level instanceof ServerLevel serverLevel) {
            ConduitSavedData savedData = ConduitSavedData.get(serverLevel);
            for (ConduitType<?> type : bundle.getTypes()) {
                NodeIdentifier<?> node = bundle.getNodeFor(type);
                node.getExtendedConduitData().onRemoved(type.cast(), level, getBlockPos());
                savedData.putUnloadedNodeIdentifier(type, this.worldPosition, node);
            }
        }
    }

    public void everyTick() {
        if (level != null && !level.isClientSide) {
            serverTick();
            checkConnection = checkConnection.next();
            if (checkConnection.isInitialized()) {
                updateConnections(level, worldPosition, null, false);
            }
        }
    }

    public void updateConnections(Level level, BlockPos pos, @Nullable BlockPos fromPos, boolean shouldActivate) {
        for (Direction direction: Direction.values()) {
            if (fromPos == null || !(level.getBlockEntity(fromPos) instanceof ConduitBlockEntity)) {
                for (ConduitType<?> type : bundle.getTypes()) {
                    if (shouldActivate && type.getTicker().hasConnectionDelay()) {
                        checkConnection = checkConnection.activate();
                    }

                    ConnectionState connectionState = bundle.getConnectionState(direction, type);
                    if (connectionState instanceof DynamicConnectionState dyn) {
                        if (!type.getTicker().canConnectTo(level, pos, direction)) {
                            bundle.getNodeFor(type).clearState(direction);
                            dropConnection(dyn);
                            bundle.setConnectionState(direction, type, StaticConnectionStates.DISCONNECTED);
                            updateShape();
                            updateConnectionToData(type);
                        }
                    } else if (connectionState == StaticConnectionStates.DISCONNECTED) {
                        tryConnectTo(direction, type, true, true);
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(tag, lookupProvider);
        tag.put(ConduitNBTKeys.CONDUIT_BUNDLE, bundle.save(lookupProvider));

        ListTag listTag = new ListTag();
        for (ConduitType<?> type : bundle.getTypes()) {
            var data = bundle.getNodeFor(type).getExtendedConduitData();
            listTag.add(data.save(lookupProvider));
        }

        tag.put(ConduitNBTKeys.CONDUIT_EXTRA_DATA, listTag);
        tag.put(CONDUIT_INV_KEY, conduitItemHandler.serializeNBT(lookupProvider));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);

        bundle = ConduitBundle.parse(lookupProvider, tag.getCompound(ConduitNBTKeys.CONDUIT_BUNDLE));
        bundle.setOnChangedRunnable(this::scheduleTick);

        lazyNodeNBT = tag.getList(ConduitNBTKeys.CONDUIT_EXTRA_DATA, Tag.TAG_COMPOUND);
        conduitItemHandler.deserializeNBT(lookupProvider, tag.getCompound(CONDUIT_INV_KEY));
    }

    @Override
    public void setLevel(Level pLevel) {
        super.setLevel(pLevel);
        if (!level.isClientSide()) {
            //pull that data earlier, so extended conduit data is present for ae2 connections
            loadFromSavedData();
        }
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(BUNDLE_MODEL_PROPERTY, clientBundle).with(POS, worldPosition).build();
    }

    public boolean hasType(ConduitType<?> type) {
        return bundle.hasType(type);
    }

    public RightClickAction addType(ConduitType<?> type, Player player) {
        RightClickAction action = bundle.addType(level, type, player);
        //something has changed
        if (action.hasChanged()) {
            List<GraphObject<Mergeable.Dummy>> nodes = new ArrayList<>();
            for (Direction dir : Direction.values()) {
                tryConnectTo(dir, type, false, false).ifPresent(nodes::add);
            }
            if (level instanceof ServerLevel serverLevel) {
                NodeIdentifier<?> thisNode = Objects.requireNonNull(bundle.getNodeForTypeExact(type), "no node found in conduit");
                Graph.integrate(thisNode, nodes);
                for (GraphObject<Mergeable.Dummy> object : thisNode.getGraph().getObjects()) {
                    if (object instanceof NodeIdentifier<?> node) {
                        thisNode.getExtendedConduitData().onConnectTo(node.getExtendedConduitData().cast());
                    }
                }
                ConduitSavedData.addPotentialGraph(type, Objects.requireNonNull(thisNode.getGraph()), serverLevel);
            }
            if (action instanceof RightClickAction.Upgrade upgrade && !upgrade.getNotInConduit().getTicker().canConnectTo(upgrade.getNotInConduit(), type)) {
                removeNeighborConnections(upgrade.notInConduit());
            }
            updateShape();
        }
        return action;
    }

    public Optional<GraphObject<Mergeable.Dummy>> tryConnectTo(Direction dir, ConduitType<?> type, boolean forceMerge, boolean shouldMergeGraph) {
        if (level.getBlockEntity(getBlockPos().relative(dir)) instanceof ConduitBlockEntity conduit
            && conduit.connectTo(dir.getOpposite(), type.cast(), bundle.getNodeFor(type).getExtendedConduitData(), forceMerge)) {
            connect(dir, type);
            updateConnectionToData(type);
            conduit.updateConnectionToData(type);
            NodeIdentifier<?> firstNode = conduit.getBundle().getNodeFor(type);
            NodeIdentifier<?> secondNode = bundle.getNodeFor(type);
            firstNode.getExtendedConduitData().onConnectTo(secondNode.getExtendedConduitData().cast());
            if (firstNode.getGraph() != null) {
                for (GraphObject<Mergeable.Dummy> object : firstNode.getGraph().getObjects()) {
                    if (object instanceof NodeIdentifier<?> node && node != firstNode) {
                        firstNode.getExtendedConduitData().onConnectTo(node.getExtendedConduitData().cast());
                    }
                }
            }
            if (secondNode.getGraph() != null && firstNode.getGraph() != secondNode.getGraph()) {
                for (GraphObject<Mergeable.Dummy> object : secondNode.getGraph().getObjects()) {
                    if (object instanceof NodeIdentifier<?> node && node != secondNode) {
                        secondNode.getExtendedConduitData().onConnectTo(node.getExtendedConduitData().cast());
                    }
                }
            }
            if (shouldMergeGraph) {
                Graph.connect(bundle.getNodeFor(type), conduit.bundle.getNodeFor(type));
            }
            return Optional.of(conduit.bundle.getNodeFor(type));
        } else if (type.getTicker().canConnectTo(level, getBlockPos(), dir)) {
            connectEnd(dir, type);
            updateConnectionToData(type);
        }
        return Optional.empty();
    }

    public void updateConnectionToData(ConduitType<?> type) {
        if (!level.isClientSide) {
            getBundle()
                .getNodeFor(type)
                .getExtendedConduitData()
                .updateConnection(Arrays
                    .stream(Direction.values())
                    .filter(streamDir -> bundle.getConnectionState(streamDir, type) != StaticConnectionStates.DISABLED)
                    .collect(Collectors.toSet()));
        }
    }

    /**
     * sets block to air if this is the last conduit
     */

    public void removeTypeAndDelete(ConduitType<?> type) {
        if (removeType(type, false)) {
            level.setBlock(getBlockPos(), getBlockState().getFluidState().createLegacyBlock(),
                level.isClientSide ? Block.UPDATE_ALL_IMMEDIATE : Block.UPDATE_ALL);
        }
    }

    public boolean removeType(ConduitType<?> type, boolean shouldDrop) {
        if (shouldDrop && !level.isClientSide()) {
            dropItem(type.getConduitItem().getDefaultInstance());
            for (Direction dir : Direction.values()) {
                if (bundle.getConnectionState(dir, type) instanceof DynamicConnectionState dyn) {
                    dropConnection(dyn);
                }
            }
        }
        boolean shouldRemove = bundle.removeType(level, type);
        removeNeighborConnections(type);
        updateShape();
        return shouldRemove;
    }

    public void updateEmptyDynConnection() {
        for (Direction dir : Direction.values()) {
            for (int i = 0; i < ConduitBundle.MAX_CONDUIT_TYPES; i++) {
                if (bundle.getConnectionState(dir, i) instanceof DynamicConnectionState dynState && dynState.isEmpty()) {
                    dropConnection(dynState);
                    bundle.disableType(dir, i);
                }
            }
        }
    }

    public void dropConnection(DynamicConnectionState dyn) {
        for (SlotType slotType : SlotType.values()) {
            ItemStack item = dyn.getItem(slotType);
            if (!item.isEmpty()) {
                dropItem(item);
            }
        }
    }

    private void dropItem(ItemStack stack) {
        level.addFreshEntity(new ItemEntity(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), stack));
    }

    public void removeNeighborConnections(ConduitType<?> type) {
        for (Direction dir : Direction.values()) {
            if (level.getBlockEntity(getBlockPos().relative(dir)) instanceof ConduitBlockEntity conduit) {
                conduit.disconnect(dir.getOpposite(), type);
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            for (Direction dir : Direction.values()) {
                BlockEntity blockEntity = level.getBlockEntity(getBlockPos().relative(dir));
                if (blockEntity instanceof ConduitBlockEntity conduit && conduit.hasType(type)) {
                    Optional
                        .of(conduit.bundle.getNodeFor(type))
                        .map(NodeIdentifier::getGraph)
                        .filter(Objects::nonNull)
                        .ifPresent(graph -> ConduitSavedData.addPotentialGraph(type, graph, serverLevel));
                }
            }
        }
    }

    public void updateShape() {
        shape.updateConduit(bundle);
    }

    private void loadFromSavedData() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ConduitSavedData savedData = ConduitSavedData.get(serverLevel);
        for (int typeIndex = 0; typeIndex < bundle.getTypes().size(); typeIndex++) {
            ConduitType<?> type = bundle.getTypes().get(typeIndex);
            NodeIdentifier<?> node = savedData.takeUnloadedNodeIdentifier(type, this.worldPosition);
            if (node == null && bundle.getNodeForTypeExact(type) == null) {
                ExtendedConduitData<?> data;
                if (typeIndex < lazyNodeNBT.size()) {
                    data = ExtendedConduitData.parse(level.registryAccess(), lazyNodeNBT.getCompound(typeIndex));
                } else {
                    data = type.createExtendedConduitData(level, worldPosition);
                }

                node = new NodeIdentifier<>(worldPosition, data);
                Graph.integrate(node, List.of());
                bundle.setNodeFor(type, node);
                lazyNodes.put(type, node);
            } else if (node != null){
                bundle.setNodeFor(type, node);
            }
        }

        lazyNodeNBT.clear();
    }

    /**
     * @param direction  the Direction to connect to
     * @param type       the type to be connected
     * @param data       the other conduitdata to check if those can connect
     * @param forceMerge if disabledstate should be ignored
     * @return true if a connection happens
     */
    private <T extends ExtendedConduitData<T>> boolean connectTo(Direction direction, ConduitType<T> type, ExtendedConduitData<T> data, boolean forceMerge) {
        if (!doTypesMatch(type)) {
            return false;
        }

        if (!data.canConnectTo(bundle.getNodeFor(type).getExtendedConduitData())) {
            return false;
        }

        if (forceMerge || bundle.getConnectionState(direction, type) != StaticConnectionStates.DISABLED) {
            connect(direction, type);
            return true;
        }

        return false;
    }

    private boolean doTypesMatch(ConduitType<?> type) {
        for (ConduitType<?> bundleType : bundle.getTypes()) {
            if (bundleType.getTicker().canConnectTo(bundleType, type)) {
                return true;
            }
        }

        return false;
    }

    private void connect(Direction direction, ConduitType<?> type) {
        bundle.connectTo(level, worldPosition, direction, type, false);
        updateClient();
    }

    private void connectEnd(Direction direction, ConduitType<?> type) {
        bundle.connectTo(level, worldPosition, direction, type, true);
        updateClient();
    }

    private void disconnect(Direction direction, ConduitType<?> type) {
        if (bundle.disconnectFrom(direction, type)) {
            updateClient();
        }
    }

    public ConduitBundle getBundle() {
        return bundle;
    }

    public ConduitShape getShape() {
        return shape;
    }

    public MenuProvider menuProvider(Direction direction, ConduitType<?> type) {
        return new ConduitMenuProvider(direction, type);
    }

    private class ConduitMenuProvider implements MenuProvider {

        private final Direction direction;
        private final ConduitType<?> type;

        private ConduitMenuProvider(Direction direction, ConduitType<?> type) {
            this.direction = direction;
            this.type = type;
        }

        @Override
        public Component getDisplayName() {
            return getBlockState().getBlock().getName();
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
            return new ConduitMenu(ConduitBlockEntity.this, pInventory, pContainerId, direction, type);
        }
    }

    //TODO: NEO-PORT: auto add caps?
    public static <T> ICapabilityProvider<ConduitBlockEntity, Direction, T> createConduitCap(BlockCapability<T, Direction> cap) {
         return (be, side) -> {
            for (ConduitType<?> type : be.bundle.getTypes()) {
                NodeIdentifier<?> node = be.bundle.getNodeFor(type);
                NodeIdentifier.IOState state = node.getIOState(side).orElse(null);

                var proxiedCap = type.proxyCapability(cap, node.getExtendedConduitData().cast(), be.level, be.getBlockPos(), side, state);

                if (proxiedCap.isPresent()) {
                    return proxiedCap.get();
                }
            }
            return null;
        };
    }


    public IItemHandler getConduitItemHandler() {
        return conduitItemHandler;
    }

    public static void pushIOState(Direction direction, NodeIdentifier<?> node, DynamicConnectionState connectionState) {
        node.pushState(direction, connectionState);
    }

    private class ConduitItemHandler implements IItemHandlerModifiable, INBTSerializable<CompoundTag> {

        @Override
        public int getSlots() {
            return 3 * ConduitBundle.MAX_CONDUIT_TYPES * 6;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot >= getSlots()) {
                return ItemStack.EMPTY;
            }

            SlotData data = SlotData.of(slot);
            if (data.conduitIndex() >= bundle.getTypes().size()) {
                return ItemStack.EMPTY;
            }

            ConnectionState connectionState = bundle.getConnectionState(data.direction(), data.conduitIndex());
            if (!(connectionState instanceof DynamicConnectionState dynamicConnectionState)) {
                return ItemStack.EMPTY;
            }

            ConduitMenuData conduitData = bundle.getTypes().get(data.conduitIndex()).getMenuData();
            if ((data.slotType() == SlotType.FILTER_EXTRACT && conduitData.hasFilterExtract()) || (data.slotType() == SlotType.FILTER_INSERT
                && conduitData.hasFilterInsert()) || (data.slotType() == SlotType.UPGRADE_EXTRACT && conduitData.hasUpgrade())) {
                return dynamicConnectionState.getItem(data.slotType());
            }

            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            //see ItemStackHandler
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }

            if (!isItemValid(slot, stack)) {
                return stack;
            }

            ItemStack existing = getStackInSlot(slot);

            int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

            if (!existing.isEmpty()) {
                if (!ItemStack.isSameItemSameComponents(stack, existing)) {
                    return stack;
                }

                limit -= existing.getCount();
            }

            if (limit <= 0) {
                return stack;
            }

            boolean reachedLimit = stack.getCount() > limit;

            if (!simulate) {
                if (existing.isEmpty()) {
                    setStackInSlot(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
                } else {
                    existing.grow(reachedLimit ? limit : stack.getCount());
                }
            }
            return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0) {
                return ItemStack.EMPTY;
            }

            ItemStack existing = getStackInSlot(slot);

            if (existing.isEmpty()) {
                return ItemStack.EMPTY;
            }

            int toExtract = Math.min(amount, existing.getMaxStackSize());

            if (existing.getCount() <= toExtract) {
                if (!simulate) {
                    setStackInSlot(slot, ItemStack.EMPTY);
                    return existing;
                } else {
                    return existing.copy();
                }
            } else {
                if (!simulate) {
                    setStackInSlot(slot, existing.copyWithCount(existing.getCount() - toExtract));
                }
                return existing.copyWithCount(toExtract);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot % 3 == 2 ? 64 : 1;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            //TODO implement
            return slot < getSlots();
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            if (slot >= getSlots()) {
                return;
            }

            SlotData data = SlotData.of(slot);
            if (data.conduitIndex() >= bundle.getTypes().size()) {
                return;
            }

            ConduitType<?> iConduitType = bundle.getTypes().get(data.conduitIndex());
            ConduitMenuData conduitData = iConduitType.getMenuData();

            if ((data.slotType() == SlotType.FILTER_EXTRACT && conduitData.hasFilterExtract()) || (data.slotType() == SlotType.FILTER_INSERT
                && conduitData.hasFilterInsert()) || (data.slotType() == SlotType.UPGRADE_EXTRACT && conduitData.hasUpgrade())) {
                bundle.setConnectionItem(data.direction(), data.conduitIndex(), data.slotType(), stack);
                if (bundle.getConnectionState(data.direction(), iConduitType) instanceof DynamicConnectionState dynamicConnectionState) {
                    NodeIdentifier<?> node = bundle.getNodeForTypeExact(iConduitType);
                    if (node != null) {
                        pushIOState(data.direction(), node, dynamicConnectionState);
                    }
                }
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            for (int i = 0; i < getSlots(); i++) {
                ItemStack stack = getStackInSlot(i);
                list.add(i, stack.saveOptional(lookupProvider));
            }
            tag.put(CONDUIT_INV_KEY, list);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
            ListTag list = nbt.getList(CONDUIT_INV_KEY, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                setStackInSlot(i, ItemStack.parseOptional(lookupProvider, list.getCompound(i)));
            }
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
