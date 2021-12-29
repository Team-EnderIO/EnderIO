package com.enderio.machines.data.recipe;

import javax.annotation.Nullable;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.recipe.MachineRecipe;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

import java.util.ArrayList;
import java.util.List;

public class RecipeResult<T extends MachineRecipe<T, ?>> implements FinishedRecipe {
    private final T recipe;
    private final ResourceLocation id;

    private final List<ICondition> conditions = new ArrayList<>();

    public RecipeResult(T recipe, String name) {
        this(recipe, new ResourceLocation(EIOMachines.MODID, recipe.getSerializer().getRegistryName().getPath() + "/" +name));
    }
    
    public RecipeResult(T recipe, ResourceLocation id) {
        this.recipe = recipe;
        this.id = id;
    }

    public void addCondition(ICondition condition) {
        conditions.add(condition);
    }

    @Override
    public void serializeRecipeData(JsonObject pJson) {
        recipe.getSerializer().toJson(recipe, pJson);

        List<String> modDependencies = recipe.getModDependencies();

        for (String modDependency : modDependencies) {
            conditions.add(new ModLoadedCondition(modDependency));
        }

        if (!conditions.isEmpty()) {
            JsonArray jsonConditions = new JsonArray();
            for (ICondition condition : conditions) {
                jsonConditions.add(CraftingHelper.serialize(condition));
            }
            pJson.add("conditions", jsonConditions);
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
