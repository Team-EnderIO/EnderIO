package com.enderio.machines.common.blockentity.base;

import com.enderio.api.UseOnly;
import com.enderio.api.capability.ICapacitorData;
import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.energy.EnergyCapacityPair;
import com.enderio.api.energy.IMachineEnergy;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.base.common.capacitor.CapacitorUtil;
import com.enderio.base.common.capacitor.DefaultCapacitorData;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.blockentity.sync.MachineEnergyDataSlot;
import com.enderio.machines.common.io.energy.ForgeEnergyWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A machine that stores power.
 */
public abstract class PoweredMachineEntity extends MachineBlockEntity implements IMachineEnergy {
    protected final CapacitorKey capacityKey, transferKey, consumptionKey;

    private int storedEnergy;

    private final ForgeEnergyWrapper energyWrapper = new ForgeEnergyWrapper(this);

    @UseOnly(LogicalSide.CLIENT) private EnergyCapacityPair clientEnergy;

    // TODO: Cache capacitor data rather than constantly querying an optional?

    public PoweredMachineEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey consumptionKey, BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);

        // Store capacitor keys
        this.capacityKey = capacityKey;
        this.transferKey = transferKey;
        this.consumptionKey = consumptionKey;

        // Sync energy
        addDataSlot(new MachineEnergyDataSlot(this::getEnergyStored, this::getMaxEnergyStored, vec -> clientEnergy = vec, SyncMode.GUI));
    }

    // region Energy

    // TODO: Energy leakage and efficiency multipliers

    @Override
    public int getEnergyStored() {
        if (level.isClientSide) {
            return clientEnergy.energy();
        }
        return storedEnergy;
    }

    @Override
    public void addEnergy(int energy) {
        storedEnergy = Math.min(this.storedEnergy + energy, getMaxEnergyStored());
        setChanged();
    }

    @Override
    public int consumeEnergy(int energy) {
        int energyConsumed = Math.min(storedEnergy, Math.min(getMaxEnergyConsumption(), energy));
        addEnergy(-energyConsumed);
        return energyConsumed;
    }

    @Override
    public int getMaxEnergyStored() {
        if (level.isClientSide) {
            return clientEnergy.capacity();
        }
        return capacityKey.getInt(getCapacitorData());
    }

    @Override
    public int getMaxEnergyConsumption() {
        return consumptionKey.getInt(getCapacitorData());
    }

    @Override
    public int getMaxEnergyTransfer() {
        return transferKey.getInt(getCapacitorData());
    }

    // TODO: Implement energy leaking.
    @Override
    public int getEnergyLeakRate() {
        return 0;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY && side != null && getIOConfig().getMode(side).canConnect()) {
            return energyWrapper.getCapabilityFor(side).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyWrapper.invalidateCaps();
    }

    @Override
    public boolean shouldAct() {
        // Ignore capacitor state on simple machines.
        if (getTier() == MachineTier.Simple)
            return super.shouldAct();
        return super.shouldAct() && (!requiresCapacitor() || isCapacitorInstalled());
    }

    @Override
    public void tick() {
        if (shouldAct()) {
            pushEnergy();
        }
        super.tick();
    }

    private void pushEnergy() {
        // Transmit power to adjacent block entities if our storage is set up to extract from.
        if (getEnergyStored() > 0 && canExtractEnergy(null)) {
            for (Direction direction : Direction.values()) {
                BlockEntity adjacent = level.getBlockEntity(worldPosition.relative(direction));
                if (adjacent != null) {
                    boolean canContinue = adjacent.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).map(handler -> {
                        if (handler.canReceive()) {
                            int received = handler.receiveEnergy(Math.min(getEnergyStored(), getMaxEnergyTransfer()), false);
                            consumeEnergy(received);
                            return getEnergyStored() > 0;
                        } else {
                            return true;
                        }
                    }).orElse(true);

                    if (!canContinue) {
                        break;
                    }
                }
            }
        }
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
        return layout.hasCapacitorSlot();
    }

    /**
     * Whether the machine has a capacitor installed.
     */
    public boolean isCapacitorInstalled() {
        MachineInventoryLayout layout = getInventoryLayout();
        if (requiresCapacitor() && layout != null) {
            return CapacitorUtil.isCapacitor(getInventory().getStackInSlot(layout.getCapacitorSlot()));
        }

        return false;
    }

    /**
     * Get the capacitor data for the machine.
     */
    public ICapacitorData getCapacitorData() {
        MachineInventoryLayout layout = getInventoryLayout();
        if (requiresCapacitor() && layout != null) {
            return CapacitorUtil.getCapacitorData(getInventory().getStackInSlot(getInventoryLayout().getCapacitorSlot())).orElse(DefaultCapacitorData.NONE);
        }
        return DefaultCapacitorData.NONE;
    }

    // TODO: Hook inventory slot changes and rescan for a capacitor rather than fetching from the slot each time.

    // endregion

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag) {
        pTag.putInt("Energy", storedEnergy);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        storedEnergy = pTag.getInt("Energy");
        super.load(pTag);
    }

    // endregion
}
