package com.enderio.enderio.content.armory;

import com.enderio.enderio.foundation.tag.EIOTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class DarkSteelTiers {
    public static final Tier DARK_STEEL_TIER = new SimpleTier(EIOTags.Blocks.INCORRECT_FOR_DARK_STEEL_TOOL, 2000,
        8.0f, 3.0f, 25, () -> Ingredient.of(EIOTags.Items.INGOTS_DARK_STEEL));
}
