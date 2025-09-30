package com.enderio.enderio.common.integrations.jei.subtype;

import com.enderio.enderio.api.EnderIOCapabilities;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SoulBindableSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        var soulBindable = ingredient.getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        if (soulBindable != null) {
            return soulBindable.getBoundSoul();
        }

        return null;
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        var soulBindable = ingredient.getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        if (soulBindable != null && soulBindable.hasSoul()) {
            return soulBindable.getBoundSoul().entityTypeId().toString();
        }

        return "";
    }
}
