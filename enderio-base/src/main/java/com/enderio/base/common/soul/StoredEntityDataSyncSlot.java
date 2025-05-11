package com.enderio.base.common.soul;

import com.enderio.base.api.attachment.StoredEntityData;
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
            private StoredEntityData value = StoredEntityData.EMPTY;

            @Override
            public StoredEntityData get() {
                return value;
            }

            @Override
            public void set(StoredEntityData value) {
                this.value = value;
            }
        };
    }

    public static StoredEntityDataSyncSlot simple(Supplier<StoredEntityData> getter, Consumer<StoredEntityData> setter) {
        return new StoredEntityDataSyncSlot() {

            @Override
            public StoredEntityData get() {
                return getter.get();
            }

            @Override
            public void set(StoredEntityData value) {
                setter.accept(value);
            }
        };
    }

    public static StoredEntityDataSyncSlot readOnly(Supplier<StoredEntityData> getter) {
        return new StoredEntityDataSyncSlot() {

            @Override
            public StoredEntityData get() {
                return getter.get();
            }

            @Override
            public void set(StoredEntityData value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    private StoredEntityData lastValue;
    public abstract StoredEntityData get();
    public abstract void set(StoredEntityData value);

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
        if (!currentValue.hasEntity()) {
            return new ListSlotPayload(List.of());
        }

        // TODO: Need to be able to send the entity tag.
        // Honestly feels like a minor rework is required to add custom payloads instead of combining them.
        return new ListSlotPayload(List.of(
            new ResourceLocationSlotPayload(currentValue.entityType().get())
        ));
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        if (payload instanceof ListSlotPayload(List<SlotPayload> contents)) {
            if (contents.isEmpty()) {
                set(StoredEntityData.EMPTY);
            } else {
                if (contents.getFirst() instanceof ResourceLocationSlotPayload(ResourceLocation value)) {
                    set(StoredEntityData.of(value));
                }
            }
        }
    }
}
