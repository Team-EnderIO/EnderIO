package com.enderio.machines.common.integrations.jei.util;

import com.enderio.machines.common.blocks.enchanter.EnchanterRecipe;

import java.util.Arrays;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

public class WrappedEnchanterRecipe implements Recipe<EnchanterRecipe.Input> {
    private final RecipeHolder<EnchanterRecipe> recipe;
    private final int level;

    public WrappedEnchanterRecipe(RecipeHolder<EnchanterRecipe> recipe, int level) {
        this.recipe = recipe;
        this.level = level;
    }

    public ResourceLocation id() {
        return ResourceLocation.fromNamespaceAndPath(recipe.id().getNamespace(), recipe.id().getPath() + "_" + level);
    }

    public List<ItemStack> getInputs() {
        return Arrays.stream(recipe.value().input().getItems()).map(item -> item.copyWithCount(item.getCount() * level)).toList();
    }

    public List<ItemStack> getLapis() {
        return Arrays.stream(Ingredient.of(Tags.Items.GEMS_LAPIS).getItems()).map(item -> item.copyWithCount(recipe.value().getLapisForLevel(level))).toList();
    }

    public ItemStack getBook() {
        return recipe.value().getBookForLevel(level);
    }

    public int getLevel() {
        return level;
    }

    public Holder<Enchantment> getEnchantment() {
        return recipe.value().enchantment();
    }

    public int getCost() {
        return recipe.value().getXPCostForLevel(level);
    }

    @Override
    public boolean matches(EnchanterRecipe.Input recipeInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(EnchanterRecipe.Input recipeInput, HolderLookup.Provider registryAccess) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return recipe.value().getType();
    }
}
