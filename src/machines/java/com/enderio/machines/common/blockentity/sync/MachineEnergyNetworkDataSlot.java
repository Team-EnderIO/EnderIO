package com.enderio.machines.common.blockentity.sync;

import com.enderio.core.common.network.slot.NetworkDataSlot;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.machines.common.io.energy.ImmutableMachineEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Data slot for syncing an instance of {@link IMachineEnergyStorage} using a data slot.
 * @apiNote Sends a {@link ImmutableMachineEnergyStorage} to the receiver.
 */
public class MachineEnergyNetworkDataSlot extends NetworkDataSlot<IMachineEnergyStorage> {
    public MachineEnergyNetworkDataSlot(Supplier<IMachineEnergyStorage> getter, Consumer<IMachineEnergyStorage> setter) {
        super(getter, setter);
    }

    @Override
    public Tag serializeValueNBT(IMachineEnergyStorage value) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(MachineNBTKeys.ENERGY_STORED, value.getEnergyStored());
        tag.putInt(MachineNBTKeys.ENERGY_MAX_STORED, value.getMaxEnergyStored());
        tag.putInt(MachineNBTKeys.ENERGY_MAX_USE, value.getMaxEnergyUse());
        return tag;
    }

    @Override
    protected IMachineEnergyStorage valueFromNBT(Tag nbt) {
        if (nbt instanceof CompoundTag compoundTag) {
            int energy = compoundTag.getInt(MachineNBTKeys.ENERGY_STORED);
            int maxStored = compoundTag.getInt(MachineNBTKeys.ENERGY_MAX_STORED);
            int maxUse = compoundTag.getInt(MachineNBTKeys.ENERGY_MAX_USE);
            return new ImmutableMachineEnergyStorage(energy, maxStored, maxUse);
        } else {
            throw new IllegalStateException("Invalid IMachineEnergyStorage tag was passed over the network.");
        }
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf, IMachineEnergyStorage value) {
        buf.writeInt(value.getEnergyStored());
        buf.writeInt(value.getMaxEnergyStored());
        buf.writeInt(value.getMaxEnergyUse());
    }

    @Override
    public IMachineEnergyStorage valueFromBuffer(FriendlyByteBuf buf) {
        try {
            int energy = buf.readInt();
            int maxStored = buf.readInt();
            int maxUse = buf.readInt();
            return new ImmutableMachineEnergyStorage(energy, maxStored, maxUse);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid IMachineEnergyStorage buffer was passed over the network.");
        }
    }

    @Override
    protected int hashCode(IMachineEnergyStorage value) {
        int code = 1;
        code = 31 * code + value.getEnergyStored();
        code = 31 * code + value.getMaxEnergyStored();
        return code;
    }
}
