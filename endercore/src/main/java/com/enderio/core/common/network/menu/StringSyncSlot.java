package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.NullSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import com.enderio.core.common.network.menu.payload.StringSlotPayload;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class StringSyncSlot implements SyncSlot {

    public static StringSyncSlot standalone() {
        return new StringSyncSlot() {
            @Nullable
            private String value;

            @Override
            @Nullable
            public String get() {
                return value;
            }

            @Override
            public void set(@Nullable String value) {
                this.value = value;
            }
        };
    }

    public static StringSyncSlot simple(Supplier<String> getter, Consumer<String> setter) {
        return new StringSyncSlot() {

            @Override
            @Nullable
            public String get() {
                return getter.get();
            }

            @Override
            public void set(@Nullable String value) {
                setter.accept(value);
            }
        };
    }

    public static StringSyncSlot readOnly(Supplier<String> getter) {
        return new StringSyncSlot() {

            @Override
            @Nullable
            public String get() {
                return getter.get();
            }

            @Override
            public void set(@Nullable String value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    @Nullable
    private String lastValue;

    @Nullable
    public abstract String get();

    public abstract void set(@Nullable String value);

    @Override
    public ChangeType detectChanges() {
        String currentValue = get();
        var changeType = Objects.equals(currentValue, lastValue) ? ChangeType.NONE : ChangeType.FULL;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(Level level, ChangeType changeType) {
        var value = get();
        if (value == null) {
            return new NullSlotPayload();
        }

        return new StringSlotPayload(value);
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        if (payload instanceof StringSlotPayload stringSlotPayload) {
            set(stringSlotPayload.value());
        } else if (payload instanceof NullSlotPayload) {
            set(null);
        }
    }
}
