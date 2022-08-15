package com.enderio.base.common.capacitor;

import com.enderio.EnderIO;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.ICapacitorData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;

public final class LootCapacitorData implements ICapacitorData {
    private float base;

    private final Map<CapacitorModifier, Float> modifiers;

    public LootCapacitorData() {
        this.base = 1.0f;
        this.modifiers = new HashMap<>();
    }

    public LootCapacitorData(float base, Map<CapacitorModifier, Float> specializations) {
        this.base = base;
        this.modifiers = specializations;
    }

    @Override
    public float getBase() {
        return base;
    }

    public void setBase(float base) {
        this.base = base;
    }

    @Override
    public float getModifier(CapacitorModifier modifier) {
        return modifiers.getOrDefault(modifier, getBase());
    }

    public void addSpecialization(CapacitorModifier modifier, float level) {
        this.modifiers.put(modifier, level);
    }

    public void addNewModifier(CapacitorModifier modifier, float level) {
        this.modifiers.clear();
        addSpecialization(modifier, level);
    }

    public void addAllModifiers(Map<CapacitorModifier, Float> specializations) {
        for (Map.Entry<CapacitorModifier, Float> entry : specializations.entrySet()) {
            addSpecialization(entry.getKey(), entry.getValue());
        }
    }

    // region Serialization

    @Override
    public Tag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("base", base);
        ListTag list = new ListTag();
        modifiers.forEach((s, l) -> {
            CompoundTag entry = new CompoundTag();
            entry.putInt("modifier", s.ordinal());
            entry.putFloat("level", l);
            list.add(entry);
        });
        tag.put("modifiers", list);
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (nbt instanceof CompoundTag tag) {
            this.modifiers.clear();
            this.base = tag.getFloat("base");
            ListTag list = tag.getList("modifiers", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag listElement = list.getCompound(i);
                try {
                    addSpecialization(CapacitorModifier.values()[listElement.getInt("modifier")], listElement.getFloat("level"));
                } catch (IndexOutOfBoundsException ex) { // In case something happens in the future.
                    EnderIO.LOGGER.error("Invalid capacitor modifier in loot capacitor NBT. Ignoring.");
                }
            }
        }
    }

    // endregion
}
