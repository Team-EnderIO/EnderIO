package com.enderio.base.datagen.recipe.standard;

import com.enderio.base.EnderIO;
import com.enderio.base.common.recipe.GrindingballRecipe;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FinishedGrindingballRecipe implements FinishedRecipe{
    private GrindingballRecipe recipe;
    private ResourceLocation id;

    public FinishedGrindingballRecipe(GrindingballRecipe recipe, String name) {
       this(recipe, EnderIO.loc("grindingballs/" + name));
    }
    
    public FinishedGrindingballRecipe(GrindingballRecipe recipe, ResourceLocation id) {
        this.recipe = recipe;
        this.id = id;
    }
    
    @Override
    public void serializeRecipeData(JsonObject pJson) {
        recipe.getSerializer().toJson(recipe, pJson);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return this.recipe.getSerializer();
    }

    @Override
    public JsonObject serializeAdvancement() {
        return null;
    }

    @Override
    public ResourceLocation getAdvancementId() {
        return null;
    }

}
