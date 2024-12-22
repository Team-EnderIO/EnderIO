package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.level.Level;

public abstract class EnumSyncSlot<T extends Enum<T>> implements SyncSlot {

    public static <T extends Enum<T>> EnumSyncSlot<T> standalone(Class<T> enumClass) {
        return new EnumSyncSlot<>(enumClass) {
            private T value = enumClass.getEnumConstants()[0];

            @Override
            public T get() {
                return value;
            }

            @Override
            public void set(T value) {
                this.value = value;
            }
        };
    }

    public static <T extends Enum<T>> EnumSyncSlot<T> simple(Class<T> enumClass, Supplier<T> getter,
            Consumer<T> setter) {
        return new EnumSyncSlot<>(enumClass) {

            @Override
            public T get() {
                return getter.get();
            }

            @Override
            public void set(T value) {
                setter.accept(value);
            }
        };
    }

    public static <T extends Enum<T>> EnumSyncSlot<T> readOnly(Class<T> enumClass, Supplier<T> getter) {
        return new EnumSyncSlot<>(enumClass) {

            @Override
            public T get() {
                return getter.get();
            }

            @Override
            public void set(T value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    private final Class<T> enumClass;
    private T lastValue;

    public abstract T get();

    public abstract void set(T value);

    public EnumSyncSlot(Class<T> enumClass) {
        if (enumClass.getEnumConstants().length == 0) {
            throw new IllegalArgumentException("Enum class must have at least one enum.");
        }

        this.enumClass = enumClass;
    }

    @Override
    public ChangeType detectChanges() {
        T currentValue = get();
        var changeType = currentValue != lastValue ? ChangeType.FULL : ChangeType.NONE;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(Level level, ChangeType changeType) {
        return new IntSlotPayload(get().ordinal());
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        if (payload instanceof IntSlotPayload intSlotPayload) {
            var values = enumClass.getEnumConstants();
            if (intSlotPayload.value() >= 0 && intSlotPayload.value() < values.length) {
                set(values[intSlotPayload.value()]);
            }
        }
    }
}
