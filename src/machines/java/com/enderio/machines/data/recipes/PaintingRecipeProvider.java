package com.enderio.machines.data.recipes;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.core.data.recipes.EnderRecipeProvider;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PaintingRecipeProvider extends EnderRecipeProvider {

    public PaintingRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        build(EIOBlocks.PAINTED_FENCE, Ingredient.of(ItemTags.WOODEN_FENCES), recipeOutput);
        build(EIOBlocks.PAINTED_FENCE_GATE, Ingredient.of(ItemTags.FENCE_GATES), recipeOutput);
        build(EIOBlocks.PAINTED_SAND, Ingredient.of(ItemTags.SAND), recipeOutput);
        build(EIOBlocks.PAINTED_STAIRS, Ingredient.of(ItemTags.WOODEN_STAIRS), recipeOutput);
        build(EIOBlocks.PAINTED_CRAFTING_TABLE, Ingredient.of(Items.CRAFTING_TABLE), recipeOutput);
        build(EIOBlocks.PAINTED_REDSTONE_BLOCK, Ingredient.of(Items.REDSTONE_BLOCK), recipeOutput);
        build(EIOBlocks.PAINTED_TRAPDOOR, Ingredient.of(ItemTags.WOODEN_TRAPDOORS), recipeOutput);
        build(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE, Ingredient.of(ItemTags.WOODEN_PRESSURE_PLATES), recipeOutput);
        build(EIOBlocks.PAINTED_SLAB, Ingredient.of(ItemTags.WOODEN_SLABS), recipeOutput);
        build(EIOBlocks.PAINTED_GLOWSTONE, Ingredient.of(Items.GLOWSTONE), recipeOutput);
        //Painted block to painted block
        build(EIOBlocks.PAINTED_FENCE, Ingredient.of(EIOBlocks.PAINTED_FENCE), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_FENCE_GATE, Ingredient.of(EIOBlocks.PAINTED_FENCE_GATE), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_SAND, Ingredient.of(EIOBlocks.PAINTED_SAND), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_STAIRS, Ingredient.of(EIOBlocks.PAINTED_STAIRS), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_CRAFTING_TABLE, Ingredient.of(EIOBlocks.PAINTED_CRAFTING_TABLE), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_REDSTONE_BLOCK, Ingredient.of(EIOBlocks.PAINTED_REDSTONE_BLOCK), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_TRAPDOOR, Ingredient.of(EIOBlocks.PAINTED_TRAPDOOR), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE, Ingredient.of(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_SLAB, Ingredient.of(EIOBlocks.PAINTED_SLAB), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_GLOWSTONE, Ingredient.of(EIOBlocks.PAINTED_GLOWSTONE), "_frompainted", recipeOutput);
    }


    protected void build(ItemLike output, Ingredient input, RecipeOutput recipeOutput) {
        build(output, input, "", recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, String suffix, RecipeOutput recipeOutput) {
        recipeOutput.accept(new FinishedPaintingRecipe(EnderIO.loc("painting/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath() + suffix), input, output.asItem()));
    }

    protected static class FinishedPaintingRecipe extends EnderFinishedRecipe {
        private final Ingredient input;
        private final Item output;

        public FinishedPaintingRecipe(ResourceLocation id, Ingredient input, Item output) {
            super(id);
            this.input = input;
            this.output = output;
        }

        @Override
        protected Set<String> getModDependencies() {
            return Set.of(BuiltInRegistries.ITEM.getKey(output).getNamespace());
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("input", input.toJson(false));
            json.addProperty("output", BuiltInRegistries.ITEM.getKey(output).toString());

            super.serializeRecipeData(json);
        }

        @Override
        public RecipeSerializer<?> type() {
            return MachineRecipes.PAINTING.serializer().get();
        }
    }
}
