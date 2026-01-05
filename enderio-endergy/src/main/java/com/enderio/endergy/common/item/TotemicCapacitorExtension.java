package com.enderio.endergy.common.item;

import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.capacitor.ICapacitorExtension;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TotemicCapacitorExtension implements ICapacitorExtension {

    public static final TotemicCapacitorExtension INSTANCE = new TotemicCapacitorExtension();

    private static final Map<Integer, CapacitorData> CACHE = new HashMap<>();

    private TotemicCapacitorExtension() {
    }

    @Override
    public void onMachineUsed(ItemStack capacitorItem, ServerLevel level) {
        capacitorItem.hurtAndBreak(1, level, null, i -> {});
    }

    @Override
    public @Nullable CapacitorData getCapacitorData(ItemStack capacitorStack, Level level) {
        var enchantmentRegistry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        var efficiencyLevel = capacitorStack.getEnchantmentLevel(enchantmentRegistry.getOrThrow(Enchantments.EFFICIENCY));
        return CACHE.computeIfAbsent(efficiencyLevel, l -> new CapacitorData(3.5f + l * 0.5f, Map.of()));
    }
}
