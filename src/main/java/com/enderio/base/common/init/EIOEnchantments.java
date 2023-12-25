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
import com.enderio.core.common.registry.EnderDeferredObject;
import com.enderio.core.common.registry.EnderRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@EventBusSubscriber
public class EIOEnchantments {
    private static final EnderRegistry<Enchantment> ENCHANTMENTS = EnderRegistry.createRegistry(BuiltInRegistries.ENCHANTMENT, EnderIO.MODID);

    // region enchantments

    public static final EnderDeferredObject<Enchantment, AutoSmeltEnchantment> AUTO_SMELT = enchantmentBuilder("auto_smelt", new AutoSmeltEnchantment())
        .setTranslation("Auto Smelt");

    public static final EnderDeferredObject<Enchantment, RepellentEnchantment> REPELLENT = enchantmentBuilder("repellent", new RepellentEnchantment())
        .setTranslation("Repellent"); //TODO not needed now I think?

    public static final EnderDeferredObject<Enchantment, ShimmerEnchantment> SHIMMER = enchantmentBuilder("shimmer", new ShimmerEnchantment())
        .setTranslation("Shimmer");

    public static final EnderDeferredObject<Enchantment, SoulBoundEnchantment> SOULBOUND = enchantmentBuilder("soulbound", new SoulBoundEnchantment())
        .setTranslation("Soulbound");

    public static final EnderDeferredObject<Enchantment, WitherEnchantment> WITHERING = enchantmentBuilder("withering", new WitherEnchantment())
        .setTranslation("Withering");

    public static final EnderDeferredObject<Enchantment, XPBoostEnchantment> XP_BOOST = enchantmentBuilder("xp_boost", new XPBoostEnchantment())
        .setTranslation("XP Boost");

    // endregion

    // region builders

    private static <T extends EIOBaseEnchantment> EnderDeferredObject<Enchantment, T> enchantmentBuilder(String name, T enchantment) {
        return ENCHANTMENTS.register(name, () -> enchantment);
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

    public static void register() {
        ENCHANTMENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
