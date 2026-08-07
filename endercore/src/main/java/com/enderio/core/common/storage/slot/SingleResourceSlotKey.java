package com.enderio.core.common.storage.slot;

import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public final class SingleResourceSlotKey<T extends Resource> implements ResourceSlotKey<T>, ResourceSlotId<T> {
    @Override
    public int index(ResourceStorageLayout<T> layout) {
        return layout.indexOf(this);
    }

    @Override
    public Collection<ResourceSlotId<T>> slots() {
        return Collections.singleton(this);
    }

    @Override
    public Iterator<ResourceSlotId<T>> iterator() {
        return slots().iterator();
    }

    @Override
    public ResourceHandler<T> rangedHandler(ResourceStorage<T> storage) {
        return RangedResourceHandler.ofSingleIndex(storage, index(storage));
    }
}
