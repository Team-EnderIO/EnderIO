package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.FloatSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import net.minecraft.core.RegistryAccess;
import net.neoforged.fml.LogicalSide;

public abstract class FloatSyncSlot implements SyncSlot<Float> {

    private float lastValue;

    @Override
    public ChangeType detectChanges() {
        float currentValue = get();
        var changeType = currentValue != lastValue ? ChangeType.FULL : ChangeType.NONE;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(RegistryAccess registryAccess, ChangeType changeType) {
        return new FloatSlotPayload(get());
    }

    @Override
    public void unpackPayload(SlotPayload payload, LogicalSide side) {
        if (payload instanceof FloatSlotPayload intSlotPayload) {
            set(intSlotPayload.value(), side);
        }
    }
}
