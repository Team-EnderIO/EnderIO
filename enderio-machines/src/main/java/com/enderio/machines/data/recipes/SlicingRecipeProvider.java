package com.enderio.machines.data.recipes;

import com.enderio.base.common.init.EIOItems;
import com.enderio.base.data.recipe.EnderRecipeProvider;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class SlicingRecipeProvider extends EnderRecipeProvider {
    public SlicingRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {
        // TODO: Tormented enderman head

        build(EIOItems.ZOMBIE_ELECTRODE.get(), List.of(
            Ingredient.of(EIOItems.ENERGETIC_ALLOY_INGOT.get()), Ingredient.of(Items.ZOMBIE_HEAD), Ingredient.of(EIOItems.ENERGETIC_ALLOY_INGOT.get()),
            Ingredient.of(EIOItems.SILICON.get()), Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), Ingredient.of(EIOItems.SILICON.get())
        ), 20000, finishedRecipeConsumer);

        build(EIOItems.ZOMBIE_CONTROLLER.get(), List.of(
            Ingredient.of(EIOItems.SOULARIUM_INGOT.get()), Ingredient.of(Items.ZOMBIE_HEAD), Ingredient.of(EIOItems.SOULARIUM_INGOT.get()),
            Ingredient.of(EIOItems.SILICON.get()), Ingredient.of(Items.REDSTONE), Ingredient.of(EIOItems.SILICON.get())
        ), 20000, finishedRecipeConsumer);

        // TODO: Ender resonator

        build(EIOItems.SKELETAL_CONTRACTOR.get(), List.of(
            Ingredient.of(EIOItems.SOULARIUM_INGOT.get()), Ingredient.of(Items.SKELETON_SKULL), Ingredient.of(EIOItems.SOULARIUM_INGOT.get()),
            Ingredient.of(Items.ROTTEN_FLESH), Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), Ingredient.of(Items.ROTTEN_FLESH)
        ), 20000, finishedRecipeConsumer);

        build(EIOItems.GUARDIAN_DIODE.get(), List.of(
            Ingredient.of(EIOItems.ENERGETIC_ALLOY_INGOT.get()), Ingredient.of(Tags.Items.DUSTS_PRISMARINE), Ingredient.of(EIOItems.ENERGETIC_ALLOY_INGOT.get()),
            Ingredient.of(Tags.Items.GEMS_PRISMARINE), Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), Ingredient.of(Tags.Items.GEMS_PRISMARINE)
        ), 20000, finishedRecipeConsumer);
    }

    protected void build(Item output, List<Ingredient> inputs, int energy, Consumer<FinishedRecipe> finishedRecipeConsumer) {
        finishedRecipeConsumer.accept(new FinishedSlicingRecipe(EIOMachines.loc("slicing/" + output.getRegistryName().getPath()), output, inputs, energy));
    }

    protected static class FinishedSlicingRecipe extends EnderFinishedRecipe {

        private final Item output;
        private final List<Ingredient> inputs;
        private final int energy;

        public FinishedSlicingRecipe(ResourceLocation id, Item output, List<Ingredient> inputs, int energy) {
            super(id);
            this.output = output;
            this.inputs = inputs;
            this.energy = energy;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("output", output.getRegistryName().toString());

            JsonArray inputsArray = new JsonArray();
            for (Ingredient input : inputs) {
                inputsArray.add(input.toJson());
            }
            json.add("inputs", inputsArray);

            json.addProperty("energy", energy);

            super.serializeRecipeData(json);
        }

        @Override
        protected Set<String> getModDependencies() {
            Set<String> mods = new HashSet<>();
            mods.add(ForgeRegistries.ITEMS.getKey(output).getNamespace());
            inputs.stream().map(ing -> Arrays.stream(ing.getItems()).map(item -> mods.add(ForgeRegistries.ITEMS.getKey(item.getItem()).getNamespace())));
            return mods;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return MachineRecipes.Serializer.SLICING.get();
        }
    }
}
