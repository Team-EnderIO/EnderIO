package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.ICapacitorData;
import com.enderio.api.capacitor.ICapacitorScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.base.common.capacitor.CapacitorUtil;
import com.enderio.base.common.capacitor.DefaultCapacitorData;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.blockentity.sync.MachineEnergyDataSlot;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.machines.common.io.energy.ImmutableMachineEnergyStorage;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A machine that stores energy.
 */
public abstract class PoweredMachineEntity extends MachineBlockEntity {
    /**
     * The energy storage medium for the block entity.
     * This will be a mutable energy storage.
     */
    protected final MachineEnergyStorage energyStorage;
    @Nullable
    protected final MachineEnergyStorage exposedEnergyStorage;

    /**
     * The client value of the energy storage.
     * This will be an instance of {@link ImmutableMachineEnergyStorage}.
     */
    private IMachineEnergyStorage clientEnergyStorage = ImmutableMachineEnergyStorage.EMPTY;

    private ICapacitorData cachedCapacitorData = DefaultCapacitorData.NONE;
    private boolean capacitorCacheDirty;

    public PoweredMachineEntity(EnergyIOMode energyIOMode, ICapacitorScalable capacity, ICapacitorScalable usageRate, BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);

        // Create energy storage
        this.energyStorage = createEnergyStorage(energyIOMode,
            capacity.scaleI(this::getCapacitorData),
            usageRate.scaleI(this::getCapacitorData));

        // Create exposed energy storage.
        // Default is that createExposedEnergyStorage returns the existing energy storage.
        this.exposedEnergyStorage = createExposedEnergyStorage();
        if (exposedEnergyStorage != null) {
            addCapabilityProvider(exposedEnergyStorage);
        }

        // Mark capacitor cache as dirty
        capacitorCacheDirty = true;

        // new new new way of syncing energy storage.
        // TODO: Need to verify this actually works as we expect during this rework.
        addDataSlot(new MachineEnergyDataSlot(this::getEnergyStorage, storage -> clientEnergyStorage = storage, SyncMode.GUI));
    }

    @Override
    public void serverTick() {
        // If redstone config is not enabled.
        if (canAct()) {
            // Push energy to other blocks.
            pushEnergy();
        }

        super.serverTick();
    }

    // region Energy

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

    /**
     * Get the machine's exposed energy storage.
     * Will likely be the same as the normal energy storage, but sometimes might expose a different energy storage.
     * For example a wrapper for combining photovoltaic cells or capacitor banks
     */
    public final IMachineEnergyStorage getExposedEnergyStorage() {
        if (exposedEnergyStorage != null)
            return exposedEnergyStorage;
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
        if (!getExposedEnergyStorage().getIOMode().canOutput())
            return;

        // Transmit power out all sides.
        for (Direction side : Direction.values()) {
            // Get our energy handler, this will handle all sided tests for us.
            getCapability(ForgeCapabilities.ENERGY, side).resolve().ifPresent(selfHandler -> {
                // Get the other energy handler
                Optional<IEnergyStorage> otherHandler = getNeighbouringCapability(ForgeCapabilities.ENERGY, side).resolve();
                if (otherHandler.isPresent()) {
                    // Don't insert into self. (Solar panels)
                    if (selfHandler == otherHandler.get()) {
                        return;
                    }

                    // If the other handler can receive power transmit ours
                    if (otherHandler.get().canReceive()) {
                        int received = otherHandler.get().receiveEnergy(selfHandler.getEnergyStored(), false);

                        // Consume that energy from our buffer.
                        getExposedEnergyStorage().takeEnergy(received);
                    }
                }
            });
        }
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
            }
        };
    }

    // endregion

    // region Capacitors

    /**
     * Whether the machine requires a capacitor to operate.
     */
    public final boolean requiresCapacitor() {
        MachineInventoryLayout layout = getInventoryLayout();
        if (layout == null)
            return false;
        return layout.supportsCapacitor();
    }

    public final int getCapacitorSlot() {
        MachineInventoryLayout layout = getInventoryLayout();
        if (layout == null)
            return -1;
        return layout.getCapacitorSlot();
    }

    /**
     * Whether the machine has a capacitor installed.
     */
    public boolean isCapacitorInstalled() {
        if (capacitorCacheDirty)
            cacheCapacitorData();
        return cachedCapacitorData != DefaultCapacitorData.NONE;
    }

    public ItemStack getCapacitorItem() {
        MachineInventory inventory = getInventory();
        MachineInventoryLayout layout = getInventoryLayout();
        if (inventory == null || layout == null)
            return ItemStack.EMPTY;
        return inventory.getStackInSlot(layout.getCapacitorSlot());
    }

    /**
     * Get the capacitor data for the machine.
     */
    public ICapacitorData getCapacitorData() {
        if (capacitorCacheDirty)
            cacheCapacitorData();
        return cachedCapacitorData;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        MachineInventoryLayout inventoryLayout = getInventoryLayout();
        if (inventoryLayout != null && inventoryLayout.getCapacitorSlot() == slot) {
            capacitorCacheDirty = true;
        }
        super.onInventoryContentsChanged(slot);
    }

    private void cacheCapacitorData() {
        if (level == null) {
            return;
        }

        capacitorCacheDirty = false;

        // Don't do this on client side, client waits for the sync packet.
        if (level.isClientSide()) {
            return;
        }

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
        if (energyStorage instanceof MachineEnergyStorage storage)
            pTag.put("energy", storage.serializeNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        var energyStorage = getEnergyStorage();
        if (energyStorage instanceof MachineEnergyStorage storage)
            storage.deserializeNBT(pTag.getCompound("energy"));
        super.load(pTag);
    }

    // endregion
}
