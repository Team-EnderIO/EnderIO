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
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
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

    private final LazyOptional<IMachineEnergyStorage> energyStorageCap;

    // Cache for external energy interaction
    private final EnumMap<Direction, LazyOptional<IEnergyStorage>> energyHandlerCache = new EnumMap<>(Direction.class);

    private ICapacitorData cachedCapacitorData = DefaultCapacitorData.NONE;
    private boolean capacitorCacheDirty;

    public PoweredMachineEntity(EnergyIOMode energyIOMode, ICapacitorScalable capacity, ICapacitorScalable usageRate, BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);

        // Create energy storage
        this.energyStorage = createEnergyStorage(energyIOMode,
            capacity.scaleI(this::getCapacitorData),
            usageRate.scaleI(this::getCapacitorData));
        this.energyStorageCap = LazyOptional.of(this::getExposedEnergyStorage);
        this.exposedEnergyStorage = createExposedEnergyStorage();
        addCapabilityProvider(exposedEnergyStorage == null ? energyStorage : exposedEnergyStorage);

        // Mark capacitor cache as dirty
        capacitorCacheDirty = true;

        // new new way of syncing energy storage.
        // TODO: Need to verify this actually works as we expect during this rework.
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
    /**
     * Get the machine's exposed energy storage. Will likely be the same as the normal energy storage, but sometimes might be different, if blocks want to expose a different energystorage (wrapper for combining photovoltaic cells or capacitor banks)
     */
    public final IMachineEnergyStorage getExposedEnergyStorage() {
        if (exposedEnergyStorage != null)
            return exposedEnergyStorage;
        return getEnergyStorage();
    }

    @Nullable
    public MachineEnergyStorage createExposedEnergyStorage() {
        return null;
    }

    public int getEnergyLeakPerSecond() {
        return 0;
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
            // Get our energy handler, this will handle all sidedness tests for us.
            getCapability(ForgeCapabilities.ENERGY, side).resolve().ifPresent(selfHandler -> {
                // If we can't extract out this side, continue
                if (selfHandler.getEnergyStored() <= 0 || !selfHandler.canExtract())
                    return;

                // Get the other energy handler
                Optional<IEnergyStorage> otherHandler = getNeighboringEnergyHandler(side).resolve();
                if (otherHandler.isPresent()) {
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
            energyHandlerCache.put(direction, addInvalidationListener(neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite())));
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
        return super.canAct() && (!requiresCapacitor() || isCapacitorInstalled());
    }

    // endregion

    // region Capabilities and Serialization

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY && side == null) {
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
