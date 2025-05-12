package com.enderio.base.common.integrations.jei.subtype;

import com.enderio.base.common.init.EIOCapabilities;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SoulBindableSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        var soulBindable = ingredient.getCapability(EIOCapabilities.SoulBindable.ITEM);
        if (soulBindable != null) {
            return soulBindable.getBoundSoul();
        }

        return null;
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        var soulBindable = ingredient.getCapability(EIOCapabilities.SoulBindable.ITEM);
        if (soulBindable != null && soulBindable.hasSoul()) {
            return soulBindable.getBoundSoul().entityTypeId().toString();
        }

        return "";
    }
}
