package com.enderio.machines.common.blockentity.base;

import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import com.enderio.base.common.blockentity.RedstoneControl;
import com.enderio.base.common.blockentity.SyncedBlockEntity;
import com.enderio.base.common.blockentity.sync.EnumDataSlot;
import com.enderio.base.common.blockentity.sync.NBTSerializableDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.io.IOConfig;
import com.enderio.machines.common.block.MachineBlock;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Optional;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public abstract class MachineBlockEntity extends SyncedBlockEntity implements MenuProvider {

    // region IO Configuration

    private final IIOConfig ioConfig;

    public static final ModelProperty<IIOConfig> IO_CONFIG_PROPERTY = new ModelProperty<>();

    private final IModelData modelData = new ModelDataMap.Builder().build();

    // endregion

    // region Redstone Control

    private RedstoneControl redstoneControl = RedstoneControl.ALWAYS_ACTIVE;

    // endregion

    // region Items and Fluids

    private final MachineInventory inventory;

    private final LazyOptional<MachineInventory> inventoryCap;

    // Caches for external block interaction
    private final EnumMap<Direction, LazyOptional<IItemHandler>> itemHandlerCache = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, LazyOptional<IFluidHandler>> fluidHandlerCache = new EnumMap<>(Direction.class);
    private boolean isCacheDirty = false;

    // endregion

    public MachineBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);

        // Create IO Config.
        this.ioConfig = createIOConfig();

        // If the machine declares an inventory layout, use it to create a handler
        MachineInventoryLayout slotLayout = getInventoryLayout();
        if (slotLayout != null) {
            inventory = createMachineInventory(slotLayout);
            inventoryCap = LazyOptional.of(() -> inventory);
        } else {
            inventory = null;
            inventoryCap = LazyOptional.empty();
        }

        if (supportsRedstoneControl()) {
            // Register sync slot for redstone control.
            add2WayDataSlot(new EnumDataSlot<>(this::getRedstoneControl, this::setRedstoneControl, SyncMode.GUI));
        }

        // Register sync slot for ioConfig and setup model data.
        add2WayDataSlot(new NBTSerializableDataSlot<>(this::getIOConfig, SyncMode.WORLD, () -> {
            if (!isServer() && getIOConfig().renderOverlay()) {
                modelData.setData(IO_CONFIG_PROPERTY, getIOConfig());
                requestModelDataUpdate();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }));

        if (getIOConfig().renderOverlay()) {
            modelData.setData(IO_CONFIG_PROPERTY, getIOConfig());
        }
    }

    // region Per-machine config/features

    /**
     * Get the machine's tier.
     * Abstract to ensure developers don't leave this as default.
     */
    public abstract MachineTier getTier();

    /**
     * Whether this block entity supports redstone control
     */
    public boolean supportsRedstoneControl() {
        return true;
    }

    /**
     * Get the block entity's inventory slot layout.
     */
    public MachineInventoryLayout getInventoryLayout() {
        return null;
    }

    // endregion

    // region IO Config

    /**
     * Create the IO Config.
     * Override and return FixedIOConfig to stop it from being configurable.
     *
     * Must never be null!
     */
    protected IIOConfig createIOConfig() {
        return new IOConfig() {
            @Override
            protected void onChanged() {
                setChanged();
            }

            @Override
            protected Direction getBlockFacing() {
                BlockState state = getBlockState();
                if (state.hasProperty(MachineBlock.FACING))
                    return getBlockState().getValue(MachineBlock.FACING);
                return super.getBlockFacing();
            }

            @Override
            public boolean supportsMode(Direction side, IOMode mode) {
                return supportsIOMode(side, mode);
            }
        };
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
    protected boolean supportsIOMode(Direction side, IOMode mode) {
        // Enhanced machines cannot have IO out the top.
        return getTier() != MachineTier.Enhanced || side != Direction.UP || mode == IOMode.NONE;
    }

    @NotNull
    @Override
    public IModelData getModelData() {
        return getIOConfig().renderOverlay() ? modelData : EmptyModelData.INSTANCE;
    }

    // endregion

    // region Item Handling

    public final MachineInventory getInventory() {
        return inventory;
    }

    public final RecipeWrapper getRecipeWrapper() {
        return new RecipeWrapper(inventory);
    }

    /**
     * Called to create an item handler if a slot layout is provided.
     */
    protected MachineInventory createMachineInventory(MachineInventoryLayout layout) {
        return new MachineInventory(getIOConfig(), layout) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    // endregion

    // region Block Entity ticking

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, MachineBlockEntity pBlockEntity) {
        pBlockEntity.tick();
    }

    @Override
    public void tick() {
        if (isCacheDirty) {
            updateCapabilityCache();
        }

        if (shouldActSlow()) {
            forceResources();
        }

        super.tick();
    }

    public boolean isServer() {
        return !level.isClientSide;
    }

    public boolean shouldAct() {
        if (supportsRedstoneControl())
            return isServer() && redstoneControl.isActive(level.hasNeighborSignal(worldPosition));
        return isServer();
    }

    public boolean shouldActSlow() {
        return shouldAct()
            && level.getGameTime() % 5 == 0;
    }

    // endregion

    // region Item movement

    /**
     * Push and pull resources to/from other blocks.
     */
    private void forceResources() {
        for (Direction direction : Direction.values()) {
            if (ioConfig.getMode(direction).canForce()) {
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
        getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).resolve().ifPresent(selfHandler -> {
            // Get neighboring item handler.
            if (itemHandlerCache.containsKey(side)) {
                Optional<IItemHandler> otherHandler = itemHandlerCache.get(side).resolve();

                if (otherHandler.isPresent()) {
                    // Get side config
                    IOMode mode = ioConfig.getMode(side);

                    // Output items to the other provider if enabled.
                    if (mode.canPush()) {
                        moveItems(selfHandler, otherHandler.get());
                    }

                    // Insert items from the other provider if enabled.
                    if (mode.canPull()) {
                        moveItems(otherHandler.get(), selfHandler);
                    }
                }
            }
        });
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
        // Get our fluid handler
        getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).resolve().ifPresent(selfHandler -> {
            // Get neighboring fluid handler.
            if (fluidHandlerCache.containsKey(side)) {
                Optional<IFluidHandler> otherHandler = fluidHandlerCache.get(side).resolve();

                if (otherHandler.isPresent()) {
                    // Get side config
                    IOMode mode = ioConfig.getMode(side);

                    // Test if we have fluid.
                    FluidStack stack = selfHandler.drain(100, FluidAction.SIMULATE);

                    // If we have no fluids, see if we can pull. Otherwise, push.
                    if (stack.isEmpty() && mode.canPull()) {
                        moveFluids(otherHandler.get(), selfHandler, 100);
                    } else if (mode.canPush()) {
                        moveFluids(selfHandler, otherHandler.get(), 100);
                    }
                }
            }
        });
    }

    /**
     * Move fluids from one handler to the other.
     */
    protected int moveFluids(IFluidHandler from, IFluidHandler to, int maxDrain) {
        FluidStack stack = from.drain(maxDrain, FluidAction.SIMULATE);
        if(stack.isEmpty()) {
            return 0;
        }
        int filled = to.fill(stack, FluidAction.EXECUTE);
        stack.setAmount(filled);
        from.drain(stack, FluidAction.EXECUTE);
        return filled;
    }

    /**
     * Add invalidation handler to a capability to be notified if it is removed.
     */
    private <T> LazyOptional<T> addInvalidationListener(LazyOptional<T> capability) {
        if (capability.isPresent())
            capability.addListener(this::markCapabilityCacheDirty);
        return capability;
    }

    /**
     * Mark the capability cache as dirty. Will be updated next tick.
     */
    private <T> void markCapabilityCacheDirty(LazyOptional<T> capability) {
        isCacheDirty = true;
    }

    /**
     * Update capability cache
     */
    public void updateCapabilityCache() {
        // Update capability cache by searching for item and fluid handlers.
        itemHandlerCache.clear();
        fluidHandlerCache.clear();
        for (Direction direction : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
            if (neighbor != null) {
                itemHandlerCache.put(direction, addInvalidationListener(neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite())));
                fluidHandlerCache.put(direction, addInvalidationListener(neighbor.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite())));
            } else {
                itemHandlerCache.put(direction, LazyOptional.empty());
                fluidHandlerCache.put(direction, LazyOptional.empty());
            }
        }
    }

    // endregion

    // region Capabilities and Serialization

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (side != null) {
            if (cap == EIOCapabilities.SIDE_CONFIG)
                return ioConfig.getCapability(side).cast();

            if (getInventory() != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && getIOConfig().getMode(side).canConnect())
                return LazyOptional.of(() -> inventory.getCapability(side)).cast();
        } else {
            if (getInventory() != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return inventoryCap.cast();
            }
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        ioConfig.invalidateCaps();
        inventory.invalidateCaps();
        inventoryCap.invalidate();
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        // Save io config.
        pTag.put("io_config", getIOConfig().serializeNBT());

        if (supportsRedstoneControl()) {
            pTag.putInt("redstone", redstoneControl.ordinal());
        }

        if (inventory != null) {
            pTag.put("inventory", inventory.serializeNBT());
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        // Load io config.
        ioConfig.deserializeNBT(pTag.getCompound("io_config"));

        if (supportsRedstoneControl()) {
            redstoneControl = RedstoneControl.values()[pTag.getInt("redstone")];
        }

        if (inventory != null) {
            inventory.deserializeNBT(pTag.getCompound("inventory"));
        }

        // For rendering io overlays after placed by an nbt filled block item
        if (level != null) {
            modelData.setData(IO_CONFIG_PROPERTY, ioConfig);
            requestModelDataUpdate();
        }

        // Mark capability cache dirty
        isCacheDirty = true;

        super.load(pTag);
    }

    // endregion

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    public boolean stillValid(Player pPlayer) {
        if (this.level.getBlockEntity(this.worldPosition) != this)
            return false;
        return pPlayer.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    public RedstoneControl getRedstoneControl() {
        return redstoneControl;
    }

    public void setRedstoneControl(RedstoneControl redstoneControl) {
        this.redstoneControl = redstoneControl;
    }
}
