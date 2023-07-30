package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public int getBaseEnergyCost() {
        return energy;
    }

    @Override
    public List<OutputStack> craft(Container container, RegistryAccess registryAccess) {
        return getResultStacks(registryAccess);
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(new ItemStack(output, 1)));
    }

    public List<Ingredient> getInputs() {
        return List.copyOf(inputs);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, inputs.toArray(new Ingredient[0]));
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
        return MachineRecipes.SLICING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.SLICING.type().get();
    }

    public static class Serializer implements RecipeSerializer<SlicingRecipe> {

        @Override
        public SlicingRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            ResourceLocation id = new ResourceLocation(serializedRecipe.get("output").getAsString());
            Item output = ForgeRegistries.ITEMS.getValue(id);
            if (output == null) {
                EnderIO.LOGGER.error("Slicing recipe {} tried to load missing item {}", recipeId, id);
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
                EnderIO.LOGGER.error("Error reading slicing recipe from packet.", ex);
                throw ex;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SlicingRecipe recipe) {
            try {
                buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(recipe.output)));
                buffer.writeCollection(recipe.inputs, (buf, ing) -> ing.toNetwork(buf));
                buffer.writeInt(recipe.energy);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing slicing recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
