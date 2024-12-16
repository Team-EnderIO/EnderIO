package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import net.minecraft.core.RegistryAccess;

public abstract class EnumSyncSlot<T extends Enum<T>> implements SyncSlot {
    private final Class<T> enumClass;
    private T lastValue;

    public EnumSyncSlot(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    public abstract T get();
    public abstract void set(T value);

    @Override
    public ChangeType detectChanges() {
        T currentValue = get();
        var changeType = currentValue != lastValue ? ChangeType.FULL : ChangeType.NONE;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload getPayload(RegistryAccess registryAccess, ChangeType changeType) {
        return new IntSlotPayload(get().ordinal());
    }

    @Override
    public void acceptPayload(SlotPayload payload) {
        if (payload instanceof IntSlotPayload intSlotPayload) {
            var values = enumClass.getEnumConstants();
            if (intSlotPayload.value() >= 0 && intSlotPayload.value() < values.length) {
                set(values[intSlotPayload.value()]);
            }
        }
    }
}
