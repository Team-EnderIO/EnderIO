package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.SlotPayload;
import net.minecraft.core.RegistryAccess;

/**
 * Inspired by Mekanism's SyncableData system.
 */
public interface SyncSlot {
    ChangeType detectChanges();

    SlotPayload createPayload(RegistryAccess registryAccess, ChangeType changeType);

    void unpackPayload(SlotPayload payload);

    enum ChangeType {
        NONE, PARTIAL, FULL
    }
}
