package com.enderio.core.common.storage.layout;

import net.neoforged.neoforge.transfer.resource.Resource;

public interface SlotAccessRules<T extends Resource> {
    boolean canInsert(T resource);
    boolean canExtract(T resource);
}
