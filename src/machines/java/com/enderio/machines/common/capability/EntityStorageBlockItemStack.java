package com.enderio.machines.common.capability;

import com.enderio.api.capability.StoredEntityData;
import com.enderio.base.common.capability.EntityStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class EntityStorageBlockItemStack extends EntityStorage {

    private final ItemStack stack;

    public EntityStorageBlockItemStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public StoredEntityData getStoredEntityData() {
        CompoundTag tag = stack.getOrCreateTag();
        StoredEntityData entity = StoredEntityData.empty();
        if (tag.contains("BlockEntityTag")) {
            CompoundTag entityTag = tag.getCompound("entitystorage");
            entity.deserializeNBT(entityTag);
        }
        return entity;
    }

    @Override
    public void setStoredEntityData(StoredEntityData entity) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag entityTag = new CompoundTag();
        entityTag.put("entitystorage", entity.serializeNBT());
        tag.put("BlockEntityTag", entityTag);
    }
}
