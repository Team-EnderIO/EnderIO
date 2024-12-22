package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.BoolSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.level.Level;

public abstract class BoolSyncSlot implements SyncSlot {

    public static BoolSyncSlot standalone() {
        return new BoolSyncSlot() {
            private boolean value;

            @Override
            public boolean get() {
                return value;
            }

            @Override
            public void set(boolean value) {
                this.value = value;
            }
        };
    }

    public static BoolSyncSlot simple(Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return new BoolSyncSlot() {

            @Override
            public boolean get() {
                return getter.get();
            }

            @Override
            public void set(boolean value) {
                setter.accept(value);
            }
        };
    }

    public static BoolSyncSlot readOnly(Supplier<Boolean> getter) {
        return new BoolSyncSlot() {

            @Override
            public boolean get() {
                return getter.get();
            }

            @Override
            public void set(boolean value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    private boolean lastValue;

    public abstract boolean get();

    public abstract void set(boolean value);

    @Override
    public ChangeType detectChanges() {
        boolean currentValue = get();
        var changeType = currentValue != lastValue ? ChangeType.FULL : ChangeType.NONE;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(Level level, ChangeType changeType) {
        return new BoolSlotPayload(get());
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        if (payload instanceof BoolSlotPayload boolSlotPayload) {
            set(boolSlotPayload.value());
        }
    }
}
