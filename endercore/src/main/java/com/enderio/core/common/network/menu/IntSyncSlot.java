package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import net.minecraft.core.RegistryAccess;

public abstract class IntSyncSlot implements SyncSlot {

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
    public SlotPayload getPayload(RegistryAccess registryAccess, ChangeType changeType) {
        return new IntSlotPayload(get());
    }

    @Override
    public void acceptPayload(SlotPayload payload) {
        if (payload instanceof IntSlotPayload intSlotPayload) {
            set(intSlotPayload.value());
        }
    }
}
