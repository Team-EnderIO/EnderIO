package com.enderio.enderio.compat.jei.subtype;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgrade;
import com.enderio.enderio.init.EIODataComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DarkSteelUpgradeSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        Holder<DarkSteelUpgrade> upgrade = ingredient.get(EIODataComponents.DARK_STEEL_UPGRADE);
        if (upgrade != null) {
            return upgrade.getRegisteredName();
        }

        return null;
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        Holder<DarkSteelUpgrade> upgrade = ingredient.get(EIODataComponents.DARK_STEEL_UPGRADE);
        if (upgrade != null) {
            return upgrade.getRegisteredName();
        }

        return "";
    }
}
