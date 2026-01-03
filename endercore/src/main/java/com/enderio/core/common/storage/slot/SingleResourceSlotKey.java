package com.enderio.core.common.storage.slot;

import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import net.neoforged.neoforge.transfer.resource.Resource;

public final class SingleResourceSlotKey<T extends Resource> implements ResourceSlotKey, ResourceSlotId<T> {
    @Override
    public int index(ResourceStorageLayout<T, ?> layout) {
        return layout.indexOf(this);
    }
}
