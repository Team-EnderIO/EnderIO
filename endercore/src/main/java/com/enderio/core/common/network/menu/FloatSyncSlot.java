package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.FloatSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.level.Level;

public abstract class FloatSyncSlot implements SyncSlot {

    public static FloatSyncSlot standalone() {
        return new FloatSyncSlot() {
            private float value;

            @Override
            public float get() {
                return value;
            }

            @Override
            public void set(float value) {
                this.value = value;
            }
        };
    }

    public static FloatSyncSlot simple(Supplier<Float> getter, Consumer<Float> setter) {
        return new FloatSyncSlot() {

            @Override
            public float get() {
                return getter.get();
            }

            @Override
            public void set(float value) {
                setter.accept(value);
            }
        };
    }

    public static FloatSyncSlot readOnly(Supplier<Float> getter) {
        return new FloatSyncSlot() {

            @Override
            public float get() {
                return getter.get();
            }

            @Override
            public void set(float value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    private float lastValue;

    public abstract float get();

    public abstract void set(float value);

    @Override
    public ChangeType detectChanges() {
        float currentValue = get();
        var changeType = currentValue != lastValue ? ChangeType.FULL : ChangeType.NONE;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(Level level, ChangeType changeType) {
        return new FloatSlotPayload(get());
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        if (payload instanceof FloatSlotPayload intSlotPayload) {
            set(intSlotPayload.value());
        }
    }
}
