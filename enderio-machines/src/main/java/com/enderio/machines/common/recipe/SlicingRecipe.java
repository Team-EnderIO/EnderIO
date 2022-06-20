package com.enderio.machines.common.recipe;

import com.enderio.api.machines.recipes.MachineRecipe;
import com.enderio.api.machines.recipes.OutputStack;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SlicingRecipe implements MachineRecipe<Container> {
    private final ResourceLocation id;
    private final Item output;
    private final List<Ingredient> inputs;
    private final int energy;

    public SlicingRecipe(ResourceLocation id, Item output, List<Ingredient> inputs, int energy) {
        this.id = id;
        this.output = output;
        this.inputs = inputs;
        this.energy = energy;
    }

    @Override
    public int getEnergyCost(Container container) {
        return energy;
    }

    @Override
    public List<OutputStack> craft(Container container) {
        return getResultStacks();
    }

    @Override
    public List<OutputStack> getResultStacks() {
        return List.of(OutputStack.of(new ItemStack(output, 1)));
    }

    @Override
    public boolean matches(Container container, Level level) {
        for (int i = 0; i < inputs.size(); i++) {
            if (!inputs.get(i).test(container.getItem(i)))
                return false;
        }
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.Serializer.SLICING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.Types.SLICING;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<SlicingRecipe> {

        @Override
        public SlicingRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            ResourceLocation id = new ResourceLocation(serializedRecipe.get("output").getAsString());
            Item output = ForgeRegistries.ITEMS.getValue(id);
            if (output == null) {
                EIOMachines.LOGGER.error("Slicing recipe {} tried to load missing item {}", recipeId, id);
                throw new ResourceLocationException("Item not found for slicing recipe.");
            }

            List<Ingredient> inputs = new ArrayList<>();
            JsonArray inputsJson = serializedRecipe.getAsJsonArray("inputs");
            for (JsonElement itemJson : inputsJson) {
                inputs.add(Ingredient.fromJson(itemJson));
            }

            int energy = serializedRecipe.get("energy").getAsInt();

            return new SlicingRecipe(recipeId, output, inputs, energy);
        }

        @Nullable
        @Override
        public SlicingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            try {
                ResourceLocation outputId = buffer.readResourceLocation();
                Item output = ForgeRegistries.ITEMS.getValue(outputId);
                if (output == null) {
                    throw new ResourceLocationException("The output of recipe " + recipeId + " does not exist.");
                }

                List<Ingredient> inputs = buffer.readCollection(ArrayList::new, Ingredient::fromNetwork);

                int energy = buffer.readInt();

                return new SlicingRecipe(recipeId, output, inputs, energy);
            } catch (Exception ex) {
                EIOMachines.LOGGER.error("Error reading slicing recipe from packet.", ex);
                throw ex;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SlicingRecipe recipe) {
            try {
                buffer.writeResourceLocation(recipe.output.getRegistryName());
                buffer.writeCollection(recipe.inputs, (buf, ing) -> ing.toNetwork(buf));
                buffer.writeInt(recipe.energy);
            } catch (Exception ex) {
                EIOMachines.LOGGER.error("Error writing slicing recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
