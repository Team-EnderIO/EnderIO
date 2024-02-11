package com.enderio.base.data.recipe;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class GrindingBallRecipeProvider extends EnderRecipeProvider {

    public GrindingBallRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        build(Items.FLINT, 1.2F, 1.25F, 0.85F, 24000, recipeOutput);
        build(EIOItems.DARK_STEEL_BALL.get(), 1.35F, 2.00F, 0.7F, 125000, recipeOutput);
        build(EIOItems.COPPER_ALLOY_BALL.get(), 1.2F, 1.65F, 0.8F, 40000, recipeOutput);
        build(EIOItems.ENERGETIC_ALLOY_BALL.get(), 1.6F, 1.1F, 1.1F, 80000, recipeOutput);
        build(EIOItems.VIBRANT_ALLOY_BALL.get(), 1.75F, 1.35F, 1.13F, 80000, recipeOutput);
        build(EIOItems.REDSTONE_ALLOY_BALL.get(), 1.00F, 1.00F, 0.35F, 30000, recipeOutput);
        build(EIOItems.CONDUCTIVE_ALLOY_BALL.get(), 1.35F, 1.00F, 1.0F, 40000, recipeOutput);
        build(EIOItems.PULSATING_ALLOY_BALL.get(), 1.00F, 1.85F, 1.0F, 100000, recipeOutput);
        build(EIOItems.SOULARIUM_BALL.get(), 1.2F, 2.15F, 0.9F, 80000, recipeOutput);
        build(EIOItems.END_STEEL_BALL.get(), 1.4F, 2.4F, 0.7F, 75000, recipeOutput);
    }

    protected void build(Item item, float grinding, float chance, float power, int durability, RecipeOutput recipeOutput) {
        recipeOutput.accept(new FinishedGrindingBall(EnderIO.loc("grindingball/" + BuiltInRegistries.ITEM.getKey(item).getPath()), item, grinding, chance, power, durability));
    }

    protected static class FinishedGrindingBall extends EnderFinishedRecipe {

        private final Item item;
        private final float mainOutput;
        private final float bonusOutput;
        private final float powerUse;
        private final int durability;

        public FinishedGrindingBall(ResourceLocation id, Item item, float mainOutput, float bonusOutput, float powerUse, int durability) {
            super(id);
            this.item = item;
            this.mainOutput = mainOutput;
            this.bonusOutput = bonusOutput;
            this.powerUse = powerUse;
            this.durability = durability;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("item", BuiltInRegistries.ITEM.getKey(item).toString());
            json.addProperty("grinding", mainOutput);
            json.addProperty("chance", bonusOutput);
            json.addProperty("power", powerUse);
            json.addProperty("durability", durability);

            super.serializeRecipeData(json);
        }

        @Override
        protected Set<String> getModDependencies() {
            return Set.of(BuiltInRegistries.ITEM.getKey(item).getNamespace());
        }

        @Override
        public RecipeSerializer<?> type() {
            return EIORecipes.GRINDING_BALL.serializer().get();
        }
    }
}
