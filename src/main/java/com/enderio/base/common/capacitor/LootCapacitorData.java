package com.enderio.base.common.capacitor;

import com.enderio.EnderIO;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.ICapacitorData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.enderio.base.EIONBTKeys.CAPACITOR_DATA;

// TODO: Instead of loot capacitors having lists of specialized machines, have different loot capacitor items for different
//       machine categories.
// TODO: Loot capacitor types (Sculk, Soul) found in respective dungeons/structures.
public final class LootCapacitorData implements ICapacitorData {
    private final ItemStack stack;

//    private final Map<CapacitorModifier, Float> modifiers;

    private static final String KEY_BASE = "Base";
    private static final String KEY_MODIFIER_ARRAY = "Modifiers";

    public LootCapacitorData(ItemStack stack) {
        this(stack, 1.0f, new HashMap<>());
    }

    public LootCapacitorData(ItemStack stack, float base, Map<CapacitorModifier, Float> specializations) {
        this.stack = stack;

        CompoundTag tag = this.stack.getOrCreateTag();
        CompoundTag nbt = new CompoundTag();
        if (!tag.contains(CAPACITOR_DATA)) {
            nbt.putFloat(KEY_BASE, base);

            CompoundTag modifiers = new CompoundTag();
            specializations.forEach((s, l) -> modifiers.putFloat(s.name(), l));
            nbt.put(KEY_MODIFIER_ARRAY, modifiers);

            tag.put(CAPACITOR_DATA, nbt);
        }
    }

    @Override
    public float getBase() {
        var tag = this.stack.getOrCreateTag();
        if (tag.contains(CAPACITOR_DATA) && tag.getCompound(CAPACITOR_DATA).contains(KEY_BASE)) {
            return tag.getCompound(CAPACITOR_DATA).getFloat(KEY_BASE);
        }
        return 0.0f;
    }

    public void setBase(float base) {
        var tag = this.stack.getOrCreateTag();
        if (tag.contains(CAPACITOR_DATA)) {
            tag.getCompound(CAPACITOR_DATA).putFloat(KEY_BASE, base);
        }
    }

    @Override
    public float getModifier(CapacitorModifier modifier) {
        var tag = this.stack.getOrCreateTag();

        if (tag.contains(CAPACITOR_DATA) && tag.getCompound(CAPACITOR_DATA).contains(KEY_MODIFIER_ARRAY) &&
            tag.getCompound(CAPACITOR_DATA).getCompound(KEY_MODIFIER_ARRAY).contains(modifier.name())) {
            return tag.getCompound(CAPACITOR_DATA).getCompound(KEY_MODIFIER_ARRAY).getFloat(modifier.name());
        }

        return getBase();
    }

    @Override
    public Map<CapacitorModifier, Float> getAllModifiers() {
        var tag = this.stack.getOrCreateTag();

        if (tag.contains(CAPACITOR_DATA) && tag.getCompound(CAPACITOR_DATA).contains(KEY_MODIFIER_ARRAY)) {
            var modifiers = tag.getCompound(CAPACITOR_DATA).getCompound(KEY_MODIFIER_ARRAY);

            try {
                return modifiers.getAllKeys().stream().collect(Collectors.toMap(CapacitorModifier::valueOf, modifiers::getFloat));
            } catch (NumberFormatException ex) {
                EnderIO.LOGGER.error("Loaded an invalid capacitor modifier key from item NBT! Capacitor reset.");
            }
        }

        return Map.of();
    }

    public void addModifier(CapacitorModifier modifier, float level) {
        var tag = this.stack.getOrCreateTag();

        if (tag.contains(CAPACITOR_DATA) && tag.getCompound(CAPACITOR_DATA).contains(KEY_MODIFIER_ARRAY)) {
            tag.getCompound(CAPACITOR_DATA).getCompound(KEY_MODIFIER_ARRAY).putFloat(modifier.name(), level);
        }
    }

    public void addNewModifier(CapacitorModifier modifier, float level) {
        var tag = this.stack.getOrCreateTag();

        if (tag.contains(CAPACITOR_DATA)) {
            tag.getCompound(CAPACITOR_DATA).put(KEY_MODIFIER_ARRAY, new CompoundTag());
        }

        addModifier(modifier, level);
    }

    public void addAllModifiers(Map<CapacitorModifier, Float> specializations) {
        for (Map.Entry<CapacitorModifier, Float> entry : specializations.entrySet()) {
            addModifier(entry.getKey(), entry.getValue());
        }
    }

}
