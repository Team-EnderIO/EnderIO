package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.painting.PaintingRecipe;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class PaintingRecipeProvider extends SubRecipeProvider {

    private HolderLookup.RegistryLookup<Item> items;

    protected Ingredient ingredientFromTag(TagKey<Item> tag) {
        return Ingredient.of(this.items.getOrThrow(tag));
    }

    @Override
    public void buildRecipes(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        this.items = registries.lookupOrThrow(Registries.ITEM);

        build(EIOBlocks.PAINTED_FENCE, ingredientFromTag(ItemTags.WOODEN_FENCES), recipeOutput);
        build(EIOBlocks.PAINTED_FENCE_GATE, ingredientFromTag(ItemTags.FENCE_GATES), recipeOutput);
        build(EIOBlocks.PAINTED_SAND, ingredientFromTag(ItemTags.SAND), recipeOutput);
        build(EIOBlocks.PAINTED_STAIRS, ingredientFromTag(ItemTags.WOODEN_STAIRS), recipeOutput);
        build(EIOBlocks.PAINTED_CRAFTING_TABLE, Ingredient.of(Items.CRAFTING_TABLE), recipeOutput);
        build(EIOBlocks.PAINTED_REDSTONE_BLOCK, Ingredient.of(Items.REDSTONE_BLOCK), recipeOutput);
        build(EIOBlocks.PAINTED_TRAPDOOR, ingredientFromTag(ItemTags.WOODEN_TRAPDOORS), recipeOutput);
        build(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE, ingredientFromTag(ItemTags.WOODEN_PRESSURE_PLATES), recipeOutput);
        build(EIOBlocks.PAINTED_SLAB, ingredientFromTag(ItemTags.WOODEN_SLABS), recipeOutput);
        build(EIOBlocks.PAINTED_WALL, ingredientFromTag(ItemTags.WALLS), recipeOutput);
        build(EIOBlocks.PAINTED_GLOWSTONE, Ingredient.of(Items.GLOWSTONE), recipeOutput);
        build(EIOBlocks.PAINTED_TRAVEL_ANCHOR, Ingredient.of(EIOBlocks.TRAVEL_ANCHOR), recipeOutput);
        // Painted block to painted block
        build(EIOBlocks.PAINTED_FENCE, Ingredient.of(EIOBlocks.PAINTED_FENCE), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_FENCE_GATE, Ingredient.of(EIOBlocks.PAINTED_FENCE_GATE), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_SAND, Ingredient.of(EIOBlocks.PAINTED_SAND), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_STAIRS, Ingredient.of(EIOBlocks.PAINTED_STAIRS), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_CRAFTING_TABLE, Ingredient.of(EIOBlocks.PAINTED_CRAFTING_TABLE), "_frompainted",
                recipeOutput);
        build(EIOBlocks.PAINTED_REDSTONE_BLOCK, Ingredient.of(EIOBlocks.PAINTED_REDSTONE_BLOCK), "_frompainted",
                recipeOutput);
        build(EIOBlocks.PAINTED_TRAPDOOR, Ingredient.of(EIOBlocks.PAINTED_TRAPDOOR), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE, Ingredient.of(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE),
                "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_SLAB, Ingredient.of(EIOBlocks.PAINTED_SLAB), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_GLOWSTONE, Ingredient.of(EIOBlocks.PAINTED_GLOWSTONE), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_WALL, Ingredient.of(EIOBlocks.PAINTED_WALL), "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_TRAVEL_ANCHOR, Ingredient.of(EIOBlocks.PAINTED_TRAVEL_ANCHOR), "_frompainted",
                recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, RecipeOutput recipeOutput) {
        build(output, input, "", recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, String suffix, RecipeOutput recipeOutput) {
        recipeOutput.accept(ResourceKey.create(Registries.RECIPE,
                EnderIO.rl("painting/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath() + suffix)),
                new PaintingRecipe(input, new ItemStack(output)), null);
    }

}
