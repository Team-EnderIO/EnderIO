package com.enderio.conduits.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.*;
import com.enderio.conduits.ConduitNBTKeys;
import com.enderio.conduits.common.ConduitShape;
import com.enderio.conduits.common.blockentity.connection.DynamicConnectionState;
import com.enderio.conduits.common.blockentity.connection.IConnectionState;
import com.enderio.conduits.common.blockentity.connection.StaticConnectionStates;
import com.enderio.conduits.common.menu.ConduitMenu;
import com.enderio.conduits.common.network.ConduitSavedData;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ConduitBlockEntity extends EnderBlockEntity {

    public static final ModelProperty<ConduitBundle> BUNDLE_MODEL_PROPERTY = new ModelProperty<>();
    public static final ModelProperty<BlockPos> POS = new ModelProperty<>();

    private final ConduitShape shape = new ConduitShape();

    private final ConduitBundle bundle;
    @UseOnly(LogicalSide.CLIENT) private ConduitBundle clientBundle;

    private UpdateState checkConnection = UpdateState.NONE;

    private final Map<IConduitType<?>,NodeIdentifier<?>> lazyNodes = new HashMap<>();
    private ListTag lazyNodeNBT = new ListTag();

    public ConduitBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
        bundle = new ConduitBundle(this::scheduleTick, worldPosition);
        clientBundle = bundle.deepCopy();

        addDataSlot(new ConduitBundleNetworkDataSlot(this::getBundle));
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
    public void handleConnectionStateUpdate(Direction direction, IConduitType<?> conduitType, DynamicConnectionState connectionState) {
        var connection = bundle.getConnection(direction);

        // Sanity check, the client shouldn't do this, but just to make sure there's no confusion.
        if (connection.getConnectionState(conduitType) instanceof DynamicConnectionState) {
            connection.setConnectionState(conduitType, connectionState);

            pushIOState(direction, bundle.getNodeFor(conduitType), connectionState);
        }
        updateClient();
        updateConnectionToData(conduitType);
    }

    @UseOnly(LogicalSide.SERVER)
    public void handleExtendedDataUpdate(IConduitType<?> conduitType, CompoundTag compoundTag) {
        getBundle().getNodeFor(conduitType).getExtendedConduitData().deserializeNBT(compoundTag);
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
            for (var entry: lazyNodes.entrySet()) {
                NodeIdentifier<?> node = entry.getValue();
                IExtendedConduitData<?> data = node.getExtendedConduitData();
                data.onCreated(entry.getKey(), level, worldPosition, null);
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
            bundle.onLoad(level, getBlockPos());
        }
    }

    public boolean stillValid(Player pPlayer) {
        if (this.level.getBlockEntity(this.worldPosition) != this)
            return false;
        return pPlayer.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= Mth.square(
            pPlayer.getAttributeValue(ForgeMod.BLOCK_REACH.get()));
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (level instanceof ServerLevel serverLevel) {
            ConduitSavedData savedData = ConduitSavedData.get(serverLevel);
            for (IConduitType<?> type : bundle.getTypes()) {
                NodeIdentifier<?> node = bundle.getNodeFor(type);
                node.getExtendedConduitData().onRemoved(type, level, getBlockPos());
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
                for (IConduitType<?> type : bundle.getTypes()) {
                    if (shouldActivate && type.getTicker().hasConnectionDelay()) {
                        checkConnection = checkConnection.activate();
                    }
                    IConnectionState connectionState = bundle.getConnection(direction).getConnectionState(type);
                    if (connectionState instanceof DynamicConnectionState dyn) {
                        if (!type.getTicker().canConnectTo(level, pos, direction)) {
                            getBundle().getNodeFor(type).clearState(direction);
                            dropConnection(dyn);
                            getBundle().getConnection(direction).setConnectionState(type, StaticConnectionStates.DISCONNECTED);
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
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(ConduitNBTKeys.CONDUIT_BUNDLE, bundle.serializeNBT());
        ListTag listTag = new ListTag();
        for (IConduitType<?> type : bundle.getTypes()) {
            IExtendedConduitData<?> data = bundle.getNodeFor(type).getExtendedConduitData();
            listTag.add(data.serializeNBT());
        }
        tag.put(ConduitNBTKeys.CONDUIT_EXTRA_DATA, listTag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        bundle.deserializeNBT(tag.getCompound(ConduitNBTKeys.CONDUIT_BUNDLE));
        lazyNodeNBT = tag.getList(ConduitNBTKeys.CONDUIT_EXTRA_DATA, Tag.TAG_COMPOUND);
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

    public boolean hasType(IConduitType<?> type) {
        return bundle.hasType(type);
    }

    public RightClickAction addType(IConduitType<?> type, Player player) {
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

    public Optional<GraphObject<Mergeable.Dummy>> tryConnectTo(Direction dir, IConduitType<?> type, boolean forceMerge, boolean shouldMergeGraph) {
        if (level.getBlockEntity(getBlockPos().relative(dir)) instanceof ConduitBlockEntity conduit
            && conduit.connectTo(dir.getOpposite(), type, bundle.getNodeFor(type).getExtendedConduitData(), forceMerge)) {
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

    public void updateConnectionToData(IConduitType<?> type) {
        if (!level.isClientSide)
            getBundle()
                .getNodeFor(type)
                .getExtendedConduitData()
                .updateConnection(Arrays
                    .stream(Direction.values())
                    .filter(streamDir -> getBundle().getConnection(streamDir).getConnectionState(type) != StaticConnectionStates.DISABLED)
                    .collect(Collectors.toSet()));
    }

    /**
     * sets block to air if this is the last conduit
     */

    public void removeTypeAndDelete(IConduitType<?> type) {
        if (removeType(type, false)) {
            level.setBlock(getBlockPos(), getBlockState().getFluidState().createLegacyBlock(),
                level.isClientSide ? Block.UPDATE_ALL_IMMEDIATE : Block.UPDATE_ALL);
        }
    }

    public boolean removeType(IConduitType<?> type, boolean shouldDrop) {
        if (shouldDrop && !level.isClientSide()) {
            dropItem(type.getConduitItem().getDefaultInstance());
            for (Direction dir : Direction.values()) {
                if (getBundle().getConnection(dir).getConnectionState(type) instanceof DynamicConnectionState dyn) {
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
            ConduitConnection connection = bundle.getConnection(dir);
            for (int i = 0; i < ConduitBundle.MAX_CONDUIT_TYPES; i++) {
                if (connection.getConnectionState(i) instanceof DynamicConnectionState dynState && dynState.isEmpty()) {
                    dropConnection(dynState);
                    connection.disableType(i);
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

    public void removeNeighborConnections(IConduitType<?> type) {
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
        if (!(level instanceof ServerLevel serverLevel))
            return;
        ConduitSavedData savedData = ConduitSavedData.get(serverLevel);
        for (int typeIndex = 0; typeIndex < bundle.getTypes().size(); typeIndex++) {
            IConduitType<?> type = bundle.getTypes().get(typeIndex);
            NodeIdentifier<?> node = savedData.takeUnloadedNodeIdentifier(type, this.worldPosition);
            if (node == null && bundle.getNodeForTypeExact(type) == null) {
                IExtendedConduitData<?> data = type.createExtendedConduitData(level, worldPosition);
                if (typeIndex < lazyNodeNBT.size()) {
                    data.deserializeNBT(lazyNodeNBT.getCompound(typeIndex));
                }
                node = new NodeIdentifier<>(worldPosition, data);
                for (Direction direction : Direction.values()) {
                    if (bundle.getConnection(direction).getConnectionState(type) instanceof DynamicConnectionState connectionState) {
                        pushIOState(direction, node, connectionState);
                    }
                }
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
    private boolean connectTo(Direction direction, IConduitType<?> type, IExtendedConduitData<?> data, boolean forceMerge) {
        if (!doTypesMatch(type))
            return false;
        if (!data.canConnectTo(bundle.getNodeFor(type).getExtendedConduitData().cast()))
            return false;
        if (forceMerge || bundle.getConnection(direction).getConnectionState(type) != StaticConnectionStates.DISABLED) {
            connect(direction, type);
            return true;
        }
        return false;
    }

    private boolean doTypesMatch(IConduitType<?> type) {
        for (IConduitType<?> bundleType : bundle.getTypes()) {
            if (bundleType.getTicker().canConnectTo(bundleType, type))
                return true;
        }
        return false;
    }

    private void connect(Direction direction, IConduitType<?> type) {
        bundle.connectTo(direction, type, false);
        updateClient();
    }

    private void connectEnd(Direction direction, IConduitType<?> type) {
        bundle.connectTo(direction, type, true);
        updateClient();
    }

    private void disconnect(Direction direction, IConduitType<?> type) {
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

    public MenuProvider menuProvider(Direction direction, IConduitType<?> type) {
        return new ConduitMenuProvider(direction, type);
    }

    private class ConduitMenuProvider implements MenuProvider {

        private final Direction direction;
        private final IConduitType<?> type;

        private ConduitMenuProvider(Direction direction, IConduitType<?> type) {
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

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        for (IConduitType<?> type : bundle.getTypes()) {
            NodeIdentifier<?> node = bundle.getNodeFor(type);
            var proxiedCap = type.proxyCapability(cap,
                node == null ? type.createExtendedConduitData(level, getBlockPos()).cast() : node.getExtendedConduitData().cast(), side);
            if (proxiedCap.isPresent())
                return proxiedCap.get();
        }
        return super.getCapability(cap, side);
    }

    public IItemHandler getConduitItemHandler() {
        return new ConduitItemHandler();
    }

    public static void pushIOState(Direction direction, NodeIdentifier<?> node, DynamicConnectionState connectionState) {
        node.pushState(direction, connectionState.isInsert() ? connectionState.insert() : null,
            connectionState.isExtract() ? connectionState.extract() : null, connectionState.control(), connectionState.redstoneChannel());
    }

    private class ConduitItemHandler implements IItemHandlerModifiable {

        @Override
        public int getSlots() {
            return 3 * ConduitBundle.MAX_CONDUIT_TYPES * 6;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot >= getSlots())
                return ItemStack.EMPTY;
            SlotData data = SlotData.of(slot);
            if (data.conduitIndex() >= bundle.getTypes().size())
                return ItemStack.EMPTY;
            IConnectionState connectionState = bundle.getConnection(data.direction()).getConnectionState(data.conduitIndex());
            if (!(connectionState instanceof DynamicConnectionState dynamicConnectionState))
                return ItemStack.EMPTY;
            IConduitMenuData conduitData = bundle.getTypes().get(data.conduitIndex()).getMenuData();
            if ((data.slotType() == SlotType.FILTER_EXTRACT && conduitData.hasFilterExtract()) || (data.slotType() == SlotType.FILTER_INSERT
                && conduitData.hasFilterInsert()) || (data.slotType() == SlotType.UPGRADE_EXTRACT && conduitData.hasUpgrade()))
                return dynamicConnectionState.getItem(data.slotType());
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            //see ItemStackHandler
            if (stack.isEmpty())
                return ItemStack.EMPTY;

            if (!isItemValid(slot, stack))
                return stack;

            ItemStack existing = getStackInSlot(slot);

            int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

            if (!existing.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                    return stack;

                limit -= existing.getCount();
            }

            if (limit <= 0)
                return stack;

            boolean reachedLimit = stack.getCount() > limit;

            if (!simulate) {
                if (existing.isEmpty()) {
                    setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                } else {
                    existing.grow(reachedLimit ? limit : stack.getCount());
                }
            }
            return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0)
                return ItemStack.EMPTY;

            ItemStack existing = getStackInSlot(slot);

            if (existing.isEmpty())
                return ItemStack.EMPTY;

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
                    setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                }
                return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
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
            if (slot >= getSlots())
                return;
            SlotData data = SlotData.of(slot);
            if (data.conduitIndex() >= bundle.getTypes().size())
                return;
            ConduitConnection connection = bundle.getConnection(data.direction());
            IConduitMenuData conduitData = bundle.getTypes().get(data.conduitIndex()).getMenuData();
            if ((data.slotType() == SlotType.FILTER_EXTRACT && conduitData.hasFilterExtract()) || (data.slotType() == SlotType.FILTER_INSERT
                && conduitData.hasFilterInsert()) || (data.slotType() == SlotType.UPGRADE_EXTRACT && conduitData.hasUpgrade())) {
                connection.setItem(data.slotType(), data.conduitIndex(), stack);
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
