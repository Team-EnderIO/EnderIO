package com.enderio.endergy.common.item;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.ICapacitorExtension;
import com.enderio.enderio.content.capacitors.CapacitorItem;
import com.enderio.enderio.content.capacitors.CapacitorLang;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class TotemicCapacitorItem extends CapacitorItem implements ICapacitorExtension {
    private static final Map<Integer, CapacitorData> DATA_CACHE = new HashMap<>();

    public TotemicCapacitorItem(Properties properties) {
        super(properties.durability(512));
    }

    @Override
    public void onMachineUsed(ItemStack capacitorStack, ServerLevel level) {
        capacitorStack.hurtAndBreak(1, level, null, i -> {});
    }

    @Override
    public @Nullable CapacitorData getCapacitorData(ItemStack capacitorStack, Level level) {
        var enchantmentRegistry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        var efficiencyLevel = capacitorStack.getEnchantmentLevel(enchantmentRegistry.getOrThrow(Enchantments.EFFICIENCY));
        return DATA_CACHE.computeIfAbsent(efficiencyLevel, l -> new CapacitorData(3.5f + l * 0.5f, Map.of()));
    }
}
