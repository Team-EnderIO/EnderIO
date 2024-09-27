package com.enderio.core.common.capability;

import com.enderio.api.capability.StoredEntityData;
import com.enderio.api.filter.EntityFilter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EntityFilterCapability implements IFilterCapability<StoredEntityData>, EntityFilter {

    private static final String NBT_KEY = "IsNbt";
    private static final String INVERTED_KEY = "IsInverted";
    private static final String ENTRIES_KEY = "EntityEntries";

    private final ItemStack container;
    private final int size;

    public EntityFilterCapability(ItemStack container, int size) {
        this.container = container;
        this.size = size;

        CompoundTag tag = container.getOrCreateTag();
        if (!tag.contains(ENTRIES_KEY, CompoundTag.TAG_LIST)) {
            ListTag entriesList = new ListTag();
            for (int i = 0; i < size; i++) {
                entriesList.add(StoredEntityData.empty().serializeNBT());
            }
            tag.put(ENTRIES_KEY, entriesList);
        }
    }

    @Override
    public void setNbt(Boolean nbt) {
        CompoundTag tag = container.getOrCreateTag();
        tag.putBoolean(NBT_KEY, nbt);
    }

    @Override
    public boolean isNbt() {
        CompoundTag tag = container.getOrCreateTag();
        return tag.contains(NBT_KEY, CompoundTag.TAG_BYTE) && tag.getBoolean(NBT_KEY);
    }

    @Override
    public void setInverted(Boolean inverted) {
        CompoundTag tag = container.getOrCreateTag();
        tag.putBoolean(INVERTED_KEY, inverted);
    }

    @Override
    public boolean isInvert() {
        CompoundTag tag = container.getOrCreateTag();
        return tag.contains(INVERTED_KEY, CompoundTag.TAG_BYTE) && tag.getBoolean(INVERTED_KEY);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<StoredEntityData> getEntries() {
        CompoundTag tag = container.getOrCreateTag();

        List<StoredEntityData> entries = new ArrayList<>();
        if (tag.contains(ENTRIES_KEY, CompoundTag.TAG_LIST)) {
            ListTag entriesList = tag.getList(ENTRIES_KEY, CompoundTag.TAG_COMPOUND);
            for (var entry : entriesList) {
                StoredEntityData entityData = StoredEntityData.empty();
                entityData.deserializeNBT(entry);
                entries.add(entityData);
            }
        }

        return entries;
    }

    @Override
    public StoredEntityData getEntry(int index) {
        CompoundTag tag = container.getOrCreateTag();

        if (!tag.contains(ENTRIES_KEY, CompoundTag.TAG_LIST)) {
            return StoredEntityData.empty();
        }

        ListTag entriesList = tag.getList(ENTRIES_KEY, CompoundTag.TAG_COMPOUND);
        StoredEntityData entityData = StoredEntityData.empty();
        entityData.deserializeNBT(entriesList.get(index));
        return entityData;
    }

    @Override
    public void setEntry(int index, StoredEntityData entry) {
        CompoundTag tag = container.getOrCreateTag();

        ListTag entriesList;
        if (tag.contains(ENTRIES_KEY, CompoundTag.TAG_LIST)) {
            entriesList = tag.getList(ENTRIES_KEY, CompoundTag.TAG_COMPOUND);
        } else {
            entriesList = new ListTag();
            tag.put(ENTRIES_KEY, entriesList);
        }

        entriesList.set(index, entry.serializeNBT());
    }

    @Override
    public boolean test(Entity entity) {
        boolean typematch = test(entity.getType());
        if (isNbt()) {
            for (StoredEntityData entry : getEntries()) {
                CompoundTag tag = entity.serializeNBT();
                boolean test = tag.equals(entry.getEntityTag());
                if (test) {
                    return !isInvert() && typematch;
                }
            }
        }

        return typematch;
    }

    @Override
    public boolean test(EntityType<?> entity) {
        for (StoredEntityData entry : getEntries()) {
            ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(entity);
            if (entry.getEntityType().isPresent() && entry.getEntityType().get().equals(key)) {
                return !isInvert();
            }
        }
        return isInvert();
    }

}
