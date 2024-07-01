package com.enderio.api.conduit;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ServiceLoader;

public interface ConduitApi {

    ConduitApi INSTANCE = ServiceLoader.load(ConduitApi.class).findFirst().orElseThrow();

    default ItemStack getStackForType(Holder<ConduitType<?, ?, ?>> type) {
        return getStackForType(type, 1);
    }

    ItemStack getStackForType(Holder<ConduitType<?, ?, ?>> type, int count);

    Ingredient getIngredientForType(Holder<ConduitType<?, ?, ?>> type);
}
