package com.enderio.base.common.integrations.jei.extension;

import com.enderio.api.capability.StoredEntityData;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.integrations.jei.EnderIOJEI;
import com.enderio.base.common.recipe.ShapedEntityStorageRecipe;
import com.enderio.base.common.util.EntityCaptureUtils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ShapedEntityStorageCategoryExtension implements ICraftingCategoryExtension {
    protected final ShapedEntityStorageRecipe recipe;

    public ShapedEntityStorageCategoryExtension(ShapedEntityStorageRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {

        var allCapturableEntities = EntityCaptureUtils.getCapturableEntities();

        ItemStack resultItem = EnderIOJEI.getResultItem(recipe);
        List<ItemStack> results = allCapturableEntities.stream().map(e -> {
            ItemStack result = resultItem.copy();

            result.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(storage -> {
                storage.setStoredEntityData(StoredEntityData.of(e));
            });

            return result;
        }).toList();

        List<List<ItemStack>> inputs = recipe.getIngredients().stream()
            .map(ingredient ->
                Arrays.stream(ingredient.getItems()).<ItemStack>mapMulti((ingredientItem, consumer) -> {
                    boolean hasStorage = ingredientItem.getCapability(EIOCapabilities.ENTITY_STORAGE).isPresent();
                    if (hasStorage) {
                        for (ResourceLocation entity : allCapturableEntities) {
                            ItemStack item = ingredientItem.copy();
                            item.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(storage -> {
                                storage.setStoredEntityData(StoredEntityData.of(entity));
                            });
                            consumer.accept(item);
                        }
                    } else {
                        consumer.accept(ingredientItem);
                    }
                }).toList()
            )
            .toList();

        craftingGridHelper.createAndSetOutputs(builder, results);
        craftingGridHelper.createAndSetInputs(builder, inputs, getWidth(), getHeight());
    }

    @Override
    public int getWidth() {
        return recipe.getWidth();
    }

    @Override
    public int getHeight() {
        return recipe.getHeight();
    }
}
