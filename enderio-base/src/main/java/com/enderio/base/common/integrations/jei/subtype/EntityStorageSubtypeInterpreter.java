package com.enderio.base.common.integrations.jei.subtype;

import com.enderio.base.common.init.EIOCapabilities;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EntityStorageSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        var soulStorage = ingredient.getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
        if (soulStorage != null) {
            return soulStorage.getSoul();
        }

        return null;
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        var soulStorage = ingredient.getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
        if (soulStorage != null) {
            return soulStorage.getSoul().entityType().map(ResourceLocation::toString).orElse("");
        }

        return "";
    }
}
