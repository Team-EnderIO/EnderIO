package com.enderio.machines.common.recipe;

import com.enderio.base.common.recipe.DataGenSerializer;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;

public class AlloySmeltingRecipe extends MachineRecipe<AlloySmeltingRecipe, Container> {
    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients; // TODO: Custom "Ingredient" class supporting counts
    private final ItemStack result;
    private final int energy;
    private final float experience;

    public AlloySmeltingRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack result, int energy, float experience) {
        if (ingredients.size() > 3) {
            throw new IllegalArgumentException("Developer tried to create an invalid alloy smelting recipe!");
        }

        this.id = id;
        this.ingredients = ingredients;
        this.result = result;
        this.energy = energy;
        this.experience = experience;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public int getEnergyCost() {
        return energy;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        // TODO: Test
        boolean[] matchArray = new boolean[3]; // Used to ensure there are blank slots left. // TODO: I want to get rid of it
        int matches = 0;

        for (int i = 0; i < 3; i++) {
            if (matchArray[i])
                continue;

            for (int j = 0; j < 3; j++) {
                if (j < ingredients.size()) {
                    if (ingredients.get(j).test(pContainer.getItem(i))) {
                        matchArray[i] = true;
                        matches++;
                    }
                } else if (pContainer.getItem(i).isEmpty())
                    matchArray[i] = true;
            }

            for (Ingredient ingredient : ingredients) {
                if (ingredient.test(pContainer.getItem(i)))
                    matchArray[i] = true;
                else if (ingredient == Ingredient.EMPTY && pContainer.getItem(i).isEmpty())
                    matchArray[i] = true;
            }
        }

        return matches == ingredients.size() && matchArray[0] && matchArray[1] && matchArray[2];
    }

    @Override
    public ItemStack assemble(Container pContainer) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public DataGenSerializer<AlloySmeltingRecipe, Container> getSerializer() {
        return MachineRecipes.Serializer.ALLOY_SMELTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.Types.ALLOY_SMELTING;
    }

    public static class Serializer extends DataGenSerializer<AlloySmeltingRecipe, Container> {

        @Override
        public AlloySmeltingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            // Load ingredients
            JsonArray jsonIngredients = pSerializedRecipe.getAsJsonArray("ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.withSize(jsonIngredients.size(), Ingredient.EMPTY);
            for (int i = 0; i < jsonIngredients.size(); i++) {
                ingredients.set(i, Ingredient.fromJson(jsonIngredients.get(i)));
            }

            // Load result, energy and experience.
            ItemStack result = CraftingHelper.getItemStack(pSerializedRecipe.getAsJsonObject("result"), false);
            int energy = pSerializedRecipe.get("energy").getAsInt();
            float experience = pSerializedRecipe.get("experience").getAsInt();
            return new AlloySmeltingRecipe(pRecipeId, ingredients, result, energy, experience);
        }

        @Override
        public AlloySmeltingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int ingredientCount = pBuffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
            for (int i = 0; i < ingredientCount; i++) {
                ingredients.set(i, Ingredient.fromNetwork(pBuffer));
            }

            ItemStack result = pBuffer.readItem();
            int energy = pBuffer.readInt();
            float experience = pBuffer.readFloat();
            return new AlloySmeltingRecipe(pRecipeId, ingredients, result, energy, experience);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, AlloySmeltingRecipe pRecipe) {
            pBuffer.writeCollection(pRecipe.ingredients, (buf, ing) -> ing.toNetwork(buf));
            pBuffer.writeItem(pRecipe.result);
            pBuffer.writeInt(pRecipe.energy);
            pBuffer.writeFloat(pRecipe.experience);
        }

        @Override
        public void toJson(AlloySmeltingRecipe recipe, JsonObject json) {
            JsonArray ingredients = new JsonArray(recipe.ingredients.size());
            recipe.ingredients.forEach(ing -> ingredients.add(ing.toJson()));

            json.add("ingredients", ingredients);

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", Registry.ITEM.getKey(recipe.result.getItem()).toString());
            if (recipe.result.getCount() > 1) {
                jsonobject.addProperty("count", recipe.result.getCount());
            }

            json.add("result", jsonobject);

            json.addProperty("energy", recipe.energy);
            json.addProperty("experience", recipe.experience);
        }
    }
}
