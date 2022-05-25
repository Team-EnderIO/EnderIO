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
import com.enderio.machines.common.blockentity.data.sidecontrol.item.MachineInventoryLayout;
import com.enderio.machines.common.blockentity.sync.MachineEnergyDataSlot;
import com.enderio.machines.common.energy.ForgeEnergyWrapper;
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

import java.util.Optional;

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
        return capacityKey.getInt(getCapacitorData().orElse(DefaultCapacitorData.NONE));
    }

    @Override
    public int getMaxEnergyConsumption() {
        return consumptionKey.getInt(getCapacitorData().orElse(DefaultCapacitorData.NONE));
    }

    @Override
    public int getMaxEnergyTransfer() {
        return transferKey.getInt(getCapacitorData().orElse(DefaultCapacitorData.NONE));
    }

    @Override
    public int getEnergyLeakRate() {
        return 0;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyWrapper.getCapability(side).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public boolean shouldAct() {
        // Ignore capacitor state on simple machines.
        if (getTier() == MachineTier.Simple)
            return super.shouldAct();
        return super.shouldAct() && hasCapacitor(); // TODO: Determine if the machine needs a capacitor to run? Maybe leave it to the machine impl to decide?
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

    // Capacitors

    public boolean hasCapacitor() {
        MachineInventoryLayout layout = getInventoryLayout();
        if (layout != null) {
            return layout.getFirst(MachineInventoryLayout.SlotType.CAPACITOR)
                .map(slot -> CapacitorUtil.isCapacitor(getInventory().getStackInSlot(slot)))
                .orElse(false);
        }

        return false;
    }

    public Optional<ICapacitorData> getCapacitorData() {
        MachineInventoryLayout layout = getInventoryLayout();
        if (layout != null) {
            return layout.getFirst(MachineInventoryLayout.SlotType.CAPACITOR)
                .flatMap(slot -> CapacitorUtil.getCapacitorData(getInventory().getStackInSlot(slot)));
        }
        return Optional.empty();
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
