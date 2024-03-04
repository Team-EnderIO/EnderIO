package com.enderio.base.common.capacitor;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.ICapacitorData;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// TODO: Instead of loot capacitors having lists of specialized machines, have different loot capacitor items for different
//       machine categories.
// TODO: Loot capacitor types (Sculk, Soul) found in respective dungeons/structures.
public final class LootCapacitorData implements ICapacitorData, INBTSerializable<CompoundTag> {

    private float base;
    private Map<CapacitorModifier, Float> specializations;

    private static final String KEY_BASE = "Base";
    private static final String KEY_MODIFIER_ARRAY = "Modifiers";

    public LootCapacitorData() {
        this(1.0f, new HashMap<>());
    }

    public LootCapacitorData(float base, Map<CapacitorModifier, Float> specializations) {
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
    public float getModifier(CapacitorModifier modifier) {
        if (specializations.containsKey(modifier)) {
            return specializations.get(modifier);
        }

        return getBase();
    }

    @Override
    public Map<CapacitorModifier, Float> getAllModifiers() {
        return specializations;
    }

    public void addModifier(CapacitorModifier modifier, float level) {
        specializations.put(modifier, level);
    }

    public void addNewModifier(CapacitorModifier modifier, float level) {
        specializations.clear();
        addModifier(modifier, level);
    }

    public void addAllModifiers(Map<CapacitorModifier, Float> specializations) {
        for (Map.Entry<CapacitorModifier, Float> entry : specializations.entrySet()) {
            addModifier(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat(KEY_BASE, base);

        CompoundTag modifiers = new CompoundTag();
        specializations.forEach((s, l) -> modifiers.putFloat(s.name(), l));
        nbt.put(KEY_MODIFIER_ARRAY, modifiers);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        base = nbt.getFloat(KEY_BASE);

        var nbtModifiers = nbt.getCompound(KEY_MODIFIER_ARRAY);
        specializations = nbtModifiers.getAllKeys().stream().collect(Collectors.toMap(CapacitorModifier::valueOf, nbtModifiers::getFloat));
    }
}
