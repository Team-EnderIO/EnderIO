package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import net.minecraft.core.RegistryAccess;
import net.neoforged.fml.LogicalSide;

public abstract class IntSyncSlot implements SyncSlot<Integer> {

    private int lastValue;

    @Override
    public ChangeType detectChanges() {
        int currentValue = get();
        var changeType = currentValue != lastValue ? ChangeType.FULL : ChangeType.NONE;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(RegistryAccess registryAccess, ChangeType changeType) {
        return new IntSlotPayload(get());
    }

    @Override
    public void unpackPayload(SlotPayload payload, LogicalSide side) {
        if (payload instanceof IntSlotPayload intSlotPayload) {
            set(intSlotPayload.value(), side);
        }
    }
}
