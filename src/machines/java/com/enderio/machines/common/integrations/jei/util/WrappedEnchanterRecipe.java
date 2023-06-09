package com.enderio.machines.common.integrations.jei.util;

import com.enderio.machines.common.recipe.EnchanterRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;

import java.util.Arrays;
import java.util.List;

public class WrappedEnchanterRecipe implements Recipe<Container> {
    private final EnchanterRecipe recipe;
    private final int level;

    public WrappedEnchanterRecipe(EnchanterRecipe recipe, int level) {
        this.recipe = recipe;
        this.level = level;
    }

    public List<ItemStack> getInputs() {
        return Arrays.stream(recipe.getInput().getItems()).map(item -> {
            var i = item.copy();
            i.setCount(recipe.getAmountPerLevel() * level);
            return i;
        }).toList();
    }

    public List<ItemStack> getLapis() {
        return Arrays.stream(Ingredient.of(Tags.Items.GEMS_LAPIS).getItems()).peek(item -> item.setCount(recipe.getLapisForLevel(level))).toList();
    }

    public ItemStack getBook() {
        return recipe.getBookForLevel(level);
    }

    public int getLevel() {
        return level;
    }

    public Enchantment getEnchantment() {
        return recipe.getEnchantment();
    }

    public int getCost() {
        return recipe.getXPCostForLevel(level);
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return recipe.getId();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return recipe.getType();
    }
}
