package com.enderio.base.common.integrations.jei.extension;

import com.enderio.api.attachment.StoredEntityData;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.integrations.jei.EnderIOJEI;
import com.enderio.base.common.recipe.ShapedEntityStorageRecipe;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.EntityCaptureUtils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

public class ShapedEntityStorageCategoryExtension implements ICraftingCategoryExtension<ShapedEntityStorageRecipe> {

    public ShapedEntityStorageCategoryExtension() {}

    @Override
    public void setRecipe(RecipeHolder<ShapedEntityStorageRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {

        Optional<IFocus<ItemStack>> output = focuses.getItemStackFocuses(OUTPUT).findFirst();
        Optional<IFocus<ItemStack>> input = focuses.getItemStackFocuses(INPUT).filter(f -> f.getTypedValue().getItemStack().get().is(EIOTags.Items.ENTITY_STORAGE)).findFirst();
        ShapedEntityStorageRecipe recipe = recipeHolder.value();
        ItemStack resultItem = EnderIOJEI.getResultItem(recipe);
        List<List<ItemStack>> inputs = recipe.getIngredients().stream()
            .map(ingredient ->
                Arrays.stream(ingredient.getItems()).<ItemStack>mapMulti(
                    (ingredientItem, consumer) -> consumer.accept(ingredientItem))
                    .toList())
            .toList();
        List<ItemStack> results = List.of(resultItem);

        if (input.isPresent()) {
            StoredEntityData storedEntityData = input.get().getTypedValue().getIngredient().getOrDefault(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY);
            resultItem.set(EIODataComponents.STORED_ENTITY, storedEntityData);
            inputs = recipe.getIngredients().stream()
                .map(ingredient ->
                    Arrays.stream(ingredient.getItems()).<ItemStack>mapMulti((ingredientItem, consumer) -> {
                        boolean hasStorage = ingredientItem.is(EIOTags.Items.ENTITY_STORAGE);
                        if (hasStorage) {
                            ItemStack item = ingredientItem.copy();
                            if (item.is(EIOTags.Items.ENTITY_STORAGE)) {
                                item.set(EIODataComponents.STORED_ENTITY, storedEntityData);
                            }
                            consumer.accept(item);
                        } else {
                            consumer.accept(ingredientItem);
                        }
                    }).toList()
                ).toList();
        } else if (output.isPresent()) {
            ItemStack itemStack = output.get().getTypedValue().getIngredient();
            if (itemStack.is(EIOTags.Items.ENTITY_STORAGE)) {
                results = List.of(itemStack);
                StoredEntityData storedEntityData = itemStack.getOrDefault(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY);
                inputs = recipe.getIngredients().stream()
                    .map(ingredient ->
                        Arrays.stream(ingredient.getItems()).<ItemStack>mapMulti((ingredientItem, consumer) -> {
                            boolean hasStorage = ingredientItem.is(EIOTags.Items.ENTITY_STORAGE);
                            if (hasStorage) {
                                ItemStack item = ingredientItem.copy();
                                if (item.is(EIOTags.Items.ENTITY_STORAGE)) {
                                    item.set(EIODataComponents.STORED_ENTITY, storedEntityData);
                                }
                                consumer.accept(item);
                            } else {
                                consumer.accept(ingredientItem);
                            }
                        }).toList()
                    ).toList();
            }
        } else {
            var allCapturableEntities = EntityCaptureUtils.getCapturableEntities();

            results = allCapturableEntities.stream().map(e -> {
                ItemStack result = resultItem.copy();

                if (result.is(EIOTags.Items.ENTITY_STORAGE)) {
                    result.set(EIODataComponents.STORED_ENTITY, StoredEntityData.of(e));
                }

                return result;
            }).toList();

            inputs = recipe.getIngredients().stream()
                .map(ingredient ->
                    Arrays.stream(ingredient.getItems()).<ItemStack>mapMulti((ingredientItem, consumer) -> {
                        boolean hasStorage = ingredientItem.is(EIOTags.Items.ENTITY_STORAGE);
                        if (hasStorage) {
                            for (ResourceLocation entity : allCapturableEntities) {
                                ItemStack item = ingredientItem.copy();
                                if (item.is(EIOTags.Items.ENTITY_STORAGE)) {
                                    item.set(EIODataComponents.STORED_ENTITY, StoredEntityData.of(entity));
                                }
                                consumer.accept(item);
                            }
                        } else {
                            consumer.accept(ingredientItem);
                        }
                    }).toList()
                )
                .toList();
        }


        craftingGridHelper.createAndSetOutputs(builder, results);
        craftingGridHelper.createAndSetInputs(builder, inputs, getWidth(recipeHolder), getHeight(recipeHolder));
    }

    @Override
    public int getWidth(RecipeHolder<ShapedEntityStorageRecipe> recipeHolder) {
        return recipeHolder.value().getWidth();
    }

    @Override
    public int getHeight(RecipeHolder<ShapedEntityStorageRecipe> recipeHolder) {
        return recipeHolder.value().getHeight();
    }
}
