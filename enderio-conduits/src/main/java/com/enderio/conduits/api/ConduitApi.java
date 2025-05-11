package com.enderio.conduits.api;

import java.util.ServiceLoader;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface ConduitApi {

    ConduitApi INSTANCE = ServiceLoader.load(ConduitApi.class).findFirst().orElseThrow();

    default ItemStack getConduitItem(Holder<Conduit<?, ?>> conduit) {
        return getConduitItem(conduit, 1);
    }

    ItemStack getConduitItem(Holder<Conduit<?, ?>> conduit, int count);

    Ingredient getConduitIngredient(Holder<Conduit<?, ?>> conduit);

    int getConduitSortIndex(Holder<Conduit<?, ?>> conduit);
}
