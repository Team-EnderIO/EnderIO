package com.enderio.core.common.recipes;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public record RecipeTypeSerializerPair<R extends Recipe<?>, S extends RecipeSerializer<? extends R>>(Supplier<RecipeType<R>> type, Supplier<S> serializer) {
}
