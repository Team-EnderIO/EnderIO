package com.enderio.core.common.storage;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;

public interface ResourceStorage<T extends Resource> extends ResourceHandler<T> {
    void set(int index, T resource, int amount);
    void set(int index, T resource, long amount);
}
