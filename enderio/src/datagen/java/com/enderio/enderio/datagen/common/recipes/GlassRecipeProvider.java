package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.glass.GlassBlocks;
import com.enderio.enderio.content.glass.GlassCollisionPredicate;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class GlassRecipeProvider extends SubRecipeProvider {
    @Override
    public void buildRecipes(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        var items = registries.lookupOrThrow(Registries.ITEM);

        for (GlassBlocks glassBlocks : EIOBlocks.GLASS_BLOCKS.values()) {
            recolor(items, glassBlocks, recipeOutput);
            if (glassBlocks.getGlassIdentifier().collisionPredicate() == GlassCollisionPredicate.NONE) {
                for (Item token: new Item[]{EIOItems.PLAYER_TOKEN.get(), EIOItems.ANIMAL_TOKEN.get(), EIOItems.MONSTER_TOKEN.get()}) {
                    addCollisionToken(items, glassBlocks, token, recipeOutput);
                }
            } else {
                invert(items, glassBlocks, recipeOutput);
            }
        }
    }

    private static void recolor(HolderLookup.RegistryLookup<Item> items, GlassBlocks blocks, RecipeOutput recipeOutput) {
        for (DyeColor color: DyeColor.values()) {
            ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, blocks.COLORS.get(color).get(), 8);
            for (int i = 0; i < 8; i++) {
                builder.requires(EIOTags.Items.GLASS_TAGS.get(blocks.getGlassIdentifier()));
            }
            builder.requires(color.getTag())
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(blocks.CLEAR.get()))
                .save(recipeOutput, ResourceKey.create(Registries.RECIPE, EnderIO.id("recolor_" + BuiltInRegistries.BLOCK.getKey(blocks.COLORS.get(color).get()).getPath())));
        }
    }

    private static void addCollisionToken(HolderLookup.RegistryLookup<Item> items, GlassBlocks blocks, Item token, RecipeOutput recipeOutput) {
        GlassCollisionPredicate collision = GlassCollisionPredicate.fromToken(token);
        if (collision == null) {
            return;
        }

        var output = EIOBlocks.GLASS_BLOCKS.get(blocks.getGlassIdentifier().withCollision(collision)).CLEAR.get();

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, output, 8)
            .define('G', blocks.CLEAR.get())
            .define('T', token)
            .pattern("GGG")
            .pattern("GTG")
            .pattern("GGG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(token))
            .save(recipeOutput, ResourceKey.create(Registries.RECIPE, EnderIO.id("collision_token_" + BuiltInRegistries.BLOCK.getKey(output).getPath())));

    }
    private static void invert(HolderLookup.RegistryLookup<Item> items, GlassBlocks blocks, RecipeOutput recipeOutput) {
        var collision = GlassCollisionPredicate.invert(blocks.getGlassIdentifier().collisionPredicate());
        if (collision == GlassCollisionPredicate.NONE) {
            return;
        }

        var output = EIOBlocks.GLASS_BLOCKS.get(blocks.getGlassIdentifier().withCollision(collision)).CLEAR.get();

        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, output, 8)
            .define('G', blocks.CLEAR.get())
            .define('T', Items.REDSTONE_TORCH)
            .pattern("GGG")
            .pattern("GTG")
            .pattern("GGG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(blocks.CLEAR.get()))
            .save(recipeOutput, ResourceKey.create(Registries.RECIPE, EnderIO.id("invert_" + BuiltInRegistries.BLOCK.getKey(output).getPath())));
    }
}
