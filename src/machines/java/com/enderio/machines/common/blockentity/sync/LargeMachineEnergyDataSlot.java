package com.enderio.machines.common.blockentity.sync;

import com.enderio.core.common.network.slot.NetworkDataSlot;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.io.energy.ILargeMachineEnergyStorage;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.machines.common.io.energy.ImmutableMachineEnergyStorage;
import com.enderio.machines.common.io.energy.LargeImmutableMachineEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Data slot for syncing an instance of {@link IMachineEnergyStorage} using a data slot.
 * @apiNote Sends a {@link ImmutableMachineEnergyStorage} to the receiver.
 */
public class LargeMachineEnergyDataSlot extends NetworkDataSlot<IMachineEnergyStorage> {
    public LargeMachineEnergyDataSlot(Supplier<IMachineEnergyStorage> getter, Consumer<IMachineEnergyStorage> setter) {
        super(getter, setter);
    }

    @Override
    public Tag serializeValueNBT(IMachineEnergyStorage value) {
        ILargeMachineEnergyStorage storage = (ILargeMachineEnergyStorage) getter.get(); //why the getter?
        CompoundTag tag = new CompoundTag();
        tag.putLong(MachineNBTKeys.ENERGY_STORED, storage.getLargeEnergyStored());
        tag.putLong(MachineNBTKeys.ENERGY_MAX_STORED, storage.getLargeMaxEnergyStored());
        return tag;
    }

    @Override
    protected IMachineEnergyStorage valueFromNBT(Tag nbt) {
        if (nbt instanceof CompoundTag compoundTag) {
            long energy = compoundTag.getLong(MachineNBTKeys.ENERGY_STORED);
            long maxStored = compoundTag.getLong(MachineNBTKeys.ENERGY_MAX_STORED);
            return new LargeImmutableMachineEnergyStorage(energy, maxStored);
        } else {
            throw new IllegalStateException("Invalid LargeMachineEnergy was passed over the network.");
        }
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf, IMachineEnergyStorage value) {
        ILargeMachineEnergyStorage storage = (ILargeMachineEnergyStorage) getter.get(); //why the getter?
        buf.writeLong(storage.getLargeEnergyStored());
        buf.writeLong(storage.getLargeMaxEnergyStored());
    }

    @Override
    public IMachineEnergyStorage valueFromBuffer(FriendlyByteBuf buf) {
        try {
            long energy = buf.readLong();
            long maxStored = buf.readLong();
            return new LargeImmutableMachineEnergyStorage(energy, maxStored);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid LargeMachineEnergy buffer was passed over the network.");
        }
    }

    @Override
    protected int hashCode(IMachineEnergyStorage value) {
        var largeStorage = (ILargeMachineEnergyStorage)value; //why the cast?
        int code = 1;
        code = 31 * code + Long.hashCode(largeStorage.getLargeEnergyStored());
        code = 31 * code + Long.hashCode(largeStorage.getLargeMaxEnergyStored());
        return code;
    }
}
