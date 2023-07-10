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
public class LargeMachineEnergyDataSlot extends NetworkDataSlot<ILargeMachineEnergyStorage> {
    public LargeMachineEnergyDataSlot(Supplier<ILargeMachineEnergyStorage> getter, Consumer<ILargeMachineEnergyStorage> setter) {
        super(getter, setter);
    }

    @Override
    public Tag serializeValueNBT(ILargeMachineEnergyStorage value) {
        CompoundTag tag = new CompoundTag();
        tag.putLong(MachineNBTKeys.ENERGY_STORED, value.getLargeEnergyStored());
        tag.putLong(MachineNBTKeys.ENERGY_MAX_STORED, value.getLargeMaxEnergyStored());
        return tag;
    }

    @Override
    protected ILargeMachineEnergyStorage valueFromNBT(Tag nbt) {
        if (nbt instanceof CompoundTag compoundTag) {
            long energy = compoundTag.getLong(MachineNBTKeys.ENERGY_STORED);
            long maxStored = compoundTag.getLong(MachineNBTKeys.ENERGY_MAX_STORED);
            return new LargeImmutableMachineEnergyStorage(energy, maxStored);
        } else {
            throw new IllegalStateException("Invalid LargeMachineEnergy was passed over the network.");
        }
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf, ILargeMachineEnergyStorage value) {
        buf.writeLong(value.getLargeEnergyStored());
        buf.writeLong(value.getLargeMaxEnergyStored());
    }

    @Override
    public ILargeMachineEnergyStorage valueFromBuffer(FriendlyByteBuf buf) {
        try {
            long energy = buf.readLong();
            long maxStored = buf.readLong();
            return new LargeImmutableMachineEnergyStorage(energy, maxStored);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid LargeMachineEnergy buffer was passed over the network.");
        }
    }

    @Override
    protected int hashCode(ILargeMachineEnergyStorage value) {
        int code = 1;
        code = 31 * code + Long.hashCode(value.getLargeEnergyStored());
        code = 31 * code + Long.hashCode(value.getLargeMaxEnergyStored());
        return code;
    }
}
