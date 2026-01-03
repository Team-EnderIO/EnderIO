package com.enderio.core.common.storage.slot;

import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import net.neoforged.neoforge.transfer.resource.Resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public final class MultiResourceSlotKey<T extends Resource> implements ResourceSlotKey, Iterable<ResourceSlotId<T>> {
    private final int count;
    private final Map<Integer, ResourceSlotId<T>> slotIdCache;

    public MultiResourceSlotKey(int count) {
        if (count <= 1) {
            throw new IllegalArgumentException("A MultiResourceSlotKey must represent 2 or more slots.");
        }

        this.count = count;

        // Create a cache of slot IDs for quick access
        var slotIdCacheMutable = new LinkedHashMap<Integer, ResourceSlotId<T>>();
        IntStream.range(0, count).forEach(i -> slotIdCacheMutable.put(i, layout -> layout.indexOf(this, i)));
        this.slotIdCache = Collections.unmodifiableMap(slotIdCacheMutable);
    }

    public int count() {
        return count;
    }

    public ResourceSlotId<T> slot(int index) {
        Objects.checkIndex(index, count);
        return slotIdCache.get(index);
    }

    @Override
    public Iterator<ResourceSlotId<T>> iterator() {
        return slotIdCache.values().iterator();
    }
}
