package com.enderio.enderio.compat.jei.subtype;

import com.enderio.enderio.api.EnderIOCapabilities;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.Nullable;

public class BindableSoulSourceSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        if (ingredient.isEmpty()) {
            return null;
        }
        var source = ItemAccess.forStack(ingredient).getCapability(EnderIOCapabilities.BINDABLE_SOUL_SOURCE);
        if (source == null || !source.isSoulAvailable()) {
            return null;
        }
        var soul = source.availableSoul();
        // Recipes match entity types, irrespective of the captured entity's individual data.
        return soul.isEmpty() ? null : soul.entityTypeId();
    }
}
