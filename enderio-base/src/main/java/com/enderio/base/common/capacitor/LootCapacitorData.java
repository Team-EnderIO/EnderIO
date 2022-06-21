package com.enderio.base.common.capacitor;

import com.enderio.api.capability.ICapacitorData;
import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.base.EnderIO;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

// TODO: Flavour text logic should probably be moved into here from CapacitorUtil.
public final class LootCapacitorData implements ICapacitorData {
    private float base;
    private final Map<ResourceLocation, Float> specializations;

    public LootCapacitorData() {
        this.base = 1.0f;
        this.specializations = new HashMap<>();
    }

    public LootCapacitorData(float base, Map<ResourceLocation, Float> specializations) {
        this.base = base;
        this.specializations = specializations;
    }

    @Override
    public float getBase() {
        return base;
    }

    public void setBase(float base) {
        this.base = base;
    }

    @Override
    public float getLevel(CapacitorKey key) {
        if (specializations.containsKey(key.getRegistryName())) {
            return specializations.get(key.getRegistryName());
        }
        return getBase();
    }

    public void addSpecialization(CapacitorKey key, float level) {
        this.specializations.put(key.getRegistryName(), level);
    }

    public void addNewSpecialization(CapacitorKey key, float level) {
        this.specializations.clear();
        addSpecialization(key, level);
    }

    public void addAllSpecialization(Map<CapacitorKey, Float> specializations) {
        for (Map.Entry<CapacitorKey, Float> entry : specializations.entrySet()) {
            addSpecialization(entry.getKey(), entry.getValue());
        }
    }

    // region Serialization

    @Override
    public Tag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("base", base);
        ListTag list = new ListTag();
        specializations.forEach((s, l) -> {
            CompoundTag entry = new CompoundTag();
            entry.putString("key", s.toString());
            entry.putFloat("level", l);
            list.add(entry);
        });
        tag.put("specializations", list);
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (nbt instanceof CompoundTag tag) {
            this.specializations.clear();
            this.base = tag.getFloat("base");
            ListTag list = tag.getList("specializations", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag listElement = list.getCompound(i);
                CapacitorKey key = EnderIO.CAPACITOR_KEY_REGISTRY.getValue(new ResourceLocation(listElement.getString("type")));

                if (key == null) {
                    EnderIO.LOGGER.warn("Capacitor had a specialization for a capacitor key that isn't registered! Ignoring. Data may be lost.");
                } else {
                    addSpecialization(key, listElement.getFloat("level"));
                }
            }
        }
    }

    // endregion
}
