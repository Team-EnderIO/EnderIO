package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.ICapacitorData;
import com.enderio.api.capacitor.ICapacitorScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.base.common.blockentity.IMachineInstall;
import com.enderio.base.common.capacitor.CapacitorUtil;
import com.enderio.base.common.capacitor.DefaultCapacitorData;
import com.enderio.base.common.init.EIOAttachments;
import com.enderio.base.common.item.capacitors.BaseCapacitorItem;
import com.enderio.core.common.network.slot.NetworkDataSlot;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.enderio.machines.common.blockentity.MachineState;
import com.enderio.machines.common.blockentity.sync.MachineEnergyNetworkDataSlot;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.machines.common.io.energy.ImmutableMachineEnergyStorage;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A machine that stores energy.
 */
public abstract class PoweredMachineBlockEntity extends MachineBlockEntity implements IMachineInstall {

    public static final ICapabilityProvider<PoweredMachineBlockEntity, Direction, IEnergyStorage> ENERGY_STORAGE_PROVIDER =
        (be, side) -> be.exposedEnergyStorage != null ? be.exposedEnergyStorage.getForSide(side) : null;

    /**
     * The energy storage medium for the block entity.
     * This will be a mutable energy storage.
     */
    protected final MachineEnergyStorage energyStorage;
    @Nullable protected final MachineEnergyStorage exposedEnergyStorage;

    /**
     * The client value of the energy storage.
     * This will be an instance of {@link ImmutableMachineEnergyStorage}.
     */
    protected IMachineEnergyStorage clientEnergyStorage = ImmutableMachineEnergyStorage.EMPTY;

    private ICapacitorData cachedCapacitorData = DefaultCapacitorData.NONE;
    private boolean capacitorCacheDirty;
    private boolean updateModel = false;

    public PoweredMachineBlockEntity(EnergyIOMode energyIOMode, ICapacitorScalable capacity, ICapacitorScalable usageRate, BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);

        // Create energy storage
        this.energyStorage = createEnergyStorage(energyIOMode,
            capacity.scaleI(this::getCapacitorData),
            usageRate.scaleI(this::getCapacitorData));

        // Create exposed energy storage.
        // Default is that createExposedEnergyStorage returns the existing energy storage.
        this.exposedEnergyStorage = createExposedEnergyStorage();

        // Mark capacitor cache as dirty
        capacitorCacheDirty = true;

