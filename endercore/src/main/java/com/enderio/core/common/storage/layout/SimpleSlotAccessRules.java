package com.enderio.core.common.storage.layout;

import net.neoforged.neoforge.transfer.resource.Resource;

public record SimpleSlotAccessRules<T extends Resource>(boolean canInsert, boolean canExtract) implements SlotAccessRules<T> {
    @Override
    public boolean canInsert(Resource resource) {
        return canInsert;
    }

    @Override
    public boolean canExtract(Resource resource) {
        return canExtract;
    }
}
