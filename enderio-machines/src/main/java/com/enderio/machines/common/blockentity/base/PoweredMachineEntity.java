package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capability.ICapacitorData;
import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.base.EnderIO;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.base.common.capacitor.CapacitorUtil;
import com.enderio.api.UseOnly;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemSlotLayout;
import com.enderio.api.energy.EnergyCapacityPair;
import com.enderio.machines.common.blockentity.sync.MachineEnergyDataSlot;
import com.enderio.machines.common.energy.EnergyTransferMode;
import com.enderio.machines.common.energy.MachineEnergyStorage;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A machine that stores power using {@link MachineEnergyStorage}.
 */
public abstract class PoweredMachineEntity extends MachineBlockEntity {
    protected MachineEnergyStorage energyStorage;

    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> this.energyStorage);

    protected final CapacitorKey capacityKey, transferKey, consumptionKey;

    // TODO: Cache capacitor data rather than constantly querying an optional?

    @UseOnly(LogicalSide.CLIENT) private EnergyCapacityPair clientEnergy;

    public PoweredMachineEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey consumptionKey, EnergyTransferMode transferMode, BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);

        this.capacityKey = capacityKey;
        this.transferKey = transferKey;
        this.consumptionKey = consumptionKey;

        energyStorage = createEnergyStorage(transferMode);

        // Add energy storage dataslot. It will only ever be synced to the client.
        addDataSlot(new MachineEnergyDataSlot(energyStorage, vec -> clientEnergy = vec, SyncMode.GUI));
    }

    // region Energy

    // TODO: Energy leakage and efficiency multipliers

    /**
     * Override this to define your energy storage medium.
     */

    protected MachineEnergyStorage createEnergyStorage(EnergyTransferMode transferMode) {
        return new MachineEnergyStorage(this::getCapacitorData, capacityKey, transferKey, consumptionKey, transferMode) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    // Helper methods for gui:
    @UseOnly(LogicalSide.CLIENT)
    public EnergyCapacityPair getGuiEnergy() {
        if (level.isClientSide) {
            return clientEnergy;
        }
        EnderIO.LOGGER.warn("getGuiEnergy called on server!");
        return new EnergyCapacityPair(energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored());
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyCap.cast(); // TODO: FUTURE: Sided access stuff.
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void tick() {
        pushEnergy();
        super.tick();
    }

    // endregion

    // region Energy and Capacitors

    public boolean hasCapacitor() {
        return getSlotLayout()
            .flatMap(layout -> layout
                .getFirst(ItemSlotLayout.SlotType.CAPACITOR))
            .map(slot -> CapacitorUtil.isCapacitor(getItemHandler().getStackInSlot(slot)))
            .orElse(false);
    }

    public Optional<ICapacitorData> getCapacitorData() {
        return getSlotLayout().flatMap(layout -> layout
            .getFirst(ItemSlotLayout.SlotType.CAPACITOR)
            .flatMap(slot -> CapacitorUtil.getCapacitorData(getItemHandler().getStackInSlot(slot))));
    }

    private void pushEnergy() {
        // Transmit power to adjacent block entities if our storage is set up to extract from.
        AtomicInteger stored = new AtomicInteger(energyStorage.getEnergyStored());
        if (stored.get() > 0 && energyStorage.canExtract()) { // TODO: Is using canExtract correct? Or should we handle this some other way.
            for (Direction direction : Direction.values()) {
                BlockEntity adjacent = level.getBlockEntity(worldPosition.relative(direction));
                if (adjacent != null) {
                    boolean canContinue = adjacent.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).map(handler -> {
                        if (handler.canReceive()) {
                            int received = handler.receiveEnergy(Math.min(stored.get(), energyStorage.getMaxEnergyTransfer()), false);
                            stored.addAndGet(-received);
                            energyStorage.consumeEnergy(received);
                            setChanged();
                            return stored.get() > 0;
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
}
