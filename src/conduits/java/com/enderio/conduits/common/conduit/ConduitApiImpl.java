package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.ConduitApi;
import com.enderio.api.conduit.ConduitType;
import com.enderio.conduits.common.recipe.ConduitIngredient;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ConduitApiImpl implements ConduitApi {

    @Override
    public ItemStack getStackForType(Holder<ConduitType<?, ?, ?>> type, int count) {
        return ConduitBlockItem.getStackFor(type, count);
    }

    @Override
    public Ingredient getIngredientForType(Holder<ConduitType<?, ?, ?>> type) {
        return ConduitIngredient.of(type);
    }
}
