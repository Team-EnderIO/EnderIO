package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.recipe.RecipeDataUtil;
import com.enderio.core.common.util.JsonUtil;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class SlicingRecipeProvider extends EnderRecipeProvider {

    public SlicingRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {
        // TODO: Tormented enderman head

        build(EIOItems.ZOMBIE_ELECTRODE.get(), List.of(
            Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY), Ingredient.of(Items.ZOMBIE_HEAD), Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY),
            Ingredient.of(EIOTags.Items.SILICON), Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), Ingredient.of(EIOTags.Items.SILICON)
        ), 20000, finishedRecipeConsumer);

        build(EIOItems.Z_LOGIC_CONTROLLER.get(), List.of(
            Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(Items.ZOMBIE_HEAD), Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM),
            Ingredient.of(EIOTags.Items.SILICON), Ingredient.of(Items.REDSTONE), Ingredient.of(EIOTags.Items.SILICON)
        ), 20000, finishedRecipeConsumer);

        // TODO: Ender resonator

        build(EIOItems.SKELETAL_CONTRACTOR.get(), List.of(
            Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(Items.SKELETON_SKULL), Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM),
            Ingredient.of(Items.ROTTEN_FLESH), Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), Ingredient.of(Items.ROTTEN_FLESH)
        ), 20000, finishedRecipeConsumer);

        build(EIOItems.GUARDIAN_DIODE.get(), List.of(
            Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY), Ingredient.of(Tags.Items.DUSTS_PRISMARINE), Ingredient.of(EIOTags.Items.INGOTS_ENERGETIC_ALLOY),
            Ingredient.of(Tags.Items.GEMS_PRISMARINE), Ingredient.of(EIOItems.BASIC_CAPACITOR.get()), Ingredient.of(Tags.Items.GEMS_PRISMARINE)
        ), 20000, finishedRecipeConsumer);

        build(EIOItems.ENDER_RESONATOR.get(), List.of(
            Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), Ingredient.of(EIOBlocks.ENDERMAN_HEAD), Ingredient.of(EIOTags.Items.INGOTS_SOULARIUM), //TODO EnderSkull
            Ingredient.of(EIOTags.Items.SILICON), Ingredient.of(EIOItems.VIBRANT_ALLOY_INGOT.get()), Ingredient.of(EIOTags.Items.SILICON)
        ), 20000, finishedRecipeConsumer);

    }

    protected void build(Item output, List<Ingredient> inputs, int energy, Consumer<FinishedRecipe> finishedRecipeConsumer) {
        finishedRecipeConsumer.accept(new FinishedSlicingRecipe(EnderIO.loc("slicing/" + ForgeRegistries.ITEMS.getKey(output).getPath()), output.getDefaultInstance(), inputs, energy));
    }

    protected static class FinishedSlicingRecipe extends EnderFinishedRecipe {

        private final ItemStack output;
        private final List<Ingredient> inputs;
        private final int energy;

        public FinishedSlicingRecipe(ResourceLocation id, ItemStack output, List<Ingredient> inputs, int energy) {
            super(id);
            this.output = output;
            this.inputs = inputs;
            this.energy = energy;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("output", JsonUtil.serializeItemStackWithoutNBT(output));

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
            Set<String> mods = new HashSet<>(RecipeDataUtil.getIngredientsModIds(inputs));
            mods.add(ForgeRegistries.ITEMS.getKey(output.getItem()).getNamespace());
            return mods;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return MachineRecipes.SLICING.serializer().get();
        }
    }
}
