package com.enderio.base.common.capability;

import com.enderio.api.capability.IEntityStorage;
import com.enderio.api.capability.StoredEntityData;
import com.enderio.base.EIONBTKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class EntityStorageItemStack implements IEntityStorage {

    private final ItemStack stack;

    public EntityStorageItemStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public StoredEntityData getStoredEntityData() {
        CompoundTag tag = stack.getOrCreateTag();
        StoredEntityData entity = StoredEntityData.empty();
        if (tag.contains(BlockItem.BLOCK_ENTITY_TAG)) {
            CompoundTag entityTag = tag.getCompound(BlockItem.BLOCK_ENTITY_TAG).getCompound(EIONBTKeys.ENTITY_STORAGE);
            entity.deserializeNBT(entityTag);
        }
        return entity;
    }

    @Override
    public void setStoredEntityData(StoredEntityData entity) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag entityTag = new CompoundTag();
        entityTag.put(EIONBTKeys.ENTITY_STORAGE, entity.serializeNBT());
        tag.put(BlockItem.BLOCK_ENTITY_TAG, entityTag);
    }
}
