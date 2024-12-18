package com.enderio.machines.common.network.menu;

import com.enderio.core.common.network.menu.SyncSlot;
import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.ListSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayloadType;
import com.enderio.machines.common.blockentity.sync.EnergySyncData;
import net.minecraft.core.RegistryAccess;
import net.neoforged.fml.LogicalSide;

import java.util.List;

public abstract class EnergySyncSlot implements SyncSlot {

    private EnergySyncData lastValue;

    public abstract EnergySyncData get();
    public abstract void set(EnergySyncData value);

    @Override
    public ChangeType detectChanges() {
        var currentValue = get();
        var changeType = !currentValue.equals(lastValue) ? ChangeType.FULL : ChangeType.NONE;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(RegistryAccess registryAccess, ChangeType changeType) {
        var value = get();
        return new ListSlotPayload(List.of(
            new IntSlotPayload(value.energyStored()),
            new IntSlotPayload(value.maxEnergyStored()),
            new IntSlotPayload(value.maxEnergyUse())
        ));
    }

    @Override
    public void unpackPayload(SlotPayload payload) {
        if (payload instanceof ListSlotPayload listSlotPayload && listSlotPayload.contents().size() == 3) {
            for (int i = 0; i < 3; i++) {
                if (listSlotPayload.contents().get(i).type() != SlotPayloadType.INT) {
                    return;
                }
            }

            set(new EnergySyncData(
                    ((IntSlotPayload)listSlotPayload.contents().get(0)).value(),
                    ((IntSlotPayload)listSlotPayload.contents().get(1)).value(),
                    ((IntSlotPayload)listSlotPayload.contents().get(2)).value()));
        }
    }
}
