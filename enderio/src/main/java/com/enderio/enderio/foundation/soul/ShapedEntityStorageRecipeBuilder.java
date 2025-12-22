package com.enderio.enderio.foundation.soul;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class ShapedEntityStorageRecipeBuilder extends ShapedRecipeBuilder {

    public ShapedEntityStorageRecipeBuilder(HolderGetter<Item> items, RecipeCategory category, ItemLike result, int count) {
        super(items, category, result, count);
    }

    public static ShapedEntityStorageRecipeBuilder shaped(HolderGetter<Item> items, RecipeCategory pCategory, ItemLike pResult) {
        return shaped(items, pCategory, pResult, 1);
    }

    public static ShapedEntityStorageRecipeBuilder shaped(HolderGetter<Item> items, RecipeCategory pCategory, ItemLike pResult, int pCount) {
        return new ShapedEntityStorageRecipeBuilder(items, pCategory, pResult, pCount);
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> resourceKey) {
        super.save(new RecipeOutput() {
            @Override
            public Advancement.Builder advancement() {
                return output.advancement();
            }

            @Override
            public void includeRootAdvancement() {
                output.includeRootAdvancement();
            }

            @Override
            public void accept(ResourceKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
                if (recipe instanceof ShapedRecipe shapedRecipe) {
                    output.accept(resourceKey, new ShapedEntityStorageRecipe(shapedRecipe), advancement, conditions);
                }
            }
        }, resourceKey);
    }
}
