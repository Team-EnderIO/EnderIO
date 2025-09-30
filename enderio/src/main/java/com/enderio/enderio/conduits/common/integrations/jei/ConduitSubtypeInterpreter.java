package com.enderio.enderio.conduits.common.integrations.jei;

import com.enderio.enderio.conduits.api.Conduit;
import com.enderio.enderio.conduits.common.init.ConduitComponents;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

public class ConduitSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    @Override
    public String apply(ItemStack ingredient, UidContext context) {
        Holder<Conduit<?, ?>> conduit = ingredient.get(ConduitComponents.CONDUIT);
        if (conduit != null) {
            return conduit.getRegisteredName();
        }

        return IIngredientSubtypeInterpreter.NONE;
    }
}
