package com.enderio.core.common.sync;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.temporal.TemporalField;

public class VersionedDataSlot<T> extends EnderDataSlot<T> {
    private long timestamp = 0;

    private final EnderDataSlot<T> slot;
    private int lastValueHash = 0;

    public VersionedDataSlot(EnderDataSlot<T> slot) {
        super(slot.getter(), slot.setter(), slot.getSyncMode());
        this.slot = slot;
    }

    @Override
    public CompoundTag toFullNBT() {
        var value = getter().get();
        if (value.hashCode() != lastValueHash) {
            timestamp = Instant.now().toEpochMilli();
            lastValueHash = value.hashCode();
        }

        CompoundTag tag = new CompoundTag();
        tag.putLong("Timestamp", timestamp);
        tag.put("Value", slot.toFullNBT());
        return tag;
    }

    @Nullable
    @Override
    protected T fromNBT(CompoundTag nbt) {
        var timestamp = nbt.getLong("Timestamp");
        if (timestamp > this.timestamp) {
            this.timestamp = timestamp;
            return slot.fromNBT(nbt.getCompound("Value"));
        }

        return null;
    }
}
