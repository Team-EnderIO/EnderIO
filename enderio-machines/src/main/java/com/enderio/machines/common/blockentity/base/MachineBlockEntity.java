package com.enderio.machines.common.blockentity.base;

import com.enderio.base.common.blockentity.RedstoneControl;
import com.enderio.base.common.blockentity.SyncedBlockEntity;
import com.enderio.base.common.blockentity.sync.EnumDataSlot;
import com.enderio.base.common.blockentity.sync.NBTSerializableDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.io.IOConfig;
import com.enderio.machines.common.io.item.ItemHandlerMaster;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Optional;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public abstract class MachineBlockEntity extends SyncedBlockEntity implements MenuProvider {

    // region IO Configuration

    private final IOConfig ioConfig = new IOConfig();

    public static final ModelProperty<IOConfig> IO_CONFIG_PROPERTY = new ModelProperty<>();

    private final IModelData modelData = new ModelDataMap.Builder().build();

    // endregion

    // region Redstone Control

    private RedstoneControl redstoneControl = RedstoneControl.ALWAYS_ACTIVE;

    // endregion

    // region Items and Fluids

    private ItemHandlerMaster itemHandler;

    private final EnumMap<Direction, LazyOptional<IItemHandler>> itemHandlerCache = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, LazyOptional<IFluidHandler>> fluidHandlerCache = new EnumMap<>(Direction.class);
    private boolean isCacheDirty = false;

    // endregion

    public MachineBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);

        // If the machine declares an inventory layout, use it to create a handler
        MachineInventoryLayout slotLayout = getInventoryLayout();
        if (slotLayout != null) {
            itemHandler = createItemHandler(slotLayout);
        }

        if (supportsRedstoneControl()) {
            // Register sync slot for redstone control.
            add2WayDataSlot(new EnumDataSlot<>(this::getRedstoneControl, this::setRedstoneControl, SyncMode.GUI));
        }

        if (supportsIo()) {
            // Register sync slot for ioConfig and setup model data.
            add2WayDataSlot(new NBTSerializableDataSlot<>(() -> ioConfig, SyncMode.WORLD, () -> {
                if (!isServer()) {
                    modelData.setData(IO_CONFIG_PROPERTY, ioConfig);
                    requestModelDataUpdate();
                }
            }));

            modelData.setData(IO_CONFIG_PROPERTY, ioConfig);
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
        return true; // TODO: Is this a reasonable default?
    }

    /**
     * Whether or not this block entity supports item and fluid transfer.
     */
    public boolean supportsIo() { // TODO: Maybe better name?
        return true; // TODO: Is this a reasonable default
    }

    /**
     * Get the block entity's inventory slot layout.
     */
    public MachineInventoryLayout getInventoryLayout() {
        return null;
    }

    // endregion

    @NotNull
    @Override
    public IModelData getModelData() {
        return supportsIo() ? modelData : EmptyModelData.INSTANCE;
    }

    public final IOConfig getIoConfig() {
        return this.ioConfig;
    }

    // TODO: supportsIOMode method.

    // region Item Handling

    public final ItemHandlerMaster getInventory() {
        return itemHandler;
    }

    public final RecipeWrapper getRecipeWrapper() {
        return new RecipeWrapper(itemHandler);
    }

    /**
     * Called to create an item handler if a slot layout is provided.
     */
    protected ItemHandlerMaster createItemHandler(MachineInventoryLayout layout) {
        return new ItemHandlerMaster(getIoConfig(), layout) {
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
            updateCache();
        }

        if (shouldActSlow()) {
            moveResources();
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

    // TODO: Calling @agnor99 to maybe document some of what this is doing?

    private void moveResources() {
        // TODO: What do we do if ioConfig's are disabled. Allow or disallow movement?
        for (Direction direction : Direction.values()) {
            if (ioConfig.getIO(direction).canForce()) {
                moveItems(direction);
                moveFluids(direction);
            }
        }
    }

    public void updateCache() {
        itemHandlerCache.clear();
        fluidHandlerCache.clear();
        for (Direction direction: Direction.values()) {
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

    /**
     * needs to be called to prevent an instant call of the listener if the capability is not present
     * @param capability
     * @param <T>
     * @return
     */
    private <T> LazyOptional<T> addInvalidationListener(LazyOptional<T> capability) {
        if (capability.isPresent())
            capability.addListener(this::markCacheDirty);
        return capability;
    }

    private <T> void markCacheDirty(LazyOptional<T> capability) {
        isCacheDirty = true;
    }

    private void moveFluids(Direction direction) {
        getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction).resolve().ifPresent(fluidHandler -> {
            if (fluidHandlerCache.containsKey(direction)) {
                Optional<IFluidHandler> otherFluid = fluidHandlerCache.get(direction).resolve();
                if (otherFluid.isPresent()) {
                    FluidStack stack = fluidHandler.drain(100, FluidAction.SIMULATE);
                    if (stack.isEmpty()) {
                        moveFluids(otherFluid.get(), fluidHandler, 100);
                    } else {
                        moveFluids(fluidHandler, otherFluid.get(), 100);
                    }
                }
            }
        });
    }
    public int moveFluids(IFluidHandler from, IFluidHandler to, int maxDrain) {
        FluidStack stack = from.drain(maxDrain, FluidAction.SIMULATE);
        if(stack.isEmpty()) {
            return 0;
        }
        int filled = to.fill(stack, FluidAction.EXECUTE);
        stack.setAmount(filled);
        from.drain(stack, FluidAction.EXECUTE);
        return filled;
    }

    private void moveItems(Direction direction) {
        getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().ifPresent(itemHandler -> {
            if (itemHandlerCache.containsKey(direction)) {
                Optional<IItemHandler> otherItem = itemHandlerCache.get(direction).resolve();

                if (otherItem.isPresent()) {
                    moveItems(itemHandler, otherItem.get());
                    moveItems(otherItem.get(), itemHandler);
                }
            }
        });
    }

    private void moveItems(IItemHandler from, IItemHandler to) {
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

    // endregion

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        if (supportsIo()) {
            pTag.put("io_config", ioConfig.serializeNBT());
        }

        if (supportsRedstoneControl()) {
            pTag.putInt("redstone", redstoneControl.ordinal());
        }

        if (itemHandler != null) {
            pTag.put("inventory", itemHandler.serializeNBT());
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        if (supportsIo()) {
            ioConfig.deserializeNBT(pTag.getCompound("io_config"));
        }

        if (supportsRedstoneControl()) {
            redstoneControl = RedstoneControl.values()[pTag.getInt("redstone")];
        }

        if (itemHandler != null) {
            itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        }

        // For rendering io overlays after placed by an nbt filled block item
        if (level != null) {
            modelData.setData(IO_CONFIG_PROPERTY, ioConfig);
            requestModelDataUpdate();
        }

        super.load(pTag);
    }

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
