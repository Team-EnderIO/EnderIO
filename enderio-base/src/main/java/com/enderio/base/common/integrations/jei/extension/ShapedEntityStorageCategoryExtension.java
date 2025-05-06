package com.enderio.base.common.integrations.jei.extension;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

import com.enderio.base.api.attachment.Soul;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.integrations.jei.EnderIOJEI;
import com.enderio.base.common.recipe.ShapedEntityStorageRecipe;
import com.enderio.base.common.util.EntityCaptureUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ShapedEntityStorageCategoryExtension implements ICraftingCategoryExtension<ShapedEntityStorageRecipe> {

    public ShapedEntityStorageCategoryExtension() {
    }

    @Override
    public void setRecipe(RecipeHolder<ShapedEntityStorageRecipe> recipeHolder, IRecipeLayoutBuilder builder,
            ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {

        Optional<IFocus<ItemStack>> output = focuses.getItemStackFocuses(OUTPUT).findFirst();
        Optional<IFocus<ItemStack>> input = focuses.getItemStackFocuses(INPUT)
                .filter(f -> f.getTypedValue()
                        .getIngredient()
                        .getCapability(EIOCapabilities.SingleSoulStorage.ITEM) != null)
                .findFirst();

        ShapedEntityStorageRecipe recipe = recipeHolder.value();
        ItemStack resultItem = EnderIOJEI.getResultItem(recipe);
        List<List<ItemStack>> inputs = recipe.getIngredients()
                .stream()
                .map(ingredient -> Arrays.stream(ingredient.getItems())
                        .<ItemStack>mapMulti((ingredientItem, consumer) -> consumer.accept(ingredientItem))
                        .toList())
                .toList();
        List<ItemStack> results = List.of(resultItem);
        boolean noData = true;

        if (input.isPresent()) {
            var inputSoulStorage = Objects.requireNonNull(
                    input.get().getTypedValue().getIngredient().getCapability(EIOCapabilities.SingleSoulStorage.ITEM));
            Soul soul = inputSoulStorage.getSoul();

            if (soul != Soul.EMPTY) {
                var resultSoulStorage = resultItem.getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
                if (resultSoulStorage != null) {
                    noData = false;

                    resultSoulStorage.setSoul(soul);

                    inputs = recipe.getIngredients()
                            .stream()
                            .map(ingredient -> Arrays.stream(ingredient.getItems())
                                    .<ItemStack>mapMulti((ingredientItem, consumer) -> {
                                        var ingredientSoulStorage = ingredientItem
                                                .getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
                                        if (ingredientSoulStorage != null) {
                                            ItemStack item = ingredientItem.copy();
                                            Objects.requireNonNull(
                                                    item.getCapability(EIOCapabilities.SingleSoulStorage.ITEM))
                                                    .setSoul(soul);
                                            consumer.accept(item);
                                        } else {
                                            consumer.accept(ingredientItem);
                                        }
                                    })
                                    .toList())
                            .toList();
                }
            }
        } else if (output.isPresent()) {
            ItemStack itemStack = output.get().getTypedValue().getIngredient();
            var outputSoulStorage = itemStack.getCapability(EIOCapabilities.SingleSoulStorage.ITEM);

            if (outputSoulStorage != null) {
                results = List.of(itemStack);
                Soul soul = outputSoulStorage.getSoul();

                if (soul != Soul.EMPTY) {
                    noData = false;
                    inputs = recipe.getIngredients()
                            .stream()
                            .map(ingredient -> Arrays.stream(ingredient.getItems())
                                    .<ItemStack>mapMulti((ingredientItem, consumer) -> {
                                        var ingredientSoulStorage = ingredientItem
                                                .getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
                                        if (ingredientSoulStorage != null) {
                                            ItemStack item = ingredientItem.copy();
                                            Objects.requireNonNull(
                                                    item.getCapability(EIOCapabilities.SingleSoulStorage.ITEM))
                                                    .setSoul(soul);
                                            consumer.accept(item);
                                        } else {
                                            consumer.accept(ingredientItem);
                                        }
                                    })
                                    .toList())
                            .toList();
                }
            }
        }

        if (noData) {
            var allCapturableEntities = EntityCaptureUtils.getCapturableEntities();

            results = new ArrayList<>(allCapturableEntities.stream().map(e -> {
                ItemStack result = resultItem.copy();
                var resultSoulStorage = result.getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
                if (resultSoulStorage != null) {
                    resultSoulStorage.setSoul(Soul.of(e));
                }

                return result;
            }).toList());

            ItemStack result = resultItem.copy();
            var resultSoulStorage = result.getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
            if (resultSoulStorage != null) {
                resultSoulStorage.setSoul(Soul.EMPTY);
            }

            results.add(result);

            inputs = recipe.getIngredients()
                    .stream()
                    .map(ingredient -> Arrays.stream(ingredient.getItems())
                            .<ItemStack>mapMulti((ingredientItem, consumer) -> {
                                var ingredientSoulStorage = ingredientItem
                                        .getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
                                if (ingredientSoulStorage != null) {
                                    for (ResourceLocation entity : allCapturableEntities) {
                                        ItemStack item = ingredientItem.copy();
                                        Objects.requireNonNull(
                                                item.getCapability(EIOCapabilities.SingleSoulStorage.ITEM))
                                                .setSoul(Soul.of(entity));
                                        consumer.accept(item);
                                        consumer.accept(item);
                                    }
                                } else {
                                    consumer.accept(ingredientItem);
                                }
                            })
                            .toList())
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
