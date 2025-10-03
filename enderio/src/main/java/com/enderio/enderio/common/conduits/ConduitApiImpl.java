package com.enderio.enderio.common.conduits;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitApi;
import com.enderio.enderio.api.conduits.ConduitIngredient;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ConduitApiImpl implements ConduitApi {

    @Override
    public ItemStack getConduitItem(Holder<Conduit<?, ?>> conduit, int count) {
        return ConduitBlockItem.getStackFor(conduit, count);
    }

    @Override
    public Ingredient getConduitIngredient(Holder<Conduit<?, ?>> conduit) {
        return ConduitIngredient.of(conduit);
    }

    @Override
    public int getConduitSortIndex(Holder<Conduit<?, ?>> conduit) {
        return ConduitSorter.getSortIndex(conduit);
    }
}
