package com.enderio.base.common.item.capacitors;

import com.enderio.api.capability.IMultiCapabilityItem;
import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.ICapacitorData;
import com.enderio.base.common.capacitor.CapacitorUtil;
import com.enderio.base.common.capacitor.LootCapacitorData;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public class LootCapacitorItem extends BaseCapacitorItem implements IMultiCapabilityItem {

    public LootCapacitorItem(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.add(EIOCapabilities.CAPACITOR, LazyOptional.of(() -> new LootCapacitorData(stack)));
        return provider;
    }

    @Override
    public Component getName(ItemStack pStack) {
        var capacitorDataOpt = CapacitorUtil.getCapacitorData(pStack);

        if (capacitorDataOpt.isEmpty()) {
            return super.getName(pStack);
        }

        var capacitorData = capacitorDataOpt.get();
        if (capacitorData.getAllModifiers().isEmpty()) {
            return getBaseName(capacitorData);
        }

        return getModifierQuality(capacitorData)
            .copy()
            .append(" ")
            .append(getModifierType(capacitorData))
            .append(" ")
            .append(getBaseName(capacitorData));
    }

    private MutableComponent getBaseName(ICapacitorData capacitorData) {
        float base = capacitorData.getBase();

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

    private MutableComponent getModifierType(ICapacitorData capacitorData) {
        var firstType = capacitorData.getAllModifiers().keySet().stream().findFirst();

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

    private MutableComponent getModifierQuality(ICapacitorData capacitorData) {
        var firstModifier = capacitorData.getAllModifiers().values().stream().findFirst();

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
