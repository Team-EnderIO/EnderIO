package com.enderio.base.datagen.recipe.standard;

import com.enderio.base.EnderIO;
import com.enderio.base.common.block.glass.FusedQuartzBlock;
import com.enderio.base.common.block.glass.GlassBlocks;
import com.enderio.base.common.block.glass.GlassCollisionPredicate;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class GlassRecipes extends RecipeProvider {
    public GlassRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> recipeConsumer) {
        for (GlassBlocks glassBlocks : EIOBlocks.GLASS_BLOCKS.values()) {
            recolor(glassBlocks, recipeConsumer);
            if (glassBlocks.getGlassIdentifier().collisionPredicate() == GlassCollisionPredicate.NONE) {
                for (Item token: new Item[]{EIOItems.PLAYER_TOKEN.get(), EIOItems.ANIMAL_TOKEN.get(), EIOItems.MONSTER_TOKEN.get()}) {
                    addCollisionToken(glassBlocks, token, recipeConsumer);
                }
            } else {
                invert(glassBlocks, recipeConsumer);
            }
        }

    }

    private static void recolor(GlassBlocks blocks, Consumer<FinishedRecipe> recipeConsumer) {
        for (DyeColor color: DyeColor.values()) {
            ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(blocks.COLORS.get(color).get(), 8);
            for (int i = 0; i < 8; i++) {
                builder.requires(EIOTags.Items.GLASS_TAGS.get(blocks.getGlassIdentifier()));
            }
            builder.requires(color.getTag())
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(blocks.CLEAR.get()))
                .save(recipeConsumer, EnderIO.loc("recolor_" + blocks.COLORS.get(color).get().getRegistryName().getPath()));
        }
    }

    private static void addCollisionToken(GlassBlocks blocks, Item token, Consumer<FinishedRecipe> recipeConsumer) {
        GlassCollisionPredicate collision = GlassCollisionPredicate.fromToken(token);
        if (collision == null)
            return;
        var output = EIOBlocks.GLASS_BLOCKS.get(blocks.getGlassIdentifier().withCollision(collision)).CLEAR.get();

        ShapedRecipeBuilder.shaped(output, 8)
            .define('G', blocks.CLEAR.get())
            .define('T', token)
            .pattern("GGG")
            .pattern("GTG")
            .pattern("GGG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(token))
            .save(recipeConsumer, EnderIO.loc("collision_token_" + output.getRegistryName().getPath()));

    }
    private static void invert(GlassBlocks blocks, Consumer<FinishedRecipe> recipeConsumer) {
        var collision = blocks.getGlassIdentifier().collisionPredicate();
        if (collision == GlassCollisionPredicate.NONE)
            return;
        var output = EIOBlocks.GLASS_BLOCKS.get(blocks.getGlassIdentifier().withCollision(collision)).CLEAR.get();

        ShapedRecipeBuilder.shaped(output, 8)
            .define('G', blocks.CLEAR.get())
            .define('T', Items.REDSTONE_TORCH)
            .pattern("GGG")
            .pattern("GTG")
            .pattern("GGG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(blocks.CLEAR.get()))
            .save(recipeConsumer, EnderIO.loc("invert_" + output.getRegistryName().getPath()));

    }

}
