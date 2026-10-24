package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.painting.PaintingRecipe;
import com.enderio.enderio.content.paint.block.PaintedWallBlock;
import com.enderio.enderio.content.travel.travel_anchor.PaintedTravelAnchorBlock;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredBlock;

public class PaintingRecipeProvider extends SubRecipeProvider {

    private HolderGetter<Item> items;

    protected Ingredient ingredientFromTag(TagKey<Item> tag) {
        return Ingredient.of(this.items.getOrThrow(tag));
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        this.items = recipeOutput.lookup(Registries.ITEM);

        build(EIOBlocks.PAINTED_FENCE, ItemTags.WOODEN_FENCES, recipeOutput);
        build(EIOBlocks.PAINTED_FENCE_GATE, ItemTags.FENCE_GATES, recipeOutput);
        build(EIOBlocks.PAINTED_SAND, ItemTags.SAND, recipeOutput);
        build(EIOBlocks.PAINTED_STAIRS, ItemTags.WOODEN_STAIRS, recipeOutput);
        build(EIOBlocks.PAINTED_CRAFTING_TABLE, Items.CRAFTING_TABLE, recipeOutput);
        build(EIOBlocks.PAINTED_REDSTONE_BLOCK, Items.REDSTONE_BLOCK, recipeOutput);
        build(EIOBlocks.PAINTED_TRAPDOOR, ItemTags.WOODEN_TRAPDOORS, recipeOutput);
        build(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE, ItemTags.WOODEN_PRESSURE_PLATES, recipeOutput);
        build(EIOBlocks.PAINTED_SLAB, ItemTags.WOODEN_SLABS, recipeOutput);
        build(EIOBlocks.PAINTED_WALL, ItemTags.WALLS, recipeOutput);
        build(EIOBlocks.PAINTED_GLOWSTONE, Items.GLOWSTONE, recipeOutput);
        build(EIOBlocks.PAINTED_TRAVEL_ANCHOR, EIOBlocks.TRAVEL_ANCHOR, recipeOutput);
        // Painted block to painted block
        build(EIOBlocks.PAINTED_FENCE, EIOBlocks.PAINTED_FENCE, "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_FENCE_GATE, EIOBlocks.PAINTED_FENCE_GATE, "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_SAND, EIOBlocks.PAINTED_SAND, "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_STAIRS, EIOBlocks.PAINTED_STAIRS, "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_CRAFTING_TABLE, EIOBlocks.PAINTED_CRAFTING_TABLE, "_frompainted",
                recipeOutput);
        build(EIOBlocks.PAINTED_REDSTONE_BLOCK, EIOBlocks.PAINTED_REDSTONE_BLOCK, "_frompainted",
                recipeOutput);
        build(EIOBlocks.PAINTED_TRAPDOOR, EIOBlocks.PAINTED_TRAPDOOR, "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE, EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE,
                "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_SLAB, EIOBlocks.PAINTED_SLAB, "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_GLOWSTONE, EIOBlocks.PAINTED_GLOWSTONE, "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_WALL, EIOBlocks.PAINTED_WALL, "_frompainted", recipeOutput);
        build(EIOBlocks.PAINTED_TRAVEL_ANCHOR, EIOBlocks.PAINTED_TRAVEL_ANCHOR, "_frompainted",
                recipeOutput);
    }

    protected void build(ItemLike output, ItemLike input, RecipeOutput recipeOutput) {
        build(output, Ingredient.of(input), "", recipeOutput, ItemPredicate.Builder.item().of(items, input).build());
    }

    protected void build(ItemLike output, TagKey<Item> input, RecipeOutput recipeOutput) {
        build(output, ingredientFromTag(input), "", recipeOutput, ItemPredicate.Builder.item().of(items, input).build());
    }

    protected void build(ItemLike output, ItemLike input, String suffix, RecipeOutput recipeOutput) {
        build(output, Ingredient.of(input), suffix, recipeOutput, ItemPredicate.Builder.item().of(items, input).build());
    }

    protected void build(ItemLike output, Ingredient input, String suffix, RecipeOutput recipeOutput, ItemPredicate unlock) {
        RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
        advancementBuilder.unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(unlock));
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE,
            EnderIO.id("painting/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath() + suffix));
        var advancement = advancementBuilder.build(recipeOutput, key, RecipeCategory.MISC);
        recipeOutput.accept(key, new PaintingRecipe(input, new ItemStackTemplate(output.asItem())), advancement);
    }
}
