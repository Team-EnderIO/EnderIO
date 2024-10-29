package com.enderio.core.common.recipes;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public record RecipeTypeSerializerPair<R extends Recipe<?>, S extends RecipeSerializer<? extends R>>(
        DeferredHolder<RecipeType<?>, RecipeType<R>> type, DeferredHolder<RecipeSerializer<?>, S> serializer) {
}
