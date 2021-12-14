package com.enderio.base.common.capability.entity;

import net.minecraft.nbt.Tag;

import javax.annotation.Nonnull;

public class EntityStorage implements IEntityStorage {
    private StoredEntityData entity = new StoredEntityData();

    @Nonnull
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
