package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.enchantment.*;
import com.enderio.base.common.lang.EIOLang;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.EnchantmentBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@EventBusSubscriber
public class EIOEnchantments {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    // region enchantments

    public static final RegistryEntry<AutoSmeltEnchantment> AUTO_SMELT = enchantmentBuilder("auto_smelt", new AutoSmeltEnchantment())
        .lang("Auto Smelt")
        .register();

    public static final RegistryEntry<RepellentEnchantment> REPELLENT = enchantmentBuilder("repellent", new RepellentEnchantment())
        .lang("Repellent")
        .register();

    public static final RegistryEntry<ShimmerEnchantment> SHIMMER = enchantmentBuilder("shimmer", new ShimmerEnchantment())
        .lang("Shimmer")
        .register();

    public static final RegistryEntry<SoulBoundEnchantment> SOULBOUND = enchantmentBuilder("soulbound", new SoulBoundEnchantment())
        .lang("Soulbound")
        .register();

    public static final RegistryEntry<WitherBladeEnchantment> WITHERING_BLADE = enchantmentBuilder("withering_blade", new WitherBladeEnchantment())
        .lang("Withering Blade")
        .register();

    public static final RegistryEntry<WitherArrowEnchantment> WITHERING_ARROW = enchantmentBuilder("withering_arrow", new WitherArrowEnchantment())
        .lang("Withering Arrow")
        .register();

    public static final RegistryEntry<WitherArrowEnchantment> WITHERING_BOLT = enchantmentBuilder("withering_bolt", new WitherArrowEnchantment())
        .lang("Withering Bolt")
        .register();

    public static final RegistryEntry<XPBoostEnchantment> XP_BOOST = enchantmentBuilder("xp_boost", new XPBoostEnchantment()).lang("XP Boost").register();

    // endregion

    // region builders

    private static <T extends EIOBaseEnchantment> EnchantmentBuilder<T, Registrate> enchantmentBuilder(String name, T enchantment) {
        return REGISTRATE.enchantment(name, enchantment.getCategory(), (r, c, s) -> enchantment);
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
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(event.getItemStack());
        List<Component> toolTip = new ArrayList<>(event.getToolTip());
        if (!enchantments.isEmpty()) {
            addTooltip(event, enchantments, toolTip, AUTO_SMELT.get(), EIOLang.AUTO_SMELT_DESC);
            addTooltip(event, enchantments, toolTip, REPELLENT.get(), EIOLang.REPELLENT_DESC1, EIOLang.REPELLENT_DESC2);
            addTooltip(event, enchantments, toolTip, SHIMMER.get(), EIOLang.SHIMMER_DESC1, EIOLang.SHIMMER_DESC2, EIOLang.SHIMMER_DESC3, EIOLang.SHIMMER_DESC4, EIOLang.SHIMMER_DESC5, EIOLang.SHIMMER_DESC6);
            addTooltip(event, enchantments, toolTip, SOULBOUND.get(), EIOLang.SOULBOUND_DESC1, EIOLang.SOULBOUND_DESC2);
            addTooltip(event, enchantments, toolTip, WITHERING_BLADE.get(), EIOLang.WITHERING_BLADE_DESC1, EIOLang.WITHERING_BLADE_DESC2);
            addTooltip(event, enchantments, toolTip, WITHERING_ARROW.get(), EIOLang.WITHERING_ARROW_DESC1, EIOLang.WITHERING_ARROW_DESC2);
            addTooltip(event, enchantments, toolTip, WITHERING_BOLT.get(), EIOLang.WITHERING_BOLT_DESC1, EIOLang.WITHERING_BOLT_DESC2);
            addTooltip(event, enchantments, toolTip, XP_BOOST.get(), EIOLang.XP_BOOST_DESC);
        }
    }

    public static void register() {}
}
