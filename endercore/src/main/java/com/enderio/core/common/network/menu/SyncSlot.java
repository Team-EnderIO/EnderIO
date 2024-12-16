package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.SlotPayload;
import net.minecraft.core.RegistryAccess;
import net.neoforged.fml.LogicalSide;

/**
 * Inspired by Mekanism's SyncableData system.
 */
public interface SyncSlot<T> {
    ChangeType detectChanges();

    SlotPayload createPayload(RegistryAccess registryAccess, ChangeType changeType);
    void unpackPayload(SlotPayload payload, LogicalSide side);

    T get();
    void set(T value, LogicalSide side);

    enum ChangeType {
        NONE,
        PARTIAL,
        FULL
    }
}
