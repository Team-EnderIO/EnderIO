package com.enderio.base.common.integrations.jei.extension;

import com.enderio.api.attachment.StoredEntityData;
import com.enderio.base.common.init.EIOAttachments;
import com.enderio.base.common.integrations.jei.EnderIOJEI;
import com.enderio.base.common.recipe.ShapedEntityStorageRecipe;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.EntityCaptureUtils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;
import java.util.List;

public class ShapedEntityStorageCategoryExtension implements ICraftingCategoryExtension<ShapedEntityStorageRecipe> {

    public ShapedEntityStorageCategoryExtension() {}

    @Override
    public void setRecipe(RecipeHolder<ShapedEntityStorageRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {

        var allCapturableEntities = EntityCaptureUtils.getCapturableEntities();

        ShapedEntityStorageRecipe recipe = recipeHolder.value();
        ItemStack resultItem = EnderIOJEI.getResultItem(recipe);
        List<ItemStack> results = allCapturableEntities.stream().map(e -> {
            ItemStack result = resultItem.copy();

            if (result.is(EIOTags.Items.ENTITY_STORAGE)) {
                result.setData(EIOAttachments.STORED_ENTITY, StoredEntityData.of(e));
            }

            return result;
        }).toList();

        List<List<ItemStack>> inputs = recipe.getIngredients().stream()
            .map(ingredient ->
                Arrays.stream(ingredient.getItems()).<ItemStack>mapMulti((ingredientItem, consumer) -> {
                    boolean hasStorage = ingredientItem.is(EIOTags.Items.ENTITY_STORAGE);
                    if (hasStorage) {
                        for (ResourceLocation entity : allCapturableEntities) {
                            ItemStack item = ingredientItem.copy();
                            if (item.is(EIOTags.Items.ENTITY_STORAGE)) {
                                item.setData(EIOAttachments.STORED_ENTITY, StoredEntityData.of(entity));
                            }
                            consumer.accept(item);
                        }
                    } else {
                        consumer.accept(ingredientItem);
                    }
                }).toList()
            )
            .toList();

        craftingGridHelper.createAndSetOutputs(builder, results);
        craftingGridHelper.createAndSetInputs(builder, inputs, getWidth(recipeHolder), getHeight(recipeHolder));
    }

    @Override
    public int getWidth(RecipeHolder<ShapedEntityStorageRecipe> recipeHolder) {
        return recipeHolder.value().getRecipeHeight();
    }

    @Override
    public int getHeight(RecipeHolder<ShapedEntityStorageRecipe> recipeHolder) {
        return recipeHolder.value().getRecipeHeight();
    }
}
