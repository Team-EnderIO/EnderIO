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
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.EnchantmentBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
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

    public static final RegistryEntry<WitherEnchantment> WITHERING = enchantmentBuilder("withering", new WitherEnchantment())
        .lang("Withering")
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
        if (event.getItemStack().getItem() == Items.ENCHANTED_BOOK) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(event.getItemStack());
            List<Component> toolTip = new ArrayList<>(event.getToolTip());
            if (!enchantments.isEmpty()) {
                addTooltip(event, enchantments, toolTip, WITHERING.get(), EIOLang.WITHERING_TYPES);
            }
        }
    }

    public static void register() {}

}
