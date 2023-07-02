package com.enderio.core.common.sync;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class EnderDataSlot<T> {
    private final Supplier<T> getter;
    private final Consumer<T> setter;

    private final SyncMode syncMode;

    private int lastSentHash = 0;
    private boolean hasDataChangedThisTick = false;

    public EnderDataSlot(Supplier<T> getter, Consumer<T> setter, SyncMode mode) {
        this.getter = getter;
        this.setter = setter;
        this.syncMode = mode;
    }

    public SyncMode getSyncMode() {
        return syncMode;
    }

    protected Supplier<T> getter() {
        return getter;
    }

    protected Consumer<T> setter() {
        return setter;
    }

    public void clearHasChangedFlag() {
        hasDataChangedThisTick = false;
    }

    public Optional<CompoundTag> toOptionalNBT() {
        int currentHash = getter().get().hashCode();
        if (currentHash != lastSentHash) {
            lastSentHash = currentHash;
            hasDataChangedThisTick = true;
        }

        if (hasDataChangedThisTick) {
            return Optional.of(toFullNBT());
        }
        return Optional.empty();
    }

    /**
     * Make sure to always retain a valid state, even when this method throws an Exception and only throw an Exception if invalid data is sent, as Clients can have full control over incoming data
     * @param tag
     */
    public void handleNBT(CompoundTag tag) {
        var value = fromNBT(tag);
        if (value != null) {
            setter.accept(value);
        }
    }

    public abstract CompoundTag toFullNBT();

    protected abstract T fromNBT(CompoundTag nbt);
}
