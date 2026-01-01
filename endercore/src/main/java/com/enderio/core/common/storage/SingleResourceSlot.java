package com.enderio.core.common.storage;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;

public record SingleResourceSlot<T extends Resource>(int index) {
    public int index() {
        return index;
    }

    public T getResource(ResourceHandler<T> handler) {
        return handler.getResource(index);
    }

    public int getAmountAsInt(ResourceHandler<T> handler) {
        return handler.getAmountAsInt(index);
    }

    public long getAmountAsLong(ResourceHandler<T> handler) {
        return handler.getAmountAsLong(index);
    }

    public int getCapacityAsInt(ResourceHandler<T> handler, T resource) {
        return handler.getCapacityAsInt(index, resource);
    }

    public long getCapacityAsLong(ResourceHandler<T> handler, T resource) {
        return handler.getCapacityAsLong(index, resource);
    }

    public void set(ResourceStorage<T> storage, T resource, int amount) {
        storage.set(resource, amount);
    }

    public void set(ResourceStorage<T> storage, T resource, long amount) {
        storage.set(resource, amount);
    }
}
