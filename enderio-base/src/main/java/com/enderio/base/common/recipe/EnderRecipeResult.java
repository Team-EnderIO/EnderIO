package com.enderio.base.common.recipe;

import com.enderio.api.recipe.IEnderRecipe;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EnderRecipeResult<R extends IEnderRecipe<R, ?>> implements FinishedRecipe {

    private final R recipe;
    private final ResourceLocation id;

    private final List<ICondition> conditions = new ArrayList<>();

    public EnderRecipeResult(R recipe, String modid, String name) {
        this(recipe, new ResourceLocation(modid, recipe.getSerializer().getRegistryName().getPath() + "/" + name));
    }

    public EnderRecipeResult(R recipe, ResourceLocation id) {
        this.recipe = recipe;
        this.id = id;
    }

    public void addCondition(ICondition condition) {
        conditions.add(condition);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
        // Serialize recipe
        recipe.getSerializer().toJson(recipe, json);

        // Stop recipes from loading
        List<String> modDependencies = recipe.getModDependencies();
        for (String modDependency : modDependencies) {
            conditions.add(new ModLoadedCondition(modDependency));
        }

        // Write conditions
        if (!conditions.isEmpty()) {
            JsonArray jsonConditions = new JsonArray();
            for (ICondition condition : conditions) {
                jsonConditions.add(CraftingHelper.serialize(condition));
            }
            json.add("conditions", jsonConditions);
        }
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return recipe.getSerializer();
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
        return null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
        return null;
    }
}