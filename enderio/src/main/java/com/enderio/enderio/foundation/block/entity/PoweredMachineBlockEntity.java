package com.enderio.enderio.foundation.block.entity;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.UseOnly;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.capacitor.CapacitorScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.content.capacitors.CapacitorItem;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.energy.PoweredMachineEnergyStorage;
import com.enderio.enderio.foundation.inventory.MachineInventory;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.energy.IEnergyStorage;

public abstract class PoweredMachineBlockEntity extends MachineBlockEntity implements MachineInstallable {

    public static final ICapabilityProvider<PoweredMachineBlockEntity, Direction, IEnergyStorage> ENERGY_STORAGE_PROVIDER = (
            be, side) -> side == null ? be.energyStorage : be.energyStorage.getSided(side);

    private final CapacitorSupport capacitorSupport;
    private CapacitorData capacitorData = CapacitorData.NONE;
    private boolean isCapacitorDataDirty;

    private final EnergyIOMode energyIOMode;
    private final CapacitorScalable scalableEnergyCapacity;
    private final CapacitorScalable scalableMaxEnergyUse;

    private final PoweredMachineEnergyStorage energyStorage;

    public PoweredMachineBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState,
                                     boolean isIoConfigMutable, CapacitorSupport capacitorSupport, EnergyIOMode energyIOMode,
                                     CapacitorScalable scalableEnergyCapacity, CapacitorScalable scalableMaxEnergyUse) {
        super(type, worldPosition, blockState, isIoConfigMutable);

        this.capacitorSupport = capacitorSupport;
        this.energyIOMode = energyIOMode;
        this.scalableEnergyCapacity = scalableEnergyCapacity;
        this.scalableMaxEnergyUse = scalableMaxEnergyUse;

        // Sanity check for capacitors.
        if (supportsCapacitor() && (!hasInventory() || !getInventory().layout().supportsCapacitor())) {
            throw new IllegalStateException(
                    "A machine which accepts a capacitor must have an inventory with a capacitor slot!");
        }

        energyStorage = new PoweredMachineEnergyStorage(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updatePowerState();
        updateCapacitorState();
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        updatePowerState();
        updateCapacitorState();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        updatePowerState();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        updateCapacitorData();
    }

    // region Energy Storage

    public PoweredMachineEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @UseOnly(LogicalSide.CLIENT)
    public final void clientSetEnergyStored(int energyStored) {
        energyStorage.setEnergyStored(energyStored);
    }

    public final boolean hasEnergy() {
        // If the machine has no capacitor, you cannot interact with it's energy storage
        if (requiresCapacitor() && !isCapacitorInstalled()) {
            return false;
        }

        return energyStorage.getEnergyStored() > 0;
    }

    public final int getMaxEnergyStored() {
        // TODO: Scalable might need redesigned to just scale directly...
        return scalableEnergyCapacity.scaleI(this::getCapacitorData).get();
    }

    public final int getMaxEnergyUse() {
        return scalableMaxEnergyUse.scaleI(this::getCapacitorData).get();
    }

    public final EnergyIOMode energyIOMode() {
        return energyIOMode;
    }

    private void updatePowerState() {
        updateMachineState(MachineState.NO_POWER, energyStorage.getEnergyStored() <= 0);
    }

    // region Distribution

    @Override
    protected int distributeResourcesInterval() {
        return energyIOMode.canOutput() ? 1 : super.distributeResourcesInterval();
    }

    @Override
    protected void distributeResources(Direction side) {
        super.distributeResources(side);

        if (energyIOMode.canOutput() && getIOMode(side).canPush()) {
            distributeEnergy(side);
        }
    }

    private void distributeEnergy(Direction side) {
        // Get the other energy handler
        IEnergyStorage otherHandler = getNeighbouringCapability(Capabilities.EnergyStorage.BLOCK, side);
        if (otherHandler == null) {
            return;
        }

        // If the other handler can receive power transmit ours
        if (otherHandler.canReceive()) {
            int energyToReceive = energyStorage.extractEnergy(Integer.MAX_VALUE, true);
            int received = otherHandler.receiveEnergy(energyToReceive, false);
            energyStorage.extractEnergy(received, false);
        }
    }

    // endregion

    // endregion

    // region Capacitor

    public final CapacitorSupport capacitorSupport() {
        return capacitorSupport;
    }

    public final boolean supportsCapacitor() {
        return capacitorSupport != CapacitorSupport.NONE;
    }

    public final boolean requiresCapacitor() {
        return capacitorSupport == CapacitorSupport.REQUIRED;
    }

    public boolean isCapacitorInstalled() {
        if (!supportsCapacitor()) {
            // TODO: Should this be an exception because we do not support capacitors?
            return false;
        }

        if (level != null && level.isClientSide()) {
            return !getCapacitorItem().isEmpty();
        }

        if (isCapacitorDataDirty) {
            updateCapacitorData();
        }

        return !capacitorData.equals(CapacitorData.NONE);
    }

    @UseOnly(LogicalSide.SERVER)
    public CapacitorData getCapacitorData() {
        if (!supportsCapacitor()) {
            throw new IllegalStateException("Unable to get capacitor data, this machine does not support capacitors!");
        }

        if (isCapacitorDataDirty) {
            updateCapacitorData();
        }

        return capacitorData;
    }

    public final ItemStack getCapacitorItem() {
        MachineInventory inventory = getInventory();
        if (inventory == null) {
            return ItemStack.EMPTY;
        }

        return inventory.getStackInSlot(inventory.layout().getCapacitorSlot());
    }

    public final int getCapacitorSlotIndex() {
        if (!hasInventory()) {
            throw new IllegalStateException("Attempt to get capacitor slot for machine with no inventory!");
        }

        var layout = getInventory().layout();
        if (!layout.supportsCapacitor()) {
            throw new IllegalStateException("Unable to get capacitor slot index, inventory has no capacitor slot.");
        }

        return layout.getCapacitorSlot();
    }

    protected void updateCapacitorData() {
        // Wait for the level to be loaded.
        if (level == null) {
            return;
        }

        isCapacitorDataDirty = false;

        if (supportsCapacitor()) {
            var capacitorItem = getCapacitorItem();
            var capacitorExtension = capacitorItem.getCapability(EnderIOCapabilities.CAPACITOR_EXTENSION);
            if (capacitorExtension != null) {
                capacitorData = capacitorExtension.getCapacitorData(capacitorItem, level);
            } else {
                capacitorData = getCapacitorItem().getOrDefault(EIODataComponents.CAPACITOR_DATA, CapacitorData.NONE);
            }

            updateCapacitorState();
        }
    }

    private void updateCapacitorState() {
        updateMachineState(MachineState.NO_CAPACITOR, supportsCapacitor() && !isCapacitorInstalled());
    }

    // TODO: Ensure this is called by all machines.
    protected void onMachineUsed() {
        if (supportsCapacitor()) {
            if (!(level instanceof ServerLevel serverLevel)) {
                return;
            }

            var capacitorItem = getCapacitorItem();
            var capacitorExtension = capacitorItem.getCapability(EnderIOCapabilities.CAPACITOR_EXTENSION);
            if (capacitorExtension != null) {
                capacitorExtension.onMachineUsed(capacitorItem, serverLevel);
            }
        }
    }

    // region MachineInstallable Implementation

    @Override
    public InteractionResult tryItemInstall(ItemStack stack, UseOnContext context) {
        if (stack.getItem() instanceof CapacitorItem && supportsCapacitor() && !isCapacitorInstalled()) {
            MachineInventory inventory = getInventory();
            inventory.setStackInSlot(inventory.layout().getCapacitorSlot(), stack.copyWithCount(1));
            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    // endregion

    // endregion

    // region Serialization

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(MachineNBTKeys.ENERGY, energyStorage);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        input.child(MachineNBTKeys.ENERGY)
            .ifPresent(energyStorage::deserialize);

        updateCapacitorData();
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentInput) {
        super.applyImplicitComponents(componentInput);
        energyStorage.setEnergyStored(componentInput.getOrDefault(EIODataComponents.ENERGY, 0));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(EIODataComponents.ENERGY, energyStorage.getEnergyStored());
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard(MachineNBTKeys.ENERGY);
    }

    // endregion
}
