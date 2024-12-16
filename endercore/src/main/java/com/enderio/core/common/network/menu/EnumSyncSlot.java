package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import net.minecraft.core.RegistryAccess;
import net.neoforged.fml.LogicalSide;

public abstract class EnumSyncSlot<T extends Enum<T>> implements SyncSlot<T> {
    private final Class<T> enumClass;
    private T lastValue;

    public EnumSyncSlot(Class<T> enumClass) {
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
    public SlotPayload createPayload(RegistryAccess registryAccess, ChangeType changeType) {
        return new IntSlotPayload(get().ordinal());
    }

    @Override
    public void unpackPayload(SlotPayload payload, LogicalSide side) {
        if (payload instanceof IntSlotPayload intSlotPayload) {
            var values = enumClass.getEnumConstants();
            if (intSlotPayload.value() >= 0 && intSlotPayload.value() < values.length) {
                set(values[intSlotPayload.value()], side);
            }
        }
    }
}
