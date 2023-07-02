package com.enderio.base.common.capability;

import com.enderio.api.capability.StoredEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class EntityStorageItemStack extends EntityStorage {

    private final ItemStack stack;

    public EntityStorageItemStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public StoredEntityData getStoredEntityData() {
        CompoundTag tag = stack.getOrCreateTag();
        StoredEntityData entity = StoredEntityData.empty();
        if (tag.contains("BlockEntityTag")) {
            CompoundTag entityTag = tag.getCompound("BlockEntityTag").getCompound("EntityStorage");
            entity.deserializeNBT(entityTag);
        }
        return entity;
    }

    @Override
    public void setStoredEntityData(StoredEntityData entity) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag entityTag = new CompoundTag();
        entityTag.put("EntityStorage", entity.serializeNBT());
        tag.put("BlockEntityTag", entityTag);
    }
}
