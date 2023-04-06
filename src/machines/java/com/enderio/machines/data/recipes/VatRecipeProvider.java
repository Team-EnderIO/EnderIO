package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.item.misc.EnderiosItem;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.VatRecipe;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class VatRecipeProvider extends EnderRecipeProvider {

    public VatRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        //hootch
        build(
            List.of(
                new VatRecipe.VatInputPair(Ingredient.of(Items.WHEAT_SEEDS), 2f),
                new VatRecipe.VatInputPair(Ingredient.of(Items.PUMPKIN_SEEDS), 1.6f),
                new VatRecipe.VatInputPair(Ingredient.of(Items.MELON_SEEDS), 1.6f),
                new VatRecipe.VatInputPair(Ingredient.of(Items.BEETROOT_SEEDS), 1.4f),
                new VatRecipe.VatInputPair(Ingredient.of(Items.POISONOUS_POTATO), 8f),
                new VatRecipe.VatInputPair(Ingredient.of(Items.POTATO), 4f),
                new VatRecipe.VatInputPair(Ingredient.of(Items.APPLE), 3.5f),
                new VatRecipe.VatInputPair(Ingredient.of(EIOItems.FLOUR.get()), 3f)
            ),
            List.of(
                new VatRecipe.VatInputPair(Ingredient.of(Items.MELON_SLICE), 1f),
                new VatRecipe.VatInputPair(Ingredient.of(Items.SUGAR), 0.5f)
            ),
            Fluids.WATER, EIOFluids.HOOTCH.get(),
            0.5f, 10000, pFinishedRecipeConsumer);

        build(
            List.of(new VatRecipe.VatInputPair(Ingredient.of(Items.REDSTONE), 1)
            ),
            List.of(new VatRecipe.VatInputPair(Ingredient.of(Items.GUNPOWDER), 1)),
            EIOFluids.HOOTCH.get(), EIOFluids.ROCKET_FUEL.get(),
            1f, 10000, pFinishedRecipeConsumer);
    }


    protected void build(List<VatRecipe.VatInputPair> leftInputs, List<VatRecipe.VatInputPair> rightInputs, Fluid inputFluid, Fluid outputFluid,
                float baseConversionRate, int energy, Consumer<FinishedRecipe> finishedRecipeConsumer) {
        finishedRecipeConsumer.accept(new VatRecipeProvider.FinishedVatRecipe(EnderIO.loc("vatting/" + Objects
            .requireNonNull(ForgeRegistries.FLUIDS.getKey(outputFluid))
            .getPath()), leftInputs, rightInputs, inputFluid, outputFluid, baseConversionRate, energy));
    }

    protected static class FinishedVatRecipe extends EnderFinishedRecipe{

        private final List<VatRecipe.VatInputPair> leftInputs;
        private final List<VatRecipe.VatInputPair> rightInputs;
        private final Fluid inputFluid;
        private final Fluid outputFluid;
        private final float baseConversionRate;
        private final int energy;

        public FinishedVatRecipe(ResourceLocation id, List<VatRecipe.VatInputPair> leftInputs, List<VatRecipe.VatInputPair> rightInputs, Fluid inputFluid,
            Fluid outputFluid, float baseConversionRate, int energy) {
            super(id);
            this.leftInputs = leftInputs;
            this.rightInputs = rightInputs;
            this.inputFluid = inputFluid;
            this.outputFluid = outputFluid;
            this.baseConversionRate = baseConversionRate;
            this.energy = energy;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            JsonArray leftItemArray = new JsonArray();
            JsonArray leftMultiplierArray = new JsonArray();

            for (VatRecipe.VatInputPair pair : leftInputs){
                leftItemArray.add(pair.ingredient().toJson());
                leftMultiplierArray.add(pair.multiplier());
            }
            json.add("leftInputs", leftItemArray);
            json.add("leftMultipliers", leftMultiplierArray);

            JsonArray rightItemArray = new JsonArray();
            JsonArray rightMultiplierArray = new JsonArray();

            for (VatRecipe.VatInputPair pair : rightInputs){
                rightItemArray.add(pair.ingredient().toJson());
                rightMultiplierArray.add(pair.multiplier());
            }
            json.add("rightInputs", rightItemArray);
            json.add("rightMultipliers", rightMultiplierArray);

            json.addProperty("inputFluid", Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(inputFluid)).toString());

            json.addProperty("outputFluid", Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(outputFluid)).toString());

            json.addProperty("baseConversionRate", baseConversionRate);
            json.addProperty("energy", energy);
            super.serializeRecipeData(json);
        }

        @Override
        protected Set<String> getModDependencies() {
            //TODO
            return Set.of();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return MachineRecipes.VATTING.serializer().get();
        }
    }
}
