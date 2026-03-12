package com.enderio.enderio.content.armory;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

public class DarkSteelTiers {
    public static final Tier DARK_STEEL_TIER = TierSortingRegistry.registerTier(
        new ForgeTier(3, 2000, 8.0F, 3, 25, EIOTags.Blocks.NEEDS_DARK_STEEL,
            () -> Ingredient.of(EIOItems.DARK_STEEL_INGOT.get())), EnderIO.rl("dark_steel_tier"), List.of(Tiers.DIAMOND), List.of(Tiers.NETHERITE));
}
