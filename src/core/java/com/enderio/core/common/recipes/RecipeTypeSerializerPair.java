package com.enderio.core.common.recipes;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

public record RecipeTypeSerializerPair<R extends Recipe<?>, S extends RecipeSerializer<? extends R>>(RegistryObject<RecipeType<R>> type, RegistryObject<S> serializer) {
}
