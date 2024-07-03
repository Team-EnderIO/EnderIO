package com.enderio.machines.common.blockentity.base;

import com.enderio.api.UseOnly;
import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.base.common.blockentity.IWrenchable;
import com.enderio.base.common.particle.RangeParticleData;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.core.common.network.slot.BooleanNetworkDataSlot;
import com.enderio.core.common.network.slot.EnumNetworkDataSlot;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.core.common.network.slot.NBTSerializableNetworkDataSlot;
import com.enderio.core.common.network.slot.SetNetworkDataSlot;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.block.MachineBlock;
import com.enderio.machines.common.blockentity.MachineState;
import com.enderio.machines.common.io.IOConfig;
import com.enderio.machines.common.io.TransferUtil;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class MachineBlockEntity extends EnderBlockEntity implements MenuProvider, IWrenchable {

    // region IO Configuration

    private final IIOConfig ioConfig;

    public static final ModelProperty<IIOConfig> IO_CONFIG_PROPERTY = new ModelProperty<>();

    private ModelData modelData = ModelData.EMPTY;

    // endregion

    // region range

    protected int range = 3;
    protected IntegerNetworkDataSlot rangeDataSlot;
    protected boolean rangeVisible = false;
    protected BooleanNetworkDataSlot rangeVisibleDataSlot;

    // endregion

    // region Redstone Control

    private RedstoneControl redstoneControl = RedstoneControl.ALWAYS_ACTIVE;

    // endregion

    // region Items and Fluids

    @Nullable
    private final MachineInventory inventory;

    @Nullable
    private final MachineFluidHandler fluidHandler;

    // region Caches for external block interaction

    private final List<Capability<?>> cachedCapabilityTypes = new ArrayList<>();
    private final Map<Capability<?>, EnumMap<Direction, LazyOptional<?>>> cachedCapabilities = new HashMap<>();
    private boolean isCapabilityCacheDirty = false;

    // endregion

    // region Common Dataslots

    private final EnumNetworkDataSlot<RedstoneControl> redstoneControlDataSlot;
    private final NBTSerializableNetworkDataSlot<IIOConfig> ioConfigDataSlot;

    // endregion

    private Set<MachineState> states = new HashSet<>();
    private boolean redstoneBlocked;
    @Nullable
    private final MachineInventoryLayout inventoryLayout;

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);

        // Create IO Config.
        this.ioConfig = createIOConfig();
        addCapabilityProvider(ioConfig);

        // If the machine declares an inventory layout, use it to create a handler
        inventoryLayout = createInventoryLayout();
        if (inventoryLayout != null) {
            inventory = createMachineInventory(inventoryLayout);
            addCapabilityProvider(inventory);
        } else {
            inventory = null;
        }

        // Create fluid storage
        MachineTankLayout tankLayout = getTankLayout();
        if (tankLayout != null) {
            fluidHandler = createFluidHandler(tankLayout);
            if (fluidHandler != null) {
                addCapabilityProvider(fluidHandler);
            }
        } else {
            fluidHandler = null;
        }

        if (supportsRedstoneControl()) {
            redstoneControlDataSlot = new EnumNetworkDataSlot<>(RedstoneControl.class,
                this::getRedstoneControl, e -> {
                redstoneControl = e;
                updateRedstone();
            });
            addDataSlot(redstoneControlDataSlot);
        } else {
            redstoneControlDataSlot = null;
        }

        // Register sync slot for ioConfig and setup model data.
        ioConfigDataSlot = new NBTSerializableNetworkDataSlot<>(this::getIOConfig, () -> {
            if (level != null && level.isClientSide()) {
                onIOConfigChanged();
            }
        });
        addDataSlot(ioConfigDataSlot);

        addDataSlot(new SetNetworkDataSlot<>(this::getMachineStates, l -> states = l, MachineState::toNBT , MachineState::fromNBT, MachineState::toBuffer, MachineState::fromBuffer ));
    }

    // region IO Config

    /**
     * Create the IO Config.
     * Override and return FixedIOConfig to stop it from being configurable.
     * Must never be null!
     */
    protected IIOConfig createIOConfig() {
        return new IOConfig() {
            @Override
            protected void onChanged(Direction side, IOMode oldMode, IOMode newMode) {
                if (level == null) {
                    return;
                }

                // Mark entity as changed.
                setChanged();

                // Invalidate capabilities for this side as the side has been disabled.
                if (newMode == IOMode.DISABLED) {
                    invalidateSide(side);
                }

                // Notify neighbors of update
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());

                // Mark change
                onIOConfigChanged(side, oldMode, newMode);
            }

            @Override
            protected Direction getBlockFacing() {
                BlockState state = getBlockState();
                if (state.hasProperty(MachineBlock.FACING)) {
                    return getBlockState().getValue(MachineBlock.FACING);
                }

                return super.getBlockFacing();
            }

            @Override
            public boolean supportsMode(Direction side, IOMode mode) {
                return supportsIOMode(side, mode);
            }
        };
    }

    protected void onIOConfigChanged(Direction side, IOMode oldMode, IOMode newMode) {
        if (level != null && level.isClientSide()) {
            clientUpdateSlot(ioConfigDataSlot, getIOConfig());
        }
    }

    /**
     * Get the IO Config for this machine.
     */
    public final IIOConfig getIOConfig() {
        return this.ioConfig;
    }

    /**
     * Override to declare custom constraints on IOMode's for sides of blocks.
     */
    @SuppressWarnings("unused")
    protected boolean supportsIOMode(Direction side, IOMode mode) {
        return true;
    }

    @NotNull
    @Override
    public ModelData getModelData() {
        return getIOConfig().renderOverlay() ? modelData : ModelData.EMPTY;
    }

    private void onIOConfigChanged() {
        if (this.level == null) {
            return;
        }

        if (ioConfig.renderOverlay()) {
            modelData = modelData.derive().with(IO_CONFIG_PROPERTY, ioConfig).build();
            requestModelDataUpdate();
        }

        this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    // endregion

    // region range

    public boolean isRangeVisible() {
        return rangeVisible;
    }

    public void setIsRangeVisible(boolean visible) {
        if (level != null && level.isClientSide()) {
            clientUpdateSlot(rangeVisibleDataSlot, visible);
        } else {
            this.rangeVisible = visible;
        }
    }

    public int getMaxRange() {
        return 0;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        if (level != null && level.isClientSide()) {
            clientUpdateSlot(rangeDataSlot, range);
        } else {
            this.range = range;
        }
    }

    public void decreaseRange() {
        if (this.range > 0) {
            if (level != null && level.isClientSide()) {
                clientUpdateSlot(rangeDataSlot, range - 1);
            } else {
                this.range--;
            }
        }
    }

    public void increaseRange() {
        if (this.range < getMaxRange()) {
            if (level != null && level.isClientSide()) {
                clientUpdateSlot(rangeDataSlot, range + 1);
            } else {
                this.range++;
            }
        }
    }

    public BlockPos getParticleLocation() {
        return getBlockPos();
    }

    private void generateParticle(RangeParticleData data, BlockPos pos) {
        if (level != null && level.isClientSide()) {
            level.addAlwaysVisibleParticle(data, true, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
        }
    }

    public String getColor(){
        return "000000";
    }

    // endregion

    // region Redstone Control

    /**
     * Whether this block entity supports redstone control
     */
    public boolean supportsRedstoneControl() {
        return true;
    }

    public boolean isRedstoneBlocked() {
        return redstoneBlocked;
    }

    public RedstoneControl getRedstoneControl() {
        return redstoneControl;
    }

    public void setRedstoneControl(RedstoneControl redstoneControl) {
        if (level != null && level.isClientSide()) {
            clientUpdateSlot(redstoneControlDataSlot, redstoneControl);
        } else {
            this.redstoneControl = redstoneControl;
            updateRedstone();
        }
    }

    // endregion

    // region Inventory

    /**
     * Makes the block entity's inventory slot layout.
     */
    @Nullable
    public MachineInventoryLayout createInventoryLayout() {
        return null;
    }

    /**
     * Gets the block entity's inventory slot layout.
     */
    @Nullable
    public MachineInventoryLayout getInventoryLayout() {
        return inventoryLayout;
    }

    @Nullable
    public final MachineInventory getInventory() {
        return inventory;
    }

    /**
     * Only call this if you're sure your machine has an inventory.
     */
    protected final MachineInventory getInventoryNN() {
        return Objects.requireNonNull(inventory);
    }

    /**
     * Called to create an item handler if a slot layout is provided.
     */
    protected MachineInventory createMachineInventory(MachineInventoryLayout layout) {
        return new MachineInventory(getIOConfig(), layout) {
            @Override
            protected void onContentsChanged(int slot) {
                onInventoryContentsChanged(slot);
                setChanged();
            }

            @Override
            public void updateMachineState(MachineState state, boolean add) {
                MachineBlockEntity.this.updateMachineState(state, add);
            }
        };
    }

    /**
     * @apiNote Must call this on custom MachineInventory handlers!
     */
    protected void onInventoryContentsChanged(int slot) { }

    // endregion

    // region Fluid Storage
    @Nullable
    public MachineTankLayout getTankLayout() {
        return null;
    }

    @Nullable
    public final MachineFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    /**
     * Only call this if you're sure your machine has an tank.
     */
    protected final MachineFluidHandler getFluidHandlerNN() {
        return Objects.requireNonNull(fluidHandler);
    }

    @Nullable
    protected MachineFluidHandler createFluidHandler(MachineTankLayout layout) {
        return new MachineFluidHandler(getIOConfig(), layout) {
            @Override
            protected void onContentsChanged(int slot) {
                onTankContentsChanged(slot);
                setChanged();
            }
        };
    }

    /**
     * @apiNote Must call this on custom MachineFluid handlers!
     */
    protected void onTankContentsChanged(int slot) {}

    // endregion

    // region Block Entity ticking

    @Override
    public void serverTick() {
        if (isCapabilityCacheDirty) {
            updateCapabilityCache();
        }

        if (canActSlow()) {
            forceResources();
        }

        super.serverTick();
    }

    @Override
    public void clientTick() {
        if (this.isRangeVisible()) {
            generateParticle(new RangeParticleData(getRange(), this.getColor()), getParticleLocation());
        }

        super.clientTick();
    }

    public boolean canAct() {
        if (this.level == null) {
            return false;
        }

        return !redstoneBlocked;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateRedstone();
    }

    public boolean canActSlow() {
        if (this.level == null) {
            return false;
        }

        return canAct() && this.level.getGameTime() % 5 == 0;
    }

    // endregion

    // region Resource movement

    /**
     * Push and pull resources to/from other blocks.
     */
    private void forceResources() {
        for (Direction direction : Direction.values()) {
            if (ioConfig.getMode(direction).canForce()) {
                // TODO: Maybe some kind of resource distributor so that items are transmitted evenly around? rather than taking the order of Direction.values()
                moveItems(direction);
                moveFluids(direction);
            }
        }
    }

    /**
     * Move items to and fro via the given side.
     */
    private void moveItems(Direction side) {
        // Get our item handler.
        getCapability(ForgeCapabilities.ITEM_HANDLER, side).resolve().ifPresent(selfHandler -> {
            // Get neighboring item handler.
            Optional<IItemHandler> otherHandlerOpt = getNeighbouringCapability(ForgeCapabilities.ITEM_HANDLER, side).resolve();

            otherHandlerOpt.ifPresent(otherHandler -> TransferUtil.distributeItems(ioConfig.getMode(side), selfHandler, otherHandler));
        });
    }

    /**
     * Move fluids to and fro via the given side.
     */
    private void moveFluids(Direction side) {
        // Get our fluid handler
        getCapability(ForgeCapabilities.FLUID_HANDLER, side).resolve().ifPresent(selfHandler -> {
            // Get neighboring fluid handler.
            Optional<IFluidHandler> otherHandlerOpt = getNeighbouringCapability(ForgeCapabilities.FLUID_HANDLER, side).resolve();

            otherHandlerOpt.ifPresent(otherHandler -> TransferUtil.distributeFluids(ioConfig.getMode(side), selfHandler, otherHandler));
        });
    }

    /**
     * Move fluids from one handler to the other.
     */
    @Deprecated(forRemoval = true)
    protected int moveFluids(IFluidHandler from, IFluidHandler to, int maxDrain) {
        FluidStack stack = FluidUtil.tryFluidTransfer(to, from, maxDrain, true);
        return stack.getAmount();
    }

    // endregion

    // region Neighboring Capabilities

    protected <T> LazyOptional<T> getNeighbouringCapability(Capability<T> capability, Direction side) {
        if (level == null) {
            return LazyOptional.empty();
        }

        if (!cachedCapabilityTypes.contains(capability)) {
            // We've not seen this capability before, time to register it!
            cachedCapabilityTypes.add(capability);
            cachedCapabilities.put(capability, new EnumMap<>(Direction.class));

            for (Direction direction : Direction.values()) {
                BlockEntity neighbor = this.level.getBlockEntity(worldPosition.relative(direction));
                populateCachesFor(direction, neighbor, capability);
            }
        }

        if (!cachedCapabilities.get(capability).containsKey(side)) {
            return LazyOptional.empty();
        }

        return cachedCapabilities.get(capability).get(side).cast();
    }

    /**
     * Mark the capability cache as dirty. Will be updated next tick.
     */
    public void markCapabilityCacheDirty() {
        isCapabilityCacheDirty = true;
    }

    /**
     * Update capability cache
     */
    private void updateCapabilityCache() {
        if (this.level != null) {
            clearCaches();

            for (Direction direction : Direction.values()) {
                BlockEntity neighbor = this.level.getBlockEntity(worldPosition.relative(direction));
                populateCaches(direction, neighbor);
            }

            isCapabilityCacheDirty = false;
        }
    }

    private final WeakHashMap<LazyOptional<?>, Boolean> listenedCaps = new WeakHashMap<LazyOptional<?>, Boolean>();

    /**
     * Add invalidation handler to a capability to be notified if it is removed.
     */
    private <T> LazyOptional<T> addInvalidationListener(LazyOptional<T> capability) {
        if (capability.isPresent() && !listenedCaps.containsKey(capability)) {
            capability.addListener(c -> {
                markCapabilityCacheDirty();
                listenedCaps.remove(capability);
            });
            listenedCaps.put(capability, true);
        }

        return capability;
    }

    private void clearCaches() {
        for (Capability<?> capability : cachedCapabilityTypes) {
            cachedCapabilities.get(capability).clear();
        }
    }

    private void populateCaches(Direction direction, @Nullable BlockEntity neighbor) {
        for (Capability<?> capability : cachedCapabilityTypes) {
            populateCachesFor(direction, neighbor, capability);
        }
    }

    private void populateCachesFor(Direction direction, @Nullable BlockEntity neighbor, Capability<?> capability) {
        if (neighbor != null) {
            cachedCapabilities.get(capability).put(direction, addInvalidationListener(neighbor.getCapability(capability, direction.getOpposite())));
        } else {
            cachedCapabilities.get(capability).put(direction, LazyOptional.empty());
        }
    }

    // endregion

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        // Save io config.
        pTag.put(MachineNBTKeys.IO_CONFIG, getIOConfig().serializeNBT());

        if (supportsRedstoneControl()) {
            pTag.putInt(MachineNBTKeys.REDSTONE_CONTROL, redstoneControl.ordinal());
        }

        if (this.inventory != null) {
            pTag.put(MachineNBTKeys.ITEMS, inventory.serializeNBT());
        }

        if (this.fluidHandler != null) {
            pTag.put(MachineNBTKeys.FLUIDS, fluidHandler.serializeNBT());
        }

        if (getMaxRange() > 0) {
            pTag.putInt(MachineNBTKeys.RANGE, getRange());
            pTag.putBoolean(MachineNBTKeys.RANGE_VISIBLE, isRangeVisible());
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        // Load io config.
        ioConfig.deserializeNBT(pTag.getCompound(MachineNBTKeys.IO_CONFIG));

        if (supportsRedstoneControl()) {
            redstoneControl = RedstoneControl.values()[pTag.getInt(MachineNBTKeys.REDSTONE_CONTROL)];
        }

        if (this.inventory != null) {
            inventory.deserializeNBT(pTag.getCompound(MachineNBTKeys.ITEMS));
        }

        if (this.fluidHandler != null) {
            fluidHandler.deserializeNBT(pTag.getCompound(MachineNBTKeys.FLUIDS));
        }

        // For rendering io overlays after placed by an nbt filled block item
        if (this.level != null) {
            onIOConfigChanged();
        }

        if (pTag.contains(MachineNBTKeys.RANGE)) {
            this.range = pTag.getInt(MachineNBTKeys.RANGE);
        }

        if (pTag.contains(MachineNBTKeys.RANGE_VISIBLE)) {
            this.rangeVisible = pTag.getBoolean(MachineNBTKeys.RANGE_VISIBLE);
        }


        // Mark capability cache dirty
        isCapabilityCacheDirty = true;

        super.load(pTag);
    }

    // endregion

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    //called when a player uses the block entity, before menu is may open.
    public InteractionResult onBlockEntityUsed(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    public boolean stillValid(Player pPlayer) {
        if (this.level == null) {
            return false;
        }

        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }

        return pPlayer.canReach(this.worldPosition, 1.5);
    }

    @UseOnly(LogicalSide.SERVER)
    @Override
    public InteractionResult onWrenched(@Nullable Player player, @Nullable Direction side) {
        if (player == null || level == null) {
            return InteractionResult.PASS;
        }

        if (player.isSecondaryUseActive()) {//aka break block
            BlockPos pos = getBlockPos();
            BlockState state = getBlockState();
            Block block = state.getBlock();

            if (level instanceof ServerLevel serverLevel) {
                List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, serverLevel.getBlockEntity(pos));
                Inventory inventory = player.getInventory();
                for (ItemStack item : drops) {
                    inventory.placeItemBackInInventory(item);
                }
            }

            block.playerWillDestroy(level, pos, state, player);
            level.removeBlock(pos, false);
            block.destroy(level, pos, state);

            //TODO: custom sound when sound manager is up and running??
        } else {
            // Cycle side config
            if (level.isClientSide()) {
                if (side != null) {
                    ioConfig.cycleMode(side); // TODO: Maybe a check to see if we can cycle?
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    public boolean canOpenMenu() {
        return true;
    }

    public int getLightEmission() {
        return getBlockState().getLightEmission();
    }

    public Set<MachineState> getMachineStates() {
        return states;
    }

    public void updateMachineState(MachineState state, boolean add) {
        if (level != null && level.isClientSide) {
            return;
        }
        if (add) {
            states.add(state);
        } else {
            states.remove(state);
        }
    }

    public void neighborChanged(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        updateRedstone();
    }

    private void updateRedstone() {
        if (supportsRedstoneControl()) {
            boolean active = redstoneControl.isActive(this.level.hasNeighborSignal(worldPosition));
            updateMachineState(MachineState.REDSTONE, !active);
            this.redstoneBlocked = !active;
        }
    }
}
