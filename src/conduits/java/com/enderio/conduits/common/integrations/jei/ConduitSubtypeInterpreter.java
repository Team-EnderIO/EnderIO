package com.enderio.conduits.common.integrations.jei;

import com.enderio.api.registry.EnderIORegistries;
import com.enderio.conduits.common.components.RepresentedConduitType;
import com.enderio.conduits.common.init.ConduitComponents;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ConduitSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    @Override
    public String apply(ItemStack ingredient, UidContext context) {
        if (ingredient.has(ConduitComponents.REPRESENTED_CONDUIT_TYPE)) {
            RepresentedConduitType representedConduitType = ingredient.get(ConduitComponents.REPRESENTED_CONDUIT_TYPE);
            ResourceLocation conduitTypeKey = EnderIORegistries.CONDUIT_TYPES.getKey(representedConduitType.conduitType());
            if (conduitTypeKey != null) {
                return conduitTypeKey.toString();
            }
        }

        return IIngredientSubtypeInterpreter.NONE;
    }
}
