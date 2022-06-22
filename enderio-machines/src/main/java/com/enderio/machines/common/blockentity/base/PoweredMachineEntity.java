package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capability.ICapacitorData;
import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.energy.EnergyIOMode;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.base.common.capacitor.CapacitorUtil;
import com.enderio.base.common.capacitor.DefaultCapacitorData;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.sync.MachineEnergyDataSlot;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.machines.common.io.energy.ImmutableMachineEnergyStorage;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Optional;

/**
 * A machine that stores energy.
 */
public abstract class PoweredMachineEntity extends MachineBlockEntity {
    /**
     * The energy storage medium for the block entity.
     * This will be a mutable energy storage.
     */
    protected final MachineEnergyStorage energyStorage;

    /**
     * The client value of the energy storage.
     * This will be an instance of {@link ImmutableMachineEnergyStorage}.
     */
    private IMachineEnergyStorage clientEnergyStorage = ImmutableMachineEnergyStorage.EMPTY;

    private final LazyOptional<MachineEnergyStorage> energyStorageCap;

    // Cache for external energy interaction
    private final EnumMap<Direction, LazyOptional<IEnergyStorage>> energyHandlerCache = new EnumMap<>(Direction.class);

    private ICapacitorData cachedCapacitorData = DefaultCapacitorData.NONE;
    private boolean capacitorCacheDirty;

    public PoweredMachineEntity(EnergyIOMode energyIOMode, CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey useKey, BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);

        // Create energy storage
        this.energyStorage = createEnergyStorage(energyIOMode, capacityKey, transferKey, useKey);
        this.energyStorageCap = LazyOptional.of(() -> energyStorage);
        addCapabilityProvider(energyStorage);

        // Mark capacitor cache as dirty
        capacitorCacheDirty = true;

        // new new way of syncing energy storage.
        addDataSlot(new MachineEnergyDataSlot(this::getEnergyStorage, storage -> clientEnergyStorage = storage, SyncMode.GUI));
    }

    @Override
    public void serverTick() {
        // Leak energy once per second
        if (level.getGameTime() % 20 == 0) {
            energyStorage.takeEnergy(getEnergyLeakPerSecond());
        }

        // If redstone config is not enabled.
        if (canAct()) {
            // Push energy to other blocks.
            pushEnergy();
        }

        super.serverTick();
    }

    // region Energy

    // TODO: Machine efficiency features.

    /**
     * Get the machine's energy storage.
     * On client side, this will likely be an instance of {@link ImmutableMachineEnergyStorage}.
     * On server side, it will be an instance descended of {@link MachineEnergyStorage}.
     */
    public final IMachineEnergyStorage getEnergyStorage() {
        if (isClientSide()) {
            return clientEnergyStorage;
        }
        return energyStorage;
    }

    public int getEnergyLeakPerSecond() {
        return 0;
    }

    /**
     * Push energy out to neighboring blocks.
     */
    private void pushEnergy() {
        // Don't bother if our energy storage cannot output ever.
        if (!getEnergyStorage().getIOMode().canOutput())
            return;

        // Transmit power out all sides.
        for (Direction side : Direction.values()) {
            // Get our energy handler, this will handle all sidedness tests for us.
            getCapability(CapabilityEnergy.ENERGY, side).resolve().ifPresent(selfHandler -> {
                // If we can't extract out this side, continue
                if (selfHandler.getEnergyStored() <= 0 || !selfHandler.canExtract())
                    return;

                // Get the other energy handler
                Optional<IEnergyStorage> otherHandler = getNeighboringEnergyHandler(side).resolve();
                if (otherHandler.isPresent()) {
                    // If the other handler can receive power transmit ours
                    if (otherHandler.get().canReceive()) {
                        // Try to send as much as our transfer rate will allow
                        int received = otherHandler.get().receiveEnergy(Math.min(selfHandler.getEnergyStored(), getEnergyStorage().getMaxEnergyTransfer()), false);

                        // Consume that energy from our buffer.
                        getEnergyStorage().takeEnergy(received);
                    }
                }
            });
        }
    }

    /**
     * Create the energy storage medium
     * Override this to customise the behaviour of the energy storage.
     */
    protected MachineEnergyStorage createEnergyStorage(EnergyIOMode energyIOMode, CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey useKey) {
        return new MachineEnergyStorage(getIOConfig(), energyIOMode, this::getCapacitorData, capacityKey, transferKey, useKey) {
            @Override
            protected void onContentsChanged() {
                setChanged();
            }
        };
    }

    // endregion

    // region Neighboring Capabilities

    @Override
    protected void clearCaches() {
        super.clearCaches();
        energyHandlerCache.clear();
    }

    @Override
    protected void populateCaches(Direction direction, @Nullable BlockEntity neighbor) {
        super.populateCaches(direction, neighbor);

        if (neighbor != null) {
            energyHandlerCache.put(direction, addInvalidationListener(neighbor.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite())));
        } else {
            energyHandlerCache.put(direction, LazyOptional.empty());
        }
    }

    protected LazyOptional<IEnergyStorage> getNeighboringEnergyHandler(Direction side) {
        if (!energyHandlerCache.containsKey(side))
            return LazyOptional.empty();
        return energyHandlerCache.get(side);
    }

    // endregion

    // region Capacitors

    /**
     * Whether the machine requires a capacitor to operate.
     */
    public boolean requiresCapacitor() {
        MachineInventoryLayout layout = getInventoryLayout();
        if (layout == null)
            return false;
        return layout.supportsCapacitor();
    }

    public int getCapacitorSlot() {
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
        MachineInventoryLayout layout = getInventoryLayout();
        if (layout == null)
            return ItemStack.EMPTY;
        return getInventory().getStackInSlot(layout.getCapacitorSlot());
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
        if (getInventoryLayout().getCapacitorSlot() == slot) {
            capacitorCacheDirty = true;
        }
        super.onInventoryContentsChanged(slot);
    }

    private void cacheCapacitorData() {
        capacitorCacheDirty = false;

        // Don't do this on client side, client waits for the sync packet.
        if (isClientSide()) {
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
        // Ignore capacitor state on simple machines.
        if (getTier() == MachineTier.SIMPLE)
            return super.canAct();
        return super.canAct() && (!requiresCapacitor() || isCapacitorInstalled());
    }

    // endregion

    // region Capabilities and Serialization

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY && side == null) {
            return energyStorageCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyStorageCap.invalidate();
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        pTag.put("energy", energyStorage.serializeNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        energyStorage.deserializeNBT(pTag.getCompound("energy"));
        super.load(pTag);
    }

    // endregion
}
