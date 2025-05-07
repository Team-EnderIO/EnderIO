package com.enderio.base.common.integrations.jei.extension;

import static mezz.jei.api.recipe.RecipeIngredientRole.INPUT;
import static mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.api.soul.SoulBoundUtils;
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
                .filter(f -> SoulBoundUtils.canBindSoul(f.getTypedValue().getIngredient()))
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
            var soul = SoulBoundUtils.getBoundSoul(input.get().getTypedValue().getIngredient());
            if (soul != Soul.EMPTY) {
                if (SoulBoundUtils.tryBindSoul(resultItem, soul)) {
                    noData = false;

                    inputs = recipe
                        .getIngredients()
                        .stream()
                        .map(ingredient -> Arrays
                            .stream(ingredient.getItems())
                            .<ItemStack>mapMulti((ingredientItem, consumer) -> SoulBoundUtils.getBoundIfCapable(ingredientItem, soul).ifPresent(consumer))
                            .toList())
                        .toList();
                }
            }
        } else if (output.isPresent()) {
            ItemStack itemStack = output.get().getTypedValue().getIngredient();
            var outputSoulBindable = itemStack.getCapability(EIOCapabilities.SoulBindable.ITEM);

            if (outputSoulBindable != null) {
                results = List.of(itemStack);
                Soul soul = outputSoulBindable.getBoundSoul();

                if (soul != Soul.EMPTY) {
                    noData = false;
                    inputs = recipe.getIngredients()
                            .stream()
                            .map(ingredient -> Arrays.stream(ingredient.getItems())
                                    .<ItemStack>mapMulti((ingredientItem, consumer) -> SoulBoundUtils.getBoundIfCapable(ingredientItem, soul).ifPresent(consumer))
                                    .toList())
                            .toList();
                }
            }
        }

        if (noData) {
            var capturableEntityTypes = EntityCaptureUtils.getCapturableEntityTypes();

            results = new ArrayList<>(capturableEntityTypes.stream().map(entityType -> {
                ItemStack result = resultItem.copy();
                if (SoulBoundUtils.tryBindSoul(result, Soul.of(entityType))) {
                    return Optional.of(result);
                }

                return Optional.<ItemStack>empty();
            }).flatMap(Optional::stream).toList());

            ItemStack result = resultItem.copy();
            SoulBoundUtils.tryBindSoul(result, Soul.EMPTY);
            results.add(result);

            inputs = recipe.getIngredients()
                    .stream()
                    .map(ingredient -> Arrays.stream(ingredient.getItems())
                            .<ItemStack>mapMulti((ingredientItem, consumer) -> {
                                if (SoulBoundUtils.canBindSoul(ingredientItem)) {
                                    for (var entityType : capturableEntityTypes) {
                                        ItemStack item = ingredientItem.copy();
                                        if (SoulBoundUtils.tryBindSoul(item, Soul.of(entityType))) {
                                            consumer.accept(item);
                                        }
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
