package com.enderio.base.common.capability;

import com.enderio.api.capability.IEntityStorage;
import com.enderio.api.capability.StoredEntityData;
import net.minecraft.nbt.Tag;

public class EntityStorage implements IEntityStorage {
    private StoredEntityData entity = new StoredEntityData();

    @Override
    public StoredEntityData getStoredEntityData() {
        return entity;
    }

    @Override
    public void setStoredEntityData(StoredEntityData entity) {
        this.entity = entity;
    }

    public void empty() {
        entity = new StoredEntityData();
    }

    @Override
    public Tag serializeNBT() {
        return entity.serializeNBT();
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        entity.deserializeNBT(nbt);
    }
}
