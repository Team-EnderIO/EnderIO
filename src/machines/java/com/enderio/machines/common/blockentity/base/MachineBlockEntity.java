package com.enderio.machines.common.blockentity.base;

import com.enderio.api.UseOnly;
import com.enderio.api.capability.ISideConfig;
import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.base.common.blockentity.IWrenchable;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.core.common.sync.EnumDataSlot;
import com.enderio.core.common.sync.NBTSerializableDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.core.common.util.PlayerInteractionUtil;
import com.enderio.machines.common.block.MachineBlock;
import com.enderio.machines.common.io.IOConfig;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public abstract class MachineBlockEntity extends EnderBlockEntity implements MenuProvider, IWrenchable {

    // region IO Configuration

    private final IIOConfig ioConfig;

    public static final ModelProperty<IIOConfig> IO_CONFIG_PROPERTY = new ModelProperty<>();

    private ModelData modelData = ModelData.EMPTY;

    // endregion

    // region Redstone Control

    private RedstoneControl redstoneControl = RedstoneControl.ALWAYS_ACTIVE;

    // endregion

    // region Items and Fluids

    @Nullable
    private final MachineInventory inventory;

    // Caches for external block interaction

    // TODO: I would like for these to be a nested map. One on direction and one on type.
    //       This way you can register what types you listen to with a simple method.
    private final EnumMap<Direction, LazyOptional<IItemHandler>> itemHandlerCache = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, LazyOptional<IFluidHandler>> fluidHandlerCache = new EnumMap<>(Direction.class);
    private boolean isCapabilityCacheDirty = false;

    // endregion

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);

        // Create IO Config.
        this.ioConfig = createIOConfig();
        addCapabilityProvider(ioConfig);

        // If the machine declares an inventory layout, use it to create a handler
        MachineInventoryLayout slotLayout = getInventoryLayout();
        if (slotLayout != null) {
            inventory = createMachineInventory(slotLayout);
            addCapabilityProvider(inventory);
        } else {
            inventory = null;
        }

        if (supportsRedstoneControl()) {
            add2WayDataSlot(new EnumDataSlot<>(this::getRedstoneControl, this::setRedstoneControl, SyncMode.GUI));
        }

        // Register sync slot for ioConfig and setup model data.
        add2WayDataSlot(new NBTSerializableDataSlot<>(this::getIOConfig, SyncMode.WORLD, () -> {
            if (level != null && level.isClientSide()) {
                onIOConfigChanged();
            }
        }));
    }

    // region Per-machine config/features

    /**
     * Whether this block entity supports redstone control
     */
    public boolean supportsRedstoneControl() {
        return true;
    }

    /**
     * Get the block entity's inventory slot layout.
     */
    @Nullable
    public MachineInventoryLayout getInventoryLayout() {
        return null;
    }

    // endregion

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
                if (MachineBlockEntity.this.level == null) {
                    return;
                }

                // Mark entity as changed.
                setChanged();

                // Invalidate capabilities for this side as the side has been disabled.
                if (newMode == IOMode.DISABLED) {
                    invalidateSide(side);
                }

                // Notify neighbors of update
                MachineBlockEntity.this.level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
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

    // region Inventory

    @Nullable
    public final MachineInventory getInventory() {
        return inventory;
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
        };
    }

    /**
     * @apiNote Must call this on custom MachineInventory handlers!
     */
    protected void onInventoryContentsChanged(int slot) { }

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

    public boolean canAct() {
        if (this.level == null) {
            return false;
        }

        if (supportsRedstoneControl()) {
            return redstoneControl.isActive(this.level.hasNeighborSignal(worldPosition));
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
        // Get our item handler.
        getCapability(ForgeCapabilities.ITEM_HANDLER, side).resolve().ifPresent(selfHandler -> {
            // Get neighboring item handler.
            Optional<IItemHandler> otherHandler = getNeighboringItemHandler(side).resolve();

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
        getCapability(ForgeCapabilities.FLUID_HANDLER, side).resolve().ifPresent(selfHandler -> {
            // Get neighboring fluid handler.
            Optional<IFluidHandler> otherHandler = getNeighboringFluidHandler(side).resolve();

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

    // endregion

    // region Neighboring Capabilities

    protected LazyOptional<IItemHandler> getNeighboringItemHandler(Direction side) {
        if (!itemHandlerCache.containsKey(side))
            return LazyOptional.empty();
        return itemHandlerCache.get(side);
    }

    protected LazyOptional<IFluidHandler> getNeighboringFluidHandler(Direction side) {
        if (!fluidHandlerCache.containsKey(side))
            return LazyOptional.empty();
        return fluidHandlerCache.get(side);
    }

    /**
     * Add invalidation handler to a capability to be notified if it is removed.
     */
    protected <T> LazyOptional<T> addInvalidationListener(LazyOptional<T> capability) {
        if (capability.isPresent())
            capability.addListener(this::markCapabilityCacheDirty);
        return capability;
    }

    /**
     * Mark the capability cache as dirty. Will be updated next tick.
     */
    private <T> void markCapabilityCacheDirty(LazyOptional<T> capability) {
        isCapabilityCacheDirty = true;
    }

    /**
     * Update capability cache
     */
    public void updateCapabilityCache() {
        if (this.level != null) {
            clearCaches();

            for (Direction direction : Direction.values()) {
                BlockEntity neighbor = this.level.getBlockEntity(worldPosition.relative(direction));
                populateCaches(direction, neighbor);
            }

            isCapabilityCacheDirty = true;
        }
    }

    // Override the next two to implement new capability caches on the machine.
    protected void clearCaches() {
        itemHandlerCache.clear();
        fluidHandlerCache.clear();
    }

    protected void populateCaches(Direction direction, @Nullable BlockEntity neighbor) {
        if (neighbor != null) {
            itemHandlerCache.put(direction, addInvalidationListener(neighbor.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite())));
            fluidHandlerCache.put(direction, addInvalidationListener(neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite())));
        } else {
            itemHandlerCache.put(direction, LazyOptional.empty());
            fluidHandlerCache.put(direction, LazyOptional.empty());
        }
    }

    // endregion

    // region Serialization

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
        if (this.level != null) {
            onIOConfigChanged();
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

        return pPlayer.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <=
            Mth.square(pPlayer.getAttributeValue(ForgeMod.BLOCK_REACH.get()));
    }

    public RedstoneControl getRedstoneControl() {
        return redstoneControl;
    }

    public void setRedstoneControl(RedstoneControl redstoneControl) {
        this.redstoneControl = redstoneControl;
    }

    @UseOnly(LogicalSide.SERVER)
    @Override
    public InteractionResult onWrenched(UseOnContext context) {
        Player player = context.getPlayer();
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
            // Check for side config capability
            LazyOptional<ISideConfig> optSideConfig = getCapability(EIOCapabilities.SIDE_CONFIG, context.getClickedFace());
            if (optSideConfig.isPresent()) {
                // Cycle state.
                optSideConfig.ifPresent(ISideConfig::cycleMode);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    public boolean canOpenMenu() {
        return true;
    }
}
