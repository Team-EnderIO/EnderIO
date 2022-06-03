package com.enderio.machines.common.recipe;

import com.enderio.api.machines.recipes.IAlloySmeltingRecipe;
import com.enderio.api.recipe.CountedIngredient;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AlloySmeltingRecipe implements IAlloySmeltingRecipe {

    private final ResourceLocation id;
    private final List<CountedIngredient> inputs;
    private final ItemStack output;
    private final int energy;
    private final float experience;

    public AlloySmeltingRecipe(ResourceLocation id, List<CountedIngredient> inputs, ItemStack output, int energy, float experience) {
        this.id = id;
        this.inputs = inputs;
        this.output = output;
        this.energy = energy;
        this.experience = experience;
    }

    @Override
    public List<CountedIngredient> getInputs() {
        return inputs;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public int getEnergyCost(Container container) {
        return energy;
    }

    @Override
    public float getExperience() {
        return experience;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.Serializer.ALLOY_SMELTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.Types.ALLOY_SMELTING;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<AlloySmeltingRecipe> {

        @Override
        public AlloySmeltingRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            // Load ingredients
            JsonArray jsonInputs = serializedRecipe.getAsJsonArray("inputs");
            List<CountedIngredient> inputs = new ArrayList<>(jsonInputs.size());
            for (int i = 0; i < jsonInputs.size(); i++) {
                inputs.add(i, CountedIngredient.fromJson(jsonInputs.get(i).getAsJsonObject()));
            }

            // Load result, energy and experience.
            ItemStack result = CraftingHelper.getItemStack(serializedRecipe.getAsJsonObject("result"), false);
            int energy = serializedRecipe.get("energy").getAsInt();
            float experience = serializedRecipe.get("experience").getAsInt();
            return new AlloySmeltingRecipe(recipeId, inputs, result, energy, experience);
        }

        @Nullable
        @Override
        public AlloySmeltingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            List<CountedIngredient> ingredients = buffer.readList(CountedIngredient::fromNetwork);
            ItemStack result = buffer.readItem();
            int energy = buffer.readInt();
            float experience = buffer.readFloat();
            return new AlloySmeltingRecipe(recipeId, ingredients, result, energy, experience);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AlloySmeltingRecipe recipe) {
            buffer.writeCollection(recipe.inputs, (buf, ing) -> ing.toNetwork(buf));
            buffer.writeItem(recipe.output);
            buffer.writeInt(recipe.energy);
            buffer.writeFloat(recipe.experience);
        }
    }
}
