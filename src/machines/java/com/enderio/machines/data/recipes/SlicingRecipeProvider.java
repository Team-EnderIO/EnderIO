package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.recipe.RecipeDataUtil;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.Tags;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SlicingRecipeProvider extends EnderRecipeProvider {

    public SlicingRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // TODO: Tormented enderman head

        build(EIOItems.ZOMBIE_ELECTRODE.get(), List.of(
            Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY), Ingredient.of(Items.ZOMBIE_HEAD), Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY),
            Ingredient.of(EIOTags.Items.SILICON), Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), Ingredient.of(EIOTags.Items.SILICON)
        ), 20000, recipeOutput);

        build(EIOItems.Z_LOGIC_CONTROLLER.get(), List.of(
            Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(Items.ZOMBIE_HEAD), Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM),
            Ingredient.of(EIOTags.Items.SILICON), Ingredient.of(Items.REDSTONE), Ingredient.of(EIOTags.Items.SILICON)
        ), 20000, recipeOutput);

        // TODO: Ender resonator

        build(EIOItems.SKELETAL_CONTRACTOR.get(), List.of(
            Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(Items.SKELETON_SKULL), Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM),
            Ingredient.of(Items.ROTTEN_FLESH), Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), Ingredient.of(Items.ROTTEN_FLESH)
        ), 20000, recipeOutput);

        build(EIOItems.GUARDIAN_DIODE.get(), List.of(
            Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY), Ingredient.of(Tags.Items.DUSTS_PRISMARINE), Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY),
            Ingredient.of(Tags.Items.GEMS_PRISMARINE), Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), Ingredient.of(Tags.Items.GEMS_PRISMARINE)
        ), 20000, recipeOutput);

        build(EIOItems.ENDER_RESONATOR.get(), List.of(
            Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(EIOBlocks.ENDERMAN_HEAD), Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), //TODO EnderSkull
            Ingredient.of(EIOTags.Items.SILICON), Ingredient.of(EIOItems.VIBRANT_ALLOY_INGOT.get()), Ingredient.of(EIOTags.Items.SILICON)
        ), 20000, recipeOutput);

    }

    protected void build(Item output, List<Ingredient> inputs, int energy, RecipeOutput recipeOutput) {
        recipeOutput.accept(new FinishedSlicingRecipe(EnderIO.loc("slicing/" + BuiltInRegistries.ITEM.getKey(output).getPath()), output, inputs, energy));
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
            json.addProperty("output", BuiltInRegistries.ITEM.getKey(output).toString());

            JsonArray inputsArray = new JsonArray();
            for (Ingredient input : inputs) {
                inputsArray.add(input.toJson(false));
            }
            json.add("inputs", inputsArray);

            json.addProperty("energy", energy);

            super.serializeRecipeData(json);
        }

        @Override
        protected Set<String> getModDependencies() {
            Set<String> mods = new HashSet<>(RecipeDataUtil.getIngredientsModIds(inputs));
            mods.add(BuiltInRegistries.ITEM.getKey(output).getNamespace());
            return mods;
        }

        @Override
        public RecipeSerializer<?> type() {
            return MachineRecipes.SLICING.serializer().get();
        }
    }
}
