package com.enderio.base.common.soul;

import com.enderio.base.api.soul.Soul;
import com.enderio.core.common.network.menu.SyncSlot;
import com.enderio.core.common.network.menu.payload.ListSlotPayload;
import com.enderio.core.common.network.menu.payload.ResourceLocationSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class StoredEntityDataSyncSlot implements SyncSlot {

    public static StoredEntityDataSyncSlot standalone() {
        return new StoredEntityDataSyncSlot() {
            private Soul value = Soul.EMPTY;

            @Override
            public Soul get() {
                return value;
            }

            @Override
            public void set(Soul value) {
                this.value = value;
            }
        };
    }

    public static StoredEntityDataSyncSlot simple(Supplier<Soul> getter, Consumer<Soul> setter) {
        return new StoredEntityDataSyncSlot() {

            @Override
            public Soul get() {
                return getter.get();
            }

            @Override
            public void set(Soul value) {
                setter.accept(value);
            }
        };
    }

    public static StoredEntityDataSyncSlot readOnly(Supplier<Soul> getter) {
        return new StoredEntityDataSyncSlot() {

            @Override
            public Soul get() {
                return getter.get();
            }

            @Override
            public void set(Soul value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    private Soul lastValue;
    public abstract Soul get();
    public abstract void set(Soul value);

    @Override
    public ChangeType detectChanges() {
        var currentValue = get();
        if (Objects.equals(currentValue, lastValue)) {
            return ChangeType.NONE;
        }

        return ChangeType.FULL;
    }

    @Override
    public SlotPayload createPayload(Level level, ChangeType changeType) {
        var currentValue = get();
        if (currentValue.isEmpty()) {
            return new ListSlotPayload(List.of());
        }

        // TODO: Need to be able to send the entity tag.
        // Honestly feels like a minor rework is required to add custom payloads instead of combining them.
        return new ListSlotPayload(List.of(
            new ResourceLocationSlotPayload(currentValue.entityTypeId())
        ));
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        if (payload instanceof ListSlotPayload(List<SlotPayload> contents)) {
            if (contents.isEmpty()) {
                set(Soul.EMPTY);
            } else {
                if (contents.getFirst() instanceof ResourceLocationSlotPayload(ResourceLocation value)) {
                    set(Soul.of(value));
                }
            }
        }
    }
}
