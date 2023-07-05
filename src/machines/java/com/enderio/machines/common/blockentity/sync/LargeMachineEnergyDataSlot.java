package com.enderio.machines.common.blockentity.sync;

import com.enderio.core.common.sync.EnderDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.io.energy.ILargeMachineEnergyStorage;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.machines.common.io.energy.ImmutableMachineEnergyStorage;
import com.enderio.machines.common.io.energy.LargeImmutableMachineEnergyStorage;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Data slot for syncing an instance of {@link IMachineEnergyStorage} using a data slot.
 * @apiNote Sends a {@link ImmutableMachineEnergyStorage} to the receiver.
 */
public class LargeMachineEnergyDataSlot extends EnderDataSlot<IMachineEnergyStorage> {
    public LargeMachineEnergyDataSlot(Supplier<IMachineEnergyStorage> getter, Consumer<IMachineEnergyStorage> setter, SyncMode mode) {
        super(getter, setter, mode);
    }

    @Override
    public CompoundTag toFullNBT() {
        ILargeMachineEnergyStorage storage = (ILargeMachineEnergyStorage) getter().get();
        CompoundTag tag = new CompoundTag();
        tag.putLong(MachineNBTKeys.ENERGY_STORED, storage.getLargeEnergyStored());
        tag.putLong(MachineNBTKeys.ENERGY_MAX_STORED, storage.getLargeMaxEnergyStored());
        return tag;
    }

    @Override
    protected IMachineEnergyStorage fromNBT(CompoundTag nbt) {
        long energy = nbt.getLong(MachineNBTKeys.ENERGY_STORED);
        long maxStored = nbt.getLong(MachineNBTKeys.ENERGY_MAX_STORED);
        return new LargeImmutableMachineEnergyStorage(energy, maxStored);
    }
}
