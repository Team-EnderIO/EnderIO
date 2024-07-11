package com.enderio.base.common.item.capacitors;

import com.enderio.base.api.capacitor.CapacitorData;
import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class LootCapacitorItem extends CapacitorItem {
    public LootCapacitorItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        var capacitorData = pStack.getOrDefault(EIODataComponents.CAPACITOR_DATA, CapacitorData.NONE);

        if (capacitorData.modifiers().isEmpty()) {
            return getBaseName(capacitorData);
        }

        return getModifierQuality(capacitorData)
            .copy()
            .append(" ")
            .append(getModifierType(capacitorData))
            .append(" ")
            .append(getBaseName(capacitorData));
    }

    private MutableComponent getBaseName(CapacitorData capacitorData) {
        float base = capacitorData.base();

        if (base < 1f) {
            return EIOLang.LOOT_CAPACITOR_BASE_DUD;
        } else if (base < 1.5f) {
            return EIOLang.LOOT_CAPACITOR_BASE_NORMAL;
        } else if (base < 2.5f) {
            return EIOLang.LOOT_CAPACITOR_BASE_ENHANCED;
        } else if (base < 3.5f) {
            return EIOLang.LOOT_CAPACITOR_BASE_WONDER;
        }

        return EIOLang.LOOT_CAPACITOR_BASE_IMPOSSIBLE;
    }

    private MutableComponent getModifierType(CapacitorData capacitorData) {
        var firstType = capacitorData.modifiers().keySet().stream().findFirst();

        if (firstType.isEmpty()) {
            throw new IllegalArgumentException();
        }

        // TODO: Use a map for this instead.
        if (firstType.get() == CapacitorModifier.ENERGY_CAPACITY) {
            return EIOLang.LOOT_CAPACITOR_TYPE_ENERGY_CAPACITY;
        } else if (firstType.get() == CapacitorModifier.ENERGY_USE) {
            return EIOLang.LOOT_CAPACITOR_TYPE_ENERGY_USE;
        } else if (firstType.get() == CapacitorModifier.FUEL_EFFICIENCY) {
            return EIOLang.LOOT_CAPACITOR_TYPE_FUEL_EFFICIENCY;
        } else if (firstType.get() == CapacitorModifier.BURNING_ENERGY_GENERATION) {
            return EIOLang.LOOT_CAPACITOR_TYPE_BURNING_ENERGY_GENERATION;
        }

        return EIOLang.LOOT_CAPACITOR_TYPE_UNKNOWN;
    }

    private MutableComponent getModifierQuality(CapacitorData capacitorData) {
        var firstModifier = capacitorData.modifiers().values().stream().findFirst();

        if (firstModifier.isEmpty()) {
            throw new IllegalArgumentException();
        }

        float modifier = firstModifier.get();

        if (modifier < 1f) {
            return EIOLang.LOOT_CAPACITOR_MODIFIER_FAILED;
        } else if (modifier < 1.5f) {
            return EIOLang.LOOT_CAPACITOR_MODIFIER_SIMPLE;
        } else if (modifier < 2.5f) {
            return EIOLang.LOOT_CAPACITOR_MODIFIER_NICE;
        } else if (modifier < 3f) {
            return EIOLang.LOOT_CAPACITOR_MODIFIER_GOOD;
        } else if (modifier < 3.5f) {
            return EIOLang.LOOT_CAPACITOR_MODIFIER_ENHANCED;
        } else if (modifier < 4f) {
            return EIOLang.LOOT_CAPACITOR_MODIFIER_PREMIUM;
        } else if (modifier < 4.25f) {
            return EIOLang.LOOT_CAPACITOR_MODIFIER_INCREDIBLY;
        }

        return EIOLang.LOOT_CAPACITOR_MODIFIER_UNSTABLE;
    }
}
