package com.enderio.machines.common.blockentity.sync;

import com.enderio.api.energy.EnergyCapacityPair;
import com.enderio.base.common.blockentity.sync.EnderDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MachineEnergyDataSlot extends EnderDataSlot<EnergyCapacityPair> {
    public MachineEnergyDataSlot(Supplier<Integer> getEnergyStored, Supplier<Integer> getEnergyCapacity, Consumer<EnergyCapacityPair> clientConsumer, SyncMode syncMode) {
        // We dont set a setter here as energy should *never* be client -> server synced.
        super(() -> new EnergyCapacityPair(getEnergyStored.get(), getEnergyCapacity.get()), clientConsumer, syncMode);
    }

    @Override
    public CompoundTag toFullNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Energy", getter().get().energy());
        tag.putInt("Capacity", getter().get().capacity());
        return tag;
    }

    @Override
    protected EnergyCapacityPair fromNBT(CompoundTag nbt) {
        return new EnergyCapacityPair(nbt.getInt("Energy"), nbt.getInt("Capacity"));
    }
}
