package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.FloatSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import net.minecraft.core.RegistryAccess;

public abstract class FloatSyncSlot implements SyncSlot {

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
    public SlotPayload getPayload(RegistryAccess registryAccess, ChangeType changeType) {
        return new FloatSlotPayload(get());
    }

    @Override
    public void acceptPayload(SlotPayload payload) {
        if (payload instanceof FloatSlotPayload intSlotPayload) {
            set(intSlotPayload.value());
        }
    }
}
