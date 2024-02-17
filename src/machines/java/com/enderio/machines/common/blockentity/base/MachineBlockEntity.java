package com.enderio.machines.common.blockentity.base;

import com.enderio.api.UseOnly;
import com.enderio.api.capability.ISideConfig;
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
import com.enderio.core.common.util.PlayerInteractionUtil;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.block.MachineBlock;
import com.enderio.machines.common.blockentity.MachineState;
import com.enderio.machines.common.io.IOConfig;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public abstract class MachineBlockEntity extends EnderBlockEntity implements MenuProvider, IWrenchable {

    public static final ICapabilityProvider<MachineBlockEntity, Direction, ISideConfig> SIDE_CONFIG_PROVIDER =
        (be, side) -> new SidedIOConfig(be.ioConfig, side);

    public static final ICapabilityProvider<MachineBlockEntity, Direction, IItemHandler> ITEM_HANDLER_PROVIDER =
        (be, side) -> be.inventory != null ? be.inventory.getForSide(side) : null;

    public static final ICapabilityProvider<MachineBlockEntity, Direction, IFluidHandler> FLUID_HANDLER_PROVIDER =
        (be, side) -> be.fluidHandler != null ? be.fluidHandler.getForSide(side) : null;

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

    // endregion

    // region External Capability Caches

    private final List<BlockCapability<?, ?>> cachedCapabilityTypes = new ArrayList<>();
    private final Map<BlockCapability<?, ?>, EnumMap<Direction, BlockCapabilityCache<?, ?>>> cachedCapabilities = new HashMap<>();

    // endregion

    // region Common Dataslots

    private final EnumNetworkDataSlot<RedstoneControl> redstoneControlDataSlot;
    private final NBTSerializableNetworkDataSlot<IIOConfig> ioConfigDataSlot;

    // endregion

    private Set<MachineState> states = new HashSet<>();

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);

        // Create IO Config.
        this.ioConfig = createIOConfig();

        // If the machine declares an inventory layout, use it to create a handler
        MachineInventoryLayout slotLayout = getInventoryLayout();
        if (slotLayout != null) {
            inventory = createMachineInventory(slotLayout);
        } else {
            inventory = null;
        }

        // Create fluid storage
        MachineTankLayout tankLayout = getTankLayout();
        if (tankLayout != null) {
            fluidHandler = createFluidHandler(tankLayout);
        } else {
            fluidHandler = null;
        }

        if (supportsRedstoneControl()) {
            redstoneControlDataSlot = new EnumNetworkDataSlot<>(RedstoneControl.class,
                this::getRedstoneControl, e -> redstoneControl = e);
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

                // Invalidate capabilities
                level.invalidateCapabilities(getBlockPos());

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

    // TODO: Not a big fan of how this works now.
    //       Might rework IIOConfig to serve ISideConfig through getSide(...) in future.
    private record SidedIOConfig(IIOConfig config, Direction side) implements ISideConfig {
        @Override
        public IOMode getMode() {
            return config.getMode(side);
        }

        @Override
        public void setMode(IOMode mode) {
            config.setMode(side, mode);
        }

        @Override
        public void cycleMode() {
            config.cycleMode(side);
        }
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

    public RedstoneControl getRedstoneControl() {
        return redstoneControl;
    }

    public void setRedstoneControl(RedstoneControl redstoneControl) {
        if (level != null && level.isClientSide()) {
            clientUpdateSlot(redstoneControlDataSlot, redstoneControl);
        } else {
            this.redstoneControl = redstoneControl;
        }
    }

    // endregion

    // region Inventory

    /**
     * Get the block entity's inventory slot layout.
     */
    @Nullable
    public MachineInventoryLayout getInventoryLayout() {
        return null;
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

        if (supportsRedstoneControl()) {
            boolean active = redstoneControl.isActive(this.level.hasNeighborSignal(worldPosition));
            updateMachineState(MachineState.REDSTONE, !active);
            return active;
        }

        return true;
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
        var selfHandler = getInventory();
        if (selfHandler == null) {
            return;
        }

        // Get neighboring item handler.
        IItemHandler otherHandler = getNeighbouringCapability(Capabilities.ItemHandler.BLOCK, side);

        if (otherHandler != null) {
            // Get side config
            IOMode mode = ioConfig.getMode(side);

            // Output items to the other provider if enabled.
            if (mode.canPush()) {
                moveItems(selfHandler, otherHandler);
            }

            // Insert items from the other provider if enabled.
            if (mode.canPull()) {
                moveItems(otherHandler, selfHandler);
            }
        }
    }

    /**
     * Move items from one item handler to the other.
     */
    protected void moveItems(IItemHandler from, IItemHandler to) {
        for (int i = 0; i < from.getSlots(); i++) {
            ItemStack extracted = from.extractItem(i, 1, true);
            if (!extracted.isEmpty()) {
                for (int j = 0; j < to.getSlots(); j++) {
                    ItemStack inserted = to.insertItem(j, extracted, false);
                    if (inserted.isEmpty()) {
                        from.extractItem(i, 1, false);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Move fluids to and fro via the given side.
     */
    private void moveFluids(Direction side) {
        var selfHandler = getFluidHandler();
        if (selfHandler == null) {
            return;
        }

        // Get neighboring fluid handler.
        IFluidHandler otherHandler = getNeighbouringCapability(Capabilities.FluidHandler.BLOCK, side);

        if (otherHandler != null) {
            // Get side config
            IOMode mode = ioConfig.getMode(side);

            // Test if we have fluid.
            FluidStack stack = selfHandler.drain(100, FluidAction.SIMULATE);

            // If we have no fluids, see if we can pull. Otherwise, push.
            if (stack.isEmpty() && mode.canPull()) {
                moveFluids(otherHandler, selfHandler, 100);
            } else if (mode.canPush()) {
                moveFluids(selfHandler, otherHandler, 100);
            }
        }
    }

    /**
     * Move fluids from one handler to the other.
     */
    protected int moveFluids(IFluidHandler from, IFluidHandler to, int maxDrain) {
        FluidStack stack = FluidUtil.tryFluidTransfer(to, from, maxDrain, true);
        return stack.getAmount();
    }

    // endregion

    // region Neighboring Capabilities

    // TODO: NEO-PORT: We might want handling for Void contexts.
    //                 However cannot have two methods with same method name and different context type params :(

    @Nullable
    protected <T> T getNeighbouringCapability(BlockCapability<T, Direction> capability, Direction side) {
        if (level == null) {
            return null;
        }

        if (!cachedCapabilityTypes.contains(capability)) {
            // We've not seen this capability before, time to register it!
            cachedCapabilityTypes.add(capability);
            cachedCapabilities.put(capability, new EnumMap<>(Direction.class));

            for (Direction direction : Direction.values()) {
                populateCachesFor(direction, capability);
            }
        }

        if (!cachedCapabilities.get(capability).containsKey(side)) {
            return null;
        }

        //noinspection unchecked
        return (T)cachedCapabilities.get(capability).get(side).getCapability();
    }

    private void populateCachesFor(Direction direction, BlockCapability<?, Direction> capability) {
        if (level instanceof ServerLevel serverLevel) {
            BlockPos neighbourPos = getBlockPos().relative(direction);
            cachedCapabilities.get(capability).put(direction, BlockCapabilityCache.create(capability, serverLevel, neighbourPos, direction.getOpposite()));
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
        if (player != null && level != null && player.isSecondaryUseActive() && level instanceof ServerLevel serverLevel) {//aka break block
            BlockPos pos = getBlockPos();
            BlockState state = getBlockState();
            List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, level.getBlockEntity(pos));
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            player.swing(InteractionHand.MAIN_HAND);
            //TODO: custom sound when sound manager is up and running??
            SoundType soundType = state.getBlock().getSoundType(state,level,pos,null);
            level.playSound(null, pos,soundType.getBreakSound(), SoundSource.BLOCKS,soundType.volume, soundType.pitch);
            PlayerInteractionUtil.putStacksInInventoryFromWorldInteraction(player,pos, drops);
            return InteractionResult.CONSUME;
        } else {
            // Cycle side config
            if (side != null) {
                ioConfig.cycleMode(side); // TODO: Maybe a check to see if we can cycle?
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
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
}
