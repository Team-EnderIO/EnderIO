package com.enderio.core.common.storage.slot;

import com.enderio.core.common.storage.EnderResourceHandler;
import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jetbrains.annotations.ApiStatus;

@FunctionalInterface
public interface ResourceSlotId<T extends Resource> {
    int index(ResourceStorageLayout<T, ?> layout);

    @ApiStatus.NonExtendable
    default int index(EnderResourceHandler<T> storage) {
        return index(storage.layout());
    }
}
