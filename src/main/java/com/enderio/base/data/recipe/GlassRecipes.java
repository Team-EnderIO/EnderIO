package com.enderio.base.data.recipe;

import com.enderio.EnderIO;
import com.enderio.base.common.block.glass.GlassBlocks;
import com.enderio.base.common.block.glass.GlassCollisionPredicate;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class GlassRecipes extends RecipeProvider {
    public GlassRecipes(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeConsumer) {
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
            ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, blocks.COLORS.get(color).get(), 8);
            for (int i = 0; i < 8; i++) {
                builder.requires(EIOTags.Items.GLASS_TAGS.get(blocks.getGlassIdentifier()));
            }
            builder.requires(color.getTag())
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(blocks.CLEAR.get()))
                .save(recipeConsumer, EnderIO.loc("recolor_" + ForgeRegistries.BLOCKS.getKey(blocks.COLORS.get(color).get()).getPath()));
        }
    }

    private static void addCollisionToken(GlassBlocks blocks, Item token, Consumer<FinishedRecipe> recipeConsumer) {
        GlassCollisionPredicate collision = GlassCollisionPredicate.fromToken(token);
        if (collision == null)
            return;
        var output = EIOBlocks.GLASS_BLOCKS.get(blocks.getGlassIdentifier().withCollision(collision)).CLEAR.get();

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 8)
            .define('G', blocks.CLEAR.get())
            .define('T', token)
            .pattern("GGG")
            .pattern("GTG")
            .pattern("GGG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(token))
            .save(recipeConsumer, EnderIO.loc("collision_token_" + ForgeRegistries.BLOCKS.getKey(output).getPath()));

    }
    private static void invert(GlassBlocks blocks, Consumer<FinishedRecipe> recipeConsumer) {
        var collision = GlassCollisionPredicate.invert(blocks.getGlassIdentifier().collisionPredicate());
        if (collision == GlassCollisionPredicate.NONE)
            return;
        var output = EIOBlocks.GLASS_BLOCKS.get(blocks.getGlassIdentifier().withCollision(collision)).CLEAR.get();

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 8)
            .define('G', blocks.CLEAR.get())
            .define('T', Items.REDSTONE_TORCH)
            .pattern("GGG")
            .pattern("GTG")
            .pattern("GGG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(blocks.CLEAR.get()))
            .save(recipeConsumer, EnderIO.loc("invert_" + ForgeRegistries.BLOCKS.getKey(output).getPath()));

    }
}
