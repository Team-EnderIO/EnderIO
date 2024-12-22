package com.enderio.machines.common.blockentity.base;

import com.enderio.base.api.capacitor.CapacitorData;
import com.enderio.base.api.capacitor.CapacitorScalable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.base.common.blockentity.MachineInstallable;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.item.capacitors.CapacitorItem;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.block.LegacyProgressMachineBlock;
import com.enderio.machines.common.blockentity.sync.EnergySyncData;
import com.enderio.machines.common.blocks.base.inventory.MachineInventory;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.state.MachineState;
import com.enderio.machines.common.init.MachineAttachments;
import com.enderio.machines.common.init.MachineDataComponents;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.machines.common.io.energy.ImmutableMachineEnergyStorage;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
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

/**
 * A machine that stores energy.
 */
@Deprecated(forRemoval = true, since = "7.1")
public abstract class LegacyPoweredMachineBlockEntity extends LegacyMachineBlockEntity implements MachineInstallable {

    public static final ICapabilityProvider<LegacyPoweredMachineBlockEntity, Direction, IEnergyStorage> ENERGY_STORAGE_PROVIDER = (
            be, side) -> be.exposedEnergyStorage != null ? be.exposedEnergyStorage.getForSide(side) : null;

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
    protected IMachineEnergyStorage clientEnergyStorage = ImmutableMachineEnergyStorage.EMPTY;

    private CapacitorData cachedCapacitorData = CapacitorData.NONE;
    private boolean capacitorCacheDirty;
    private boolean updateModel = false;
    private final boolean hasActiveState;

    public LegacyPoweredMachineBlockEntity(EnergyIOMode energyIOMode, CapacitorScalable capacity,
            CapacitorScalable usageRate, BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);

        // Create energy storage
        this.energyStorage = createEnergyStorage(energyIOMode, capacity.scaleI(this::getCapacitorData),
                usageRate.scaleI(this::getCapacitorData));

        // Create exposed energy storage.
        // Default is that createExposedEnergyStorage returns the existing energy
        // storage.
        this.exposedEnergyStorage = createExposedEnergyStorage();

        // Mark capacitor cache as dirty
        capacitorCacheDirty = true;

        // new new new new way of syncing energy storage.
        addDataSlot(createEnergyDataSlot());

        this.hasActiveState = blockState.hasProperty(LegacyProgressMachineBlock.POWERED);
    }

    public NetworkDataSlot<?> createEnergyDataSlot() {
        return EnergySyncData.DATA_SLOT_TYPE.create(() -> EnergySyncData.from(getExposedEnergyStorage()),
                v -> clientEnergyStorage = v.toImmutableStorage());
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
            boolean isBlockStateOutdated = hasActiveState
                    && blockState.getValue(LegacyProgressMachineBlock.POWERED) != isActive();
            boolean isMachineStateOutdated = getMachineStates().contains(MachineState.ACTIVE) != isActive();
            if (isBlockStateOutdated || isMachineStateOutdated) {
                if (updateModel) {
                    if (isBlockStateOutdated) {
                        level.setBlock(getBlockPos(),
                                blockState.setValue(LegacyProgressMachineBlock.POWERED, isActive()), Block.UPDATE_ALL);
                    }

                    if (isMachineStateOutdated) {
                        updateMachineState(MachineState.ACTIVE, isActive());
                    }
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
        return Objects.requireNonNullElseGet(exposedEnergyStorage, this::getEnergyStorage);
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
    protected MachineEnergyStorage createEnergyStorage(EnergyIOMode energyIOMode, Supplier<Integer> capacity,
            Supplier<Integer> usageRate) {
        return new MachineEnergyStorage(this, energyIOMode, capacity, usageRate) {
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
        if (stack.getItem() instanceof CapacitorItem && requiresCapacitor() && !isCapacitorInstalled()) {
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

        return !cachedCapacitorData.equals(CapacitorData.NONE);
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
    public CapacitorData getCapacitorData() {
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
        capacitorCacheDirty = false;

        MachineInventoryLayout layout = getInventoryLayout();
        if (requiresCapacitor() && layout != null) {
            cachedCapacitorData = getCapacitorItem().getOrDefault(EIODataComponents.CAPACITOR_DATA, CapacitorData.NONE);
        } else {
            cachedCapacitorData = CapacitorData.NONE;
        }
    }

    @Override
    public boolean canAct() {
        return super.canAct() && (!requiresCapacitor() || isCapacitorInstalled());
    }

    // endregion

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);

        var energyStorage = getEnergyStorage();
        if (energyStorage instanceof MachineEnergyStorage storage) {
            pTag.put(MachineNBTKeys.ENERGY, storage.serializeNBT(lookupProvider));
        }
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        var energyStorage = getEnergyStorage();
        if (energyStorage instanceof MachineEnergyStorage storage && pTag.contains(MachineNBTKeys.ENERGY)) {
            storage.deserializeNBT(lookupProvider, pTag.getCompound(MachineNBTKeys.ENERGY));
        }

        super.loadAdditional(pTag, lookupProvider);

        cacheCapacitorData();

        updateMachineState(MachineState.NO_CAPACITOR, requiresCapacitor() && getCapacitorItem().isEmpty());
        updateMachineState(MachineState.NO_POWER, energyStorage.getEnergyStored() <= 0);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);
        energyStorage.setEnergyStored(components.getOrDefault(EIODataComponents.ENERGY, 0));
        setData(MachineAttachments.REDSTONE_CONTROL,
                components.getOrDefault(MachineDataComponents.REDSTONE_CONTROL, RedstoneControl.ALWAYS_ACTIVE));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(EIODataComponents.ENERGY, energyStorage.getEnergyStored());
        components.set(MachineDataComponents.REDSTONE_CONTROL, getData(MachineAttachments.REDSTONE_CONTROL));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(MachineNBTKeys.ENERGY);
        removeData(MachineAttachments.REDSTONE_CONTROL);
    }

    // endregion

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        // These are the values before Load is called. In case the machine is placed
        // down without nbt, Load isn't called, so it will use these values.
        // Ideally I would want to use onLoad, but when placing a block this is called
        // before load is done.
        updateMachineState(MachineState.NO_CAPACITOR, requiresCapacitor() && getCapacitorItem().isEmpty());
        updateMachineState(MachineState.NO_POWER, energyStorage.getEnergyStored() <= 0);
    }
}
