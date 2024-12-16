package com.enderio.machines.common.network.menu;

import com.enderio.core.common.network.menu.SyncSlot;
import com.enderio.core.common.network.menu.payload.IntSlotPayload;
import com.enderio.core.common.network.menu.payload.ListSlotPayload;
import com.enderio.core.common.network.menu.payload.LongSlotPayload;
import com.enderio.core.common.network.menu.payload.PairSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayloadType;
import com.enderio.machines.common.blockentity.sync.EnergySyncData;
import com.enderio.machines.common.blockentity.sync.LargeEnergyData;
import net.minecraft.core.RegistryAccess;

import java.util.List;
import java.util.Objects;

public abstract class LargeEnergySyncSlot implements SyncSlot {

    private LargeEnergyData lastValue;

    public abstract LargeEnergyData get();
    public abstract void set(LargeEnergyData value);

    @Override
    public ChangeType detectChanges() {
        var currentValue = get();
        var changeType = !Objects.equals(currentValue, lastValue) ? ChangeType.FULL : ChangeType.NONE;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload getPayload(RegistryAccess registryAccess, ChangeType changeType) {
        var value = get();
        return new PairSlotPayload(
            new LongSlotPayload(value.energyStored()),
            new LongSlotPayload(value.maxEnergyStored())
        );
    }

    @Override
    public void acceptPayload(SlotPayload payload) {
        if (payload instanceof PairSlotPayload pair) {
            if (pair.left().type() != SlotPayloadType.LONG || pair.right().type() != SlotPayloadType.LONG) {
                return;
            }

            set(new LargeEnergyData(
                ((LongSlotPayload)pair.left()).value(),
                ((LongSlotPayload)pair.right()).value()
            ));
        }
    }
}
