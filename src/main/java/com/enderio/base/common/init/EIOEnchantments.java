package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.enchantment.AutoSmeltEnchantment;
import com.enderio.base.common.enchantment.EIOBaseEnchantment;
import com.enderio.base.common.enchantment.RepellentEnchantment;
import com.enderio.base.common.enchantment.ShimmerEnchantment;
import com.enderio.base.common.enchantment.SoulBoundEnchantment;
import com.enderio.base.common.enchantment.WitherEnchantment;
import com.enderio.base.common.enchantment.XPBoostEnchantment;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@EventBusSubscriber
public class EIOEnchantments {
    private static final DeferredRegister<Enchantment> ENCHANTMENT_REGISTRY = DeferredRegister.create(Registries.ENCHANTMENT, EnderIO.MODID);

    // region enchantments

    public static final DeferredHolder<Enchantment, AutoSmeltEnchantment> AUTO_SMELT =
        enchantmentBuilder("auto_smelt", "Auto Smelt", AutoSmeltEnchantment::new);

    public static final DeferredHolder<Enchantment, RepellentEnchantment> REPELLENT =
        enchantmentBuilder("repellent", "Repellent", RepellentEnchantment::new);

    public static final DeferredHolder<Enchantment, ShimmerEnchantment> SHIMMER =
        enchantmentBuilder("shimmer", "Shimmer", ShimmerEnchantment::new);

    public static final DeferredHolder<Enchantment, SoulBoundEnchantment> SOULBOUND =
        enchantmentBuilder("soulbound", "Soulbound", SoulBoundEnchantment::new);

    public static final DeferredHolder<Enchantment, WitherEnchantment> WITHERING =
        enchantmentBuilder("withering", "Withering", WitherEnchantment::new);

    public static final DeferredHolder<Enchantment, XPBoostEnchantment> XP_BOOST =
        enchantmentBuilder("xp_boost", "XP Boost", XPBoostEnchantment::new);

    // endregion

    // region builders

    private static <T extends EIOBaseEnchantment> DeferredHolder<Enchantment, T> enchantmentBuilder(String name, String translation, Supplier<T> enchantment) {
        var holder = ENCHANTMENT_REGISTRY.register(name, enchantment);
        addTranslation(name, translation);
        return holder;
    }

    private static void addTranslation(String name, String translation) {
        EnderIO.getRegilite().addTranslation("enchantment", new ResourceLocation(EnderIO.MODID, name), translation);
    }
    
    private static void addTooltip(ItemTooltipEvent event, Map<Enchantment, Integer> enchantments, List<Component> toolTip, Enchantment enchantment, Component... components) {
        if (enchantments.containsKey(enchantment)) {
            toolTip.stream().forEach(c -> {
                if(c.equals(enchantment.getFullname(enchantments.get(enchantment)))) {
                    for (int i = 0; i < components.length; i++) {
                        event.getToolTip().add(event.getToolTip().indexOf(c)+i+1, components[i]);
                    }
                }
            });
        }
    }

    // endregion
    
    // Renders Enchantment tooltips.
    @SubscribeEvent
    static void tooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() == Items.ENCHANTED_BOOK) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(event.getItemStack());
            List<Component> toolTip = new ArrayList<>(event.getToolTip());
            if (!enchantments.isEmpty()) {
                addTooltip(event, enchantments, toolTip, WITHERING.get(), EIOLang.WITHERING_TYPES);
            }
        }
    }

    public static void register(IEventBus bus) {
        ENCHANTMENT_REGISTRY.register(bus);
    }

}
