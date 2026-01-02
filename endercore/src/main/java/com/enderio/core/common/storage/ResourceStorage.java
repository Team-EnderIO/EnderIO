package com.enderio.core.common.storage;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jetbrains.annotations.ApiStatus;

public interface ResourceStorage<T extends Resource> extends ResourceHandler<T> {
    void set(int index, T resource, int amount);

    // TODO: Should we add NonExtendable methods that make using the inventory layout easier?
    //       Potentially even instead of having them in the layout, we have them on here and have a layout() method expected for a ResourceStorage?
}

