package com.enderio.machines.common.blockentity.sync;

import com.enderio.core.common.sync.EnderDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.machines.common.io.energy.ImmutableMachineEnergyStorage;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Data slot for syncing an instance of {@link IMachineEnergyStorage} using a data slot.
 * @apiNote Sends a {@link ImmutableMachineEnergyStorage} to the receiver.
 */
public class MachineEnergyDataSlot extends EnderDataSlot<IMachineEnergyStorage> {
    public MachineEnergyDataSlot(Supplier<IMachineEnergyStorage> getter, Consumer<IMachineEnergyStorage> setter, SyncMode mode) {
        super(getter, setter, mode);
    }

    @Override
    public CompoundTag toFullNBT() {
        IMachineEnergyStorage storage = getter().get();
        CompoundTag tag = new CompoundTag();
        tag.putInt(MachineNBTKeys.ENERGY_STORED, storage.getEnergyStored());
        tag.putInt(MachineNBTKeys.ENERGY_MAX_STORED, storage.getMaxEnergyStored());
        tag.putInt(MachineNBTKeys.ENERGY_MAX_USE, storage.getMaxEnergyUse());
        return tag;
    }

    @Override
    protected IMachineEnergyStorage fromNBT(CompoundTag nbt) {
        int energy = nbt.getInt(MachineNBTKeys.ENERGY_STORED);
        int maxStored = nbt.getInt(MachineNBTKeys.ENERGY_MAX_STORED);
        int maxUse = nbt.getInt(MachineNBTKeys.ENERGY_MAX_USE);
        return new ImmutableMachineEnergyStorage(energy, maxStored, maxUse);
    }
}