        // new new new new way of syncing energy storage.
        addDataSlot(createEnergyDataSlot());
    }

    public NetworkDataSlot<?> createEnergyDataSlot() {
        return new MachineEnergyNetworkDataSlot(this::getExposedEnergyStorage, storage -> clientEnergyStorage = storage);
    }

    @Override
    public void serverTick() {
        // If redstone config is not enabled.
        if (canAct()) {
            // Push energy to other blocks.
            pushEnergy();
        }

        if (level != null) {
            BlockState blockState = getBlockState();
            if (blockState.hasProperty(ProgressMachineBlock.POWERED) && blockState.getValue(ProgressMachineBlock.POWERED) != isActive()) {
                if (updateModel) {
                    level.setBlock(getBlockPos(), blockState.setValue(ProgressMachineBlock.POWERED, isActive()), Block.UPDATE_ALL);
                    updateMachineState(MachineState.ACTIVE, isActive());
                }
                updateModel = true;
            } else {
                updateModel = false;
            }
        }

        super.serverTick();
    }

    // region Energy

    /**
     * This should only be a test of if the machine has a "job" or "task".
     * Not whether it is powered or it can act.
     * @return Whether this machine is currently active.
     */
    protected abstract boolean isActive();

    /**
     * Get the machine's energy storage.
     * On client side, this will likely be an instance of {@link ImmutableMachineEnergyStorage}.
     * On server side, it will be an instance descended of {@link MachineEnergyStorage}.
     */
    public final IMachineEnergyStorage getEnergyStorage() {
        if (level != null && level.isClientSide()) {
            return clientEnergyStorage;
        }

        return energyStorage;
    }

    public final boolean hasEnergy() {
        if (requiresCapacitor() && !isCapacitorInstalled()) {
            return false;
        }

        return getEnergyStorage().getEnergyStored() > 0;
    }

    /**
     * Get the machine's exposed energy storage.
     * Will likely be the same as the normal energy storage, but sometimes might expose a different energy storage.
     * For example a wrapper for combining photovoltaic cells or capacitor banks
     */
    public final IMachineEnergyStorage getExposedEnergyStorage() {
        if (exposedEnergyStorage != null) {
            return exposedEnergyStorage;
        }

        return getEnergyStorage();
    }

    /**
     * Create the exposed energy storage.
     * Default behaviour returns the existing energy storage included with the class.
     * Override this and return null to disable exposing energy capability.
     * Override this and return a new energy storage to add a different energy capability.
     */
    @Nullable
    public MachineEnergyStorage createExposedEnergyStorage() {
        return energyStorage;
    }

    /**
     * Push energy out to neighboring blocks.
     */
    private void pushEnergy() {
        // Don't bother if our energy storage cannot output ever.
        if (!getExposedEnergyStorage().getIOMode().canOutput()) {
            return;
        }

        // Transmit power out all sides.
        for (Direction side : Direction.values()) {
            if (!shouldPushEnergyTo(side)) {
                continue;
            }

            var selfHandler = getSelfCapability(Capabilities.EnergyStorage.BLOCK, side);
            if (selfHandler == null) {
                continue;
            }

            // Get the other energy handler
            IEnergyStorage otherHandler = getNeighbouringCapability(Capabilities.EnergyStorage.BLOCK, side);
            if (otherHandler != null) {
                // If the other handler can receive power transmit ours
                if (otherHandler.canReceive()) {
                    int energyToReceive = selfHandler.extractEnergy(selfHandler.getEnergyStored(), true);
                    int received = otherHandler.receiveEnergy(energyToReceive, false);
                    selfHandler.extractEnergy(received, false);
                }
            }
        }
    }

    /**
     * prevent pushing energy to other parts of the same energyMultiblock
     * @param direction
     * @return
     */
    protected boolean shouldPushEnergyTo(Direction direction) {
        return true;
    }

    /**
     * Create the energy storage medium
     * Override this to customise the behaviour of the energy storage.
     */
    protected MachineEnergyStorage createEnergyStorage(EnergyIOMode energyIOMode, Supplier<Integer> capacity, Supplier<Integer> usageRate) {
        return new MachineEnergyStorage(getIOConfig(), energyIOMode, capacity, usageRate) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                updateMachineState(MachineState.NO_POWER, getEnergyStorage().getEnergyStored() <= 0);
            }
        };
    }

    // endregion

    // region Capacitors

    /**
     * Handles right click auto equip for capacitors
     */
    @Override
    public InteractionResult tryItemInstall(ItemStack stack, UseOnContext context) {
        if (stack.getItem() instanceof BaseCapacitorItem && requiresCapacitor() && !isCapacitorInstalled()) {
            MachineInventory inventory = getInventory();
            MachineInventoryLayout layout = getInventoryLayout();
            if (inventory != null && layout != null) {
                inventory.setStackInSlot(layout.getCapacitorSlot(), stack.copyWithCount(1));
                stack.shrink(1);
                return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
            }
        }

        return InteractionResult.PASS;
    }

    /**
     * Whether the machine requires a capacitor to operate.
     */
    public final boolean requiresCapacitor() {
        MachineInventoryLayout layout = getInventoryLayout();
        if (layout == null) {
            return false;
        }

        return layout.supportsCapacitor();
    }

    public final int getCapacitorSlot() {
        MachineInventoryLayout layout = getInventoryLayout();
        if (layout == null) {
            return -1;
        }

        return layout.getCapacitorSlot();
    }

    /**
     * Whether the machine has a capacitor installed.
     */
    public boolean isCapacitorInstalled() {
        if (level != null && level.isClientSide) {
            return !getCapacitorItem().isEmpty();
        }

        if (capacitorCacheDirty) {
            cacheCapacitorData();
        }

        return cachedCapacitorData != DefaultCapacitorData.NONE;
    }

    public ItemStack getCapacitorItem() {
        MachineInventory inventory = getInventory();
        MachineInventoryLayout layout = getInventoryLayout();
        if (inventory == null || layout == null) {
            return ItemStack.EMPTY;
        }

        return inventory.getStackInSlot(layout.getCapacitorSlot());
    }

    /**
     * Get the capacitor data for the machine.
     */
    public ICapacitorData getCapacitorData() {
        if (capacitorCacheDirty) {
            cacheCapacitorData();
        }

        return cachedCapacitorData;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        MachineInventoryLayout inventoryLayout = getInventoryLayout();
        if (inventoryLayout != null && inventoryLayout.getCapacitorSlot() == slot) {
            if (requiresCapacitor()) {
                updateMachineState(MachineState.NO_CAPACITOR, getCapacitorItem().isEmpty());
                capacitorCacheDirty = true;
            }
        }
        super.onInventoryContentsChanged(slot);
    }

    private void cacheCapacitorData() {
        if (level == null) {
            return;
        }

        capacitorCacheDirty = false;

        // Don't do this on client side, client waits for the sync packet.
        // TODO Do we want to sync with a packet cause right now we don't
//        if (level.isClientSide()) {
//            return;
//        }

        MachineInventoryLayout layout = getInventoryLayout();
        if (requiresCapacitor() && layout != null) {
            cachedCapacitorData = CapacitorUtil.getCapacitorData(getCapacitorItem()).orElse(DefaultCapacitorData.NONE);
        } else {
            cachedCapacitorData = DefaultCapacitorData.NONE;
        }
    }

    @Override
    public boolean canAct() {
        return super.canAct() && (!requiresCapacitor() || isCapacitorInstalled());
    }

    // endregion

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag) {
        var energyStorage = getEnergyStorage();
        if (energyStorage instanceof MachineEnergyStorage storage) {
            pTag.put(MachineNBTKeys.ENERGY, storage.serializeNBT());
        }

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        var energyStorage = getEnergyStorage();
        if (energyStorage instanceof MachineEnergyStorage storage && pTag.contains(MachineNBTKeys.ENERGY)) {
            storage.deserializeNBT(pTag.getCompound(MachineNBTKeys.ENERGY));
        }

        super.load(pTag);

        cacheCapacitorData();

        updateMachineState(MachineState.NO_CAPACITOR, requiresCapacitor() && getCapacitorItem().isEmpty());
        updateMachineState(MachineState.NO_POWER, energyStorage.getEnergyStored() <= 0);
    }

    // endregion

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        //These are the values before Load is called. In case the machine is placed down without nbt, Load isn't called, so it will use these values.
        //Ideally I would want to use onLoad, but when placing a block this is called before load is done.
        updateMachineState(MachineState.NO_CAPACITOR, requiresCapacitor() && getCapacitorItem().isEmpty());
        updateMachineState(MachineState.NO_POWER, energyStorage.getEnergyStored() <= 0);
    }

    @Override
    public void copyFromStack(ItemStack stack) {
        super.copyFromStack(stack);
        if (stack.hasData(EIOAttachments.NBT_ATTACHMENT)) {
            CompoundTag tag = stack.getData(EIOAttachments.NBT_ATTACHMENT).getTag();
            //TODO get energy since it's not an attachment?
        }
    }
}
