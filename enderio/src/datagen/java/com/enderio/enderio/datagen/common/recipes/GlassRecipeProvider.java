package com.enderio.enderio.datagen.common.recipes;

import com.enderio.core.data.recipe.SubRecipeProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.common.block.glass.GlassBlocks;
import com.enderio.enderio.common.block.glass.GlassCollisionPredicate;
import com.enderio.enderio.common.init.EIOBlocks;
import com.enderio.enderio.common.init.EIOItems;
import com.enderio.enderio.common.tag.EIOTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class GlassRecipeProvider extends SubRecipeProvider {
    @Override
    public void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        for (GlassBlocks glassBlocks : EIOBlocks.GLASS_BLOCKS.values()) {
            recolor(glassBlocks, recipeOutput);
            if (glassBlocks.getGlassIdentifier().collisionPredicate() == GlassCollisionPredicate.NONE) {
                for (Item token: new Item[]{EIOItems.PLAYER_TOKEN.get(), EIOItems.ANIMAL_TOKEN.get(), EIOItems.MONSTER_TOKEN.get()}) {
                    addCollisionToken(glassBlocks, token, recipeOutput);
                }
            } else {
                invert(glassBlocks, recipeOutput);
            }
        }
    }

    private static void recolor(GlassBlocks blocks, RecipeOutput recipeOutput) {
        for (DyeColor color: DyeColor.values()) {
            ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, blocks.COLORS.get(color).get(), 8);
            for (int i = 0; i < 8; i++) {
                builder.requires(EIOTags.Items.GLASS_TAGS.get(blocks.getGlassIdentifier()));
            }
            builder.requires(color.getTag())
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(blocks.CLEAR.get()))
                .save(recipeOutput, EnderIO.rl("recolor_" + BuiltInRegistries.BLOCK.getKey(blocks.COLORS.get(color).get()).getPath()));
        }
    }

    private static void addCollisionToken(GlassBlocks blocks, Item token, RecipeOutput recipeOutput) {
        GlassCollisionPredicate collision = GlassCollisionPredicate.fromToken(token);
        if (collision == null) {
            return;
        }

        var output = EIOBlocks.GLASS_BLOCKS.get(blocks.getGlassIdentifier().withCollision(collision)).CLEAR.get();

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 8)
            .define('G', blocks.CLEAR.get())
            .define('T', token)
            .pattern("GGG")
            .pattern("GTG")
            .pattern("GGG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(token))
            .save(recipeOutput, EnderIO.rl("collision_token_" + BuiltInRegistries.BLOCK.getKey(output).getPath()));

    }
    private static void invert(GlassBlocks blocks, RecipeOutput recipeOutput) {
        var collision = GlassCollisionPredicate.invert(blocks.getGlassIdentifier().collisionPredicate());
        if (collision == GlassCollisionPredicate.NONE) {
            return;
        }

        var output = EIOBlocks.GLASS_BLOCKS.get(blocks.getGlassIdentifier().withCollision(collision)).CLEAR.get();

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 8)
            .define('G', blocks.CLEAR.get())
            .define('T', Items.REDSTONE_TORCH)
            .pattern("GGG")
            .pattern("GTG")
            .pattern("GGG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(blocks.CLEAR.get()))
            .save(recipeOutput, EnderIO.rl("invert_" + BuiltInRegistries.BLOCK.getKey(output).getPath()));
    }
}
