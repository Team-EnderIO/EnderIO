package com.enderio.base.datagen.recipe.standard;

import com.enderio.base.EnderIO;
import com.enderio.base.common.block.glass.GlassBlocks;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.DyeColor;

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

}
