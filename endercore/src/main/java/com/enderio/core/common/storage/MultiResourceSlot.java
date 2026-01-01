package com.enderio.core.common.storage;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public record MultiResourceSlot<T extends Resource>(List<SingleResourceSlot<T>> slots) {
    public int size() {
        return this.slots.size();
    }

    public SingleResourceSlot<T> get(int index) {
        return this.slots.get(index);
    }

    public T getResource(ResourceHandler<T> handler, int index) {
        return get(index).getResource(handler);
    }

    public int getAmountAsInt(ResourceHandler<T> handler, int index) {
        return get(index).getAmountAsInt(handler);
    }

    public long getAmountAsLong(ResourceHandler<T> handler, int index) {
        return get(index).getAmountAsLong(handler);
    }

    public int getCapacityAsInt(ResourceHandler<T> handler, int index, T resource) {
        return get(index).getCapacityAsInt(handler, resource);
    }

    public long getCapacityAsLong(ResourceHandler<T> handler, int index, T resource) {
        return get(index).getCapacityAsLong(handler, resource);
    }

    public void set(ResourceStorage<T> storage, int index, T resource, int amount) {
        get(index).set(storage, resource, amount);
    }

    public void set(ResourceStorage<T> storage, int index, T resource, long amount) {
        get(index).set(storage, resource, amount);
    }
}
