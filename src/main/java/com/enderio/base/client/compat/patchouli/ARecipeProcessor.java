package com.enderio.base.client.compat.patchouli;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.CountedIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ARecipeProcessor<R extends Recipe<?>> implements IComponentProcessor {
    private final RecipeType<R> recipeType;
    protected R recipe = null;

    public ARecipeProcessor(RecipeType<R> recipeType) {
        this.recipeType = recipeType;
    }

    @Override
    public void setup(IVariableProvider variables) {
        if (variables.has("recipe")) {
            if (Minecraft.getInstance().level != null) {
                RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
                Optional<? extends Recipe<?>> optionalRecipe = recipeManager.byKey(new ResourceLocation(variables.get("recipe").asString()));
                optionalRecipe.ifPresent(rawRecipe -> {
                    if (rawRecipe.getType() == recipeType) {
                        recipe = (R) rawRecipe;
                    }
                });
            }

            if (recipe == null)
                EnderIO.LOGGER.error("Recipe: {} doesn't exist", variables.get("recipe").asString());
        } else {
            EnderIO.LOGGER.error("Recipe page contains no recipe");
        }
    }

    /**
     * Convert the CountedIngredient into a string patchouli can understand
     * NOTE: It seems that the item component from patchouli can only accept Strings not objects,
     * therefor Tags are not supported.
     *
     * @param ingredient The ingredient to be converted
     * @return An IVariable with the JsonPrimitive of a String
     * @see <a href="https://vazkiimods.github.io/Patchouli/docs/patchouli-advanced/itemstack-format/">ItemStack String Format</a>
     */
    public static IVariable processCountedIngredient(CountedIngredient ingredient) {
        Ingredient ing = ingredient.ingredient();
        int count = ingredient.count();
        return IVariable.wrap(
            Arrays.stream(ing.getItems())
                .map(stack -> ForgeRegistries.ITEMS.getKey(stack.getItem()).toString() + '#' + count + stack.getOrCreateTag())
                .collect(Collectors.joining(",")));
    }
}
