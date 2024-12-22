package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.level.Level;

public abstract class IntSyncSlot implements SyncSlot {

    public static IntSyncSlot standalone() {
        return new IntSyncSlot() {
            private int value;

            @Override
            public int get() {
                return value;
            }

            @Override
            public void set(int value) {
                this.value = value;
            }
        };
    }

    public static IntSyncSlot simple(Supplier<Integer> getter, Consumer<Integer> setter) {
        return new IntSyncSlot() {

            @Override
            public int get() {
                return getter.get();
            }

            @Override
            public void set(int value) {
                setter.accept(value);
            }
        };
    }

    public static IntSyncSlot readOnly(Supplier<Integer> getter) {
        return new IntSyncSlot() {

            @Override
            public int get() {
                return getter.get();
            }

            @Override
            public void set(int value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    private int lastValue;

    public abstract int get();

    public abstract void set(int value);

    @Override
    public ChangeType detectChanges() {
        int currentValue = get();
        var changeType = currentValue != lastValue ? ChangeType.FULL : ChangeType.NONE;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(Level level, ChangeType changeType) {
        return new IntSlotPayload(get());
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        if (payload instanceof IntSlotPayload intSlotPayload) {
            set(intSlotPayload.value());
        }
    }
}
