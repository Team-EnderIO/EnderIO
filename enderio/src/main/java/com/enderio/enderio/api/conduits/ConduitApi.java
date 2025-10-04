package com.enderio.enderio.api.conduits;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ServiceLoader;

public interface ConduitApi {

    ConduitApi INSTANCE = ServiceLoader.load(ConduitApi.class).findFirst().orElseThrow();

    ItemStack getConduitItem(Holder<Conduit<?, ?>> conduit, int count);

    default ItemStack getConduitItem(Holder<Conduit<?, ?>> conduit) {
        return getConduitItem(conduit, 1);
    }

    int getConduitSortIndex(Holder<Conduit<?, ?>> conduit);
}
