package com.enderio.enderio.foundation.network.menu_sync;

import com.enderio.core.common.network.menu.SyncSlot;
import com.enderio.core.common.network.menu.payload.LongSlotPayload;
import com.enderio.core.common.network.menu.payload.PairSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayloadType;
import com.enderio.enderio.foundation.energy.EnergyStorageInfo;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class EnergyStorageSyncSlot implements SyncSlot {

    public static EnergyStorageSyncSlot standalone() {
        return new EnergyStorageSyncSlot() {
            private EnergyStorageInfo value = new EnergyStorageInfo(0, 0);

            @Override
            public EnergyStorageInfo get() {
                return value;
            }

            @Override
            public void set(EnergyStorageInfo value) {
                this.value = value;
            }
        };
    }

    public static EnergyStorageSyncSlot simple(Supplier<EnergyStorageInfo> getter, Consumer<EnergyStorageInfo> setter) {
        return new EnergyStorageSyncSlot() {

            @Override
            public EnergyStorageInfo get() {
                return getter.get();
            }

            @Override
            public void set(EnergyStorageInfo value) {
                setter.accept(value);
            }
        };
    }

    public static EnergyStorageSyncSlot readOnly(Supplier<EnergyStorageInfo> getter) {
        return new EnergyStorageSyncSlot() {

            @Override
            public EnergyStorageInfo get() {
                return getter.get();
            }

            @Override
            public void set(EnergyStorageInfo value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    private EnergyStorageInfo lastValue;

    public abstract EnergyStorageInfo get();

    public abstract void set(EnergyStorageInfo value);

    @Override
    public ChangeType detectChanges() {
        var currentValue = get();
        if (Objects.equals(lastValue, currentValue)) {
            return ChangeType.NONE;
        }

        var changeType = lastValue == null || currentValue.capacity() != lastValue.capacity()
                ? ChangeType.FULL
                : ChangeType.PARTIAL;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(Level level, ChangeType changeType) {
        var value = get();

        if (changeType == ChangeType.PARTIAL) {
            return new LongSlotPayload(value.energy());
        }

        return new PairSlotPayload(new LongSlotPayload(value.energy()),
                new LongSlotPayload(value.capacity()));
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        if (payload instanceof LongSlotPayload longSlotPayload) {
            set(get().withEnergy(longSlotPayload.value()));
        } else if (payload instanceof PairSlotPayload pairSlotPayload) {
            if (pairSlotPayload.left().type() != SlotPayloadType.LONG
                    || pairSlotPayload.right().type() != SlotPayloadType.LONG) {
                return;
            }

            set(new EnergyStorageInfo(((LongSlotPayload) pairSlotPayload.left()).value(),
                    ((LongSlotPayload) pairSlotPayload.right()).value()));
        }
    }
}
