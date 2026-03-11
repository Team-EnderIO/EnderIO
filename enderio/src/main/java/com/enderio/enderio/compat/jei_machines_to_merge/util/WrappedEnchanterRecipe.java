package com.enderio.enderio.compat.jei_machines_to_merge.util;

import com.enderio.enderio.content.enchanter.EnchanterRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
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

public class WrappedEnchanterRecipe implements Recipe<EnchanterRecipe.Input> {
    private final EnchanterRecipe recipe;
    private final int level;

    public WrappedEnchanterRecipe(EnchanterRecipe recipe, int level) {
        this.recipe = recipe;
        this.level = level;
    }

    public ResourceLocation id() {
        return new ResourceLocation(recipe.id().getNamespace(), recipe.id().getPath() + "_" + level);
    }

    public List<ItemStack> getInputs() {
        return Arrays.stream(recipe.input().getItems()).map(item -> item.copyWithCount(item.getCount() * level)).toList();
    }

    public List<ItemStack> getLapis() {
        return Arrays.stream(Ingredient.of(Tags.Items.GEMS_LAPIS).getItems()).map(item -> item.copyWithCount(recipe.getLapisForLevel(level))).toList();
    }

    public ItemStack getBook() {
        return recipe.getBookForLevel(level);
    }

    public int getLevel() {
        return level;
    }

    public Holder<Enchantment> getEnchantment() {
        return recipe.enchantment();
    }

    public int getCost() {
        return recipe.getXPCostForLevel(level);
    }

    @Override
    public boolean matches(EnchanterRecipe.Input recipeInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(EnchanterRecipe.Input recipeInput, RegistryAccess registryAccess) {
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
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return recipe.getType();
    }
}
