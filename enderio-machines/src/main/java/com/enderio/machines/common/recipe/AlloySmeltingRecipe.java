package com.enderio.machines.common.recipe;

import com.enderio.base.common.recipe.DataGenSerializer;
import com.enderio.base.common.recipe.EnderIngredient;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlloySmeltingRecipe implements IMachineRecipe<AlloySmeltingRecipe, Container> {
    private final ResourceLocation id;
    private final List<EnderIngredient> inputs;
    private final ItemStack result;
    private final int energy;
    private final float experience;

    public AlloySmeltingRecipe(ResourceLocation id, List<EnderIngredient> inputs, ItemStack result, int energy, float experience) {
        if (inputs.size() > 3) {
            throw new IllegalArgumentException("Tried to create an invalid alloy smelting recipe!");
        }

        this.id = id;
        this.inputs = inputs;
        this.result = result;
        this.energy = energy;
        this.experience = experience;
    }

    // TODO: Need a better solution to this.
    public List<EnderIngredient> getInputs() {
        return inputs;
    }

    public ItemStack consumeInput(ItemStack input) {
        // We allow empty slots
        if (input.isEmpty())
            return input;

        // Try to work out which ingredient this is
        for (EnderIngredient ingredient : inputs) {
            if (ingredient.test(input)) {
                input.shrink(ingredient.count());
                return input;
            }
        }

        throw new RuntimeException("Tried to consume an invalid input. A recipe match check must not have been performed!");
    }

    @Override
    public List<List<ItemStack>> getAllInputs() {
        List<List<ItemStack>> inputs = new ArrayList<>();
        for (EnderIngredient ingredient : this.inputs) {
            inputs.add(Arrays.stream(ingredient.getItems()).toList());
        }
        return inputs;
    }

    @Override
    public List<ItemStack> getAllOutputs() {
        return List.of(result);
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
                if (j < inputs.size()) {
                    if (inputs.get(j).test(pContainer.getItem(i))) {
                        matchArray[i] = true;
                        matches++;
                    }
                } else if (pContainer.getItem(i).isEmpty())
                    matchArray[i] = true;
            }

            for (EnderIngredient ingredient : inputs) {
                if (ingredient.test(pContainer.getItem(i)))
                    matchArray[i] = true;
                else if (ingredient == EnderIngredient.EMPTY && pContainer.getItem(i).isEmpty())
                    matchArray[i] = true;
            }
        }

        return matches == inputs.size() && matchArray[0] && matchArray[1] && matchArray[2];
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
        return result.copy();
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
            JsonArray jsonInputs = pSerializedRecipe.getAsJsonArray("inputs");
            List<EnderIngredient> inputs = new ArrayList<>(jsonInputs.size());
            for (int i = 0; i < jsonInputs.size(); i++) {
                inputs.add(i, EnderIngredient.fromJson(jsonInputs.get(i).getAsJsonObject()));
            }

            // Load result, energy and experience.
            ItemStack result = CraftingHelper.getItemStack(pSerializedRecipe.getAsJsonObject("result"), false);
            int energy = pSerializedRecipe.get("energy").getAsInt();
            float experience = pSerializedRecipe.get("experience").getAsInt();
            return new AlloySmeltingRecipe(pRecipeId, inputs, result, energy, experience);
        }

        @Override
        public void toJson(AlloySmeltingRecipe recipe, JsonObject json) {
            JsonArray inputs = new JsonArray(recipe.inputs.size());
            recipe.inputs.forEach(ing -> inputs.add(ing.toJson()));

            json.add("inputs", inputs);

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", Registry.ITEM.getKey(recipe.result.getItem()).toString());
            if (recipe.result.getCount() > 1) {
                jsonobject.addProperty("count", recipe.result.getCount());
            }

            json.add("result", jsonobject);

            json.addProperty("energy", recipe.energy);
            json.addProperty("experience", recipe.experience);
        }

        @Override
        public AlloySmeltingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            List<EnderIngredient> ingredients = pBuffer.readList(EnderIngredient::fromNetwork);
            ItemStack result = pBuffer.readItem();
            int energy = pBuffer.readInt();
            float experience = pBuffer.readFloat();
            return new AlloySmeltingRecipe(pRecipeId, ingredients, result, energy, experience);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, AlloySmeltingRecipe pRecipe) {
            pBuffer.writeCollection(pRecipe.inputs, (buf, ing) -> ing.toNetwork(buf));
            pBuffer.writeItem(pRecipe.result);
            pBuffer.writeInt(pRecipe.energy);
            pBuffer.writeFloat(pRecipe.experience);
        }
    }
}