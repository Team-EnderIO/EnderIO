package com.enderio.machines.common.blockentity.sync;

import com.enderio.base.common.blockentity.sync.EnderDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.base.common.util.Vector2i;
import com.enderio.machines.common.energy.MachineEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MachineEnergyDataSlot extends EnderDataSlot<Vector2i> {
    public MachineEnergyDataSlot(MachineEnergyStorage storage, Consumer<Vector2i> clientConsumer, SyncMode syncMode) {
        // We dont set a setter here as energy should *never* be client -> server synced.
        super(() -> new Vector2i(storage.getEnergyStored(), storage.getMaxEnergyStored()), clientConsumer, syncMode);
    }

    @Override
    public CompoundTag toFullNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Energy", getter().get().getX());
        tag.putInt("Capacity", getter().get().getY());
        return tag;
    }

    @Override
    protected Vector2i fromNBT(CompoundTag nbt) {
        return new Vector2i(nbt.getInt("Energy"), nbt.getInt("Capacity"));
    }
}
