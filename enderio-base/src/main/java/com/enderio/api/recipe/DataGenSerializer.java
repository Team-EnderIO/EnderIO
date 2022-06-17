package com.enderio.api.recipe;

import com.google.gson.JsonObject;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public abstract class DataGenSerializer<T extends Recipe<C>, C extends Container> implements RecipeSerializer<T> {
    public abstract void toJson(T recipe, JsonObject json);
}
