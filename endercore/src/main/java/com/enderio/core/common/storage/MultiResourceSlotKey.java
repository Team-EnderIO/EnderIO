package com.enderio.core.common.storage;

import net.neoforged.neoforge.transfer.resource.Resource;

public final class MultiResourceSlotKey<T extends Resource> implements ResourceSlotKey {
    private final int count;

    public MultiResourceSlotKey(int count) {
        this.count = count;
    }

    public int count() {
        return count;
    }
}
