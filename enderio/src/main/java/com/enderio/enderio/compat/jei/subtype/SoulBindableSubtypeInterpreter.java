package com.enderio.enderio.compat.jei.subtype;

import com.enderio.enderio.api.EnderIOCapabilities;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.Nullable;

public class SoulBindableSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        var soulBindable = ItemAccess.forStack(ingredient).getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        if (soulBindable != null) {
            return soulBindable.boundSoul();
        }

        return null;
    }
}
