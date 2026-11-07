package com.enderio.enderio.datagen.common.datapack_registries;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgrade;
import com.enderio.enderio.content.armory.effects.tools.AddItemAbilitiesEffect;
import com.enderio.enderio.content.armory.effects.tools.AdditionalToolRulesEffect;
import com.enderio.enderio.content.armory.effects.tools.ShovelBlockTransformationEffect;
import com.enderio.enderio.foundation.tag.EIOTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.ItemAbilities;

import java.util.List;

public class DarkSteelUpgrades {
    public static void bootstrap(BootstrapContext<DarkSteelUpgrade> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        // /give @s enderio:dark_steel_pickaxe[enderio:dark_steel_upgrades={upgrades:["enderio:spoon"]}]
        context.register(key("spoon"), new DarkSteelUpgrade(Component.literal("Spoon"),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE), false), HolderSet.empty(), List.of(
                new AddItemAbilitiesEffect(ItemAbilities.SHOVEL_DIG),
                new AdditionalToolRulesEffect.MinesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL),
                ShovelBlockTransformationEffect.INSTANCE
        )));
    }

    private static ResourceKey<DarkSteelUpgrade> key(String name) {
        return ResourceKey.create(EnderIORegistries.Keys.DARK_STEEL_UPGRADE, EnderIO.rl(name));
    }
}
