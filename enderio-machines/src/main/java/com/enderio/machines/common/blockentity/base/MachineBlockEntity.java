package com.enderio.machines.common.blockentity.base;

import com.enderio.base.common.blockentity.RedstoneControl;
import com.enderio.base.common.blockentity.SyncedBlockEntity;
import com.enderio.base.common.blockentity.sync.EnumDataSlot;
import com.enderio.base.common.blockentity.sync.NBTSerializableDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.data.sidecontrol.IOConfig;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemSlotLayout;
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
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Optional;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public abstract class MachineBlockEntity extends SyncedBlockEntity implements MenuProvider {

    public static final ModelProperty<IOConfig> IO_CONFIG_PROPERTY = new ModelProperty<>();

    private final IOConfig ioConfig = new IOConfig();

    // TODO: This isn't available on some machines, shouldn't be default. Will deal with in future.
    private RedstoneControl redstoneControl = RedstoneControl.ALWAYS_ACTIVE;

    private final EnumMap<Direction, LazyOptional<IItemHandler>> itemHandlerCache = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, LazyOptional<IFluidHandler>> fluidHandlerCache = new EnumMap<>(Direction.class);
    private boolean isCacheDirty = false;
    private final MachineTier tier;

    private ItemHandlerMaster itemHandlerMaster;

    private final IModelData modelData = new ModelDataMap.Builder().build();

    public MachineBlockEntity(MachineTier tier, BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
        this.tier = tier;

        // If the machine declares an inventory layout, use it to create a handler
        getSlotLayout().ifPresent(layout -> itemHandlerMaster = createItemHandler(layout));

        add2WayDataSlot(new EnumDataSlot<>(this::getRedstoneControl, this::setRedstoneControl, SyncMode.GUI));
        add2WayDataSlot(new NBTSerializableDataSlot<>(() -> ioConfig, SyncMode.WORLD, () -> {
            if (!isServer()) {
                modelData.setData(IO_CONFIG_PROPERTY, ioConfig);
                requestModelDataUpdate();
            }
        }));

        modelData.setData(IO_CONFIG_PROPERTY, ioConfig);
    }

    @NotNull
    @Override
    public IModelData getModelData() {
        return modelData;
    }

    // TODO: Could just make this abstract and remove the field...
    public final MachineTier getTier() {
        return tier;
    }

    public final IOConfig getIoConfig() {
        return this.ioConfig;
    }

    // TODO: supportsIOMode method.

    public final ItemHandlerMaster getItemHandler() {
        return itemHandlerMaster;
    }

    public final RecipeWrapper getRecipeWrapper() {
        return new RecipeWrapper(itemHandlerMaster);
    }

    public Optional<ItemSlotLayout> getSlotLayout() {
        return Optional.empty();
    }

    /**
     * Called to create an item handler if a slot layout is provided.
     */
    protected ItemHandlerMaster createItemHandler(ItemSlotLayout layout) {
        throw new NotImplementedException("Dev didn't implement the item handler for this BE");
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

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, MachineBlockEntity pBlockEntity) {
        pBlockEntity.tick();
    }

    @Override
    public void tick() {
        if (isCacheDirty) {
            updateCache();
        }
        if (shouldActSlow()) {
            for (Direction direction : Direction.values()) {
                if (ioConfig.getIO(direction).canForce()) {
                    moveItems(direction);
                    moveFluids(direction);
                }
            }
        }
        super.tick();
    }

    public boolean isServer() {
        return !level.isClientSide;
    }

    public boolean shouldAct() {
        return isServer()
            && redstoneControl.isActive(level.hasNeighborSignal(worldPosition));
    }

    public boolean shouldActSlow() {
        return shouldAct()
            && level.getGameTime() % 5 == 0;
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

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("io_config", ioConfig.serializeNBT());
        pTag.putInt("redstone", redstoneControl.ordinal());

        if (itemHandlerMaster != null) {
            pTag.put("inventory", itemHandlerMaster.serializeNBT());
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        ioConfig.deserializeNBT(pTag.getCompound("io_config"));
        redstoneControl = RedstoneControl.values()[pTag.getInt("redstone")];

        if (itemHandlerMaster != null) {
            itemHandlerMaster.deserializeNBT(pTag.getCompound("inventory"));
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
