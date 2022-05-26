package com.enderio.machines.common.recipe.serializer;

import com.enderio.api.recipe.AlloySmeltingRecipe;
import com.enderio.api.recipe.CountedIngredient;
import com.enderio.api.recipe.DataGenSerializer;
import com.enderio.machines.common.recipe.AlloySmeltingRecipeImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.ArrayList;
import java.util.List;

public class AlloySmeltingRecipeSerializer extends DataGenSerializer<AlloySmeltingRecipe, Container> {

    @Override
    public AlloySmeltingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
        // Load ingredients
        JsonArray jsonInputs = pSerializedRecipe.getAsJsonArray("inputs");
        List<CountedIngredient> inputs = new ArrayList<>(jsonInputs.size());
        for (int i = 0; i < jsonInputs.size(); i++) {
            inputs.add(i, CountedIngredient.fromJson(jsonInputs.get(i).getAsJsonObject()));
        }

        // Load result, energy and experience.
        ItemStack result = CraftingHelper.getItemStack(pSerializedRecipe.getAsJsonObject("result"), false);
        int energy = pSerializedRecipe.get("energy").getAsInt();
        float experience = pSerializedRecipe.get("experience").getAsInt();
        return new AlloySmeltingRecipeImpl(pRecipeId, inputs, result, energy, experience);
    }

    @Override
    public void toJson(AlloySmeltingRecipe recipe, JsonObject json) {
        List<CountedIngredient> recipeInputs = recipe.getInputs();

        JsonArray inputs = new JsonArray(recipeInputs.size());
        recipeInputs.forEach(ing -> inputs.add(ing.toJson()));

        json.add("inputs", inputs);

        ItemStack recipeResult = recipe.getResultItem();

        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("item", Registry.ITEM.getKey(recipeResult.getItem()).toString());
        if (recipeResult.getCount() > 1) {
            jsonobject.addProperty("count", recipeResult.getCount());
        }

        json.add("result", jsonobject);

        json.addProperty("energy", recipe.getEnergyCost());
        json.addProperty("experience", recipe.getExperience());
    }

    @Override
    public AlloySmeltingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        List<CountedIngredient> ingredients = pBuffer.readList(CountedIngredient::fromNetwork);
        ItemStack result = pBuffer.readItem();
        int energy = pBuffer.readInt();
        float experience = pBuffer.readFloat();
        return new AlloySmeltingRecipeImpl(pRecipeId, ingredients, result, energy, experience);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, AlloySmeltingRecipe pRecipe) {
        pBuffer.writeCollection(pRecipe.getInputs(), (buf, ing) -> ing.toNetwork(buf));
        pBuffer.writeItem(pRecipe.getResultItem());
        pBuffer.writeInt(pRecipe.getEnergyCost());
        pBuffer.writeFloat(pRecipe.getEnergyCost());
    }
}
