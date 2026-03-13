package com.enderio.enderio.compat.jei_machines_to_merge.util;

import com.enderio.enderio.content.enchanter.EnchanterRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public class WrappedEnchanterRecipe implements Recipe<EnchanterRecipe.Input> {
    private final RecipeHolder<EnchanterRecipe> recipe;
    private final int level;

    public WrappedEnchanterRecipe(RecipeHolder<EnchanterRecipe> recipe, int level) {
        this.recipe = recipe;
        this.level = level;
    }

    public Identifier id() {
        return Identifier.fromNamespaceAndPath(recipe.id().identifier().getNamespace(), recipe.id().identifier().getPath() + "_" + level);
    }

    public List<ItemStack> getInputs() {
        int size = recipe.value().input().count() * level;
        return recipe.value().input().ingredient().getValues().stream().map(v -> new ItemStack(v.value(), size)).toList();
    }

    public List<ItemStack> getLapis() {
        return Ingredient.of(BuiltInRegistries.ITEM.get(Tags.Items.GEMS_LAPIS).orElseThrow()).getValues().stream().map(item -> new ItemStack(item.value(), recipe.value().getLapisForLevel(level))).toList();
    }

    public ItemStack getBook() {
        return recipe.value().getBookForLevel(level).create();
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
    public ItemStack assemble(EnchanterRecipe.Input recipeInput) {
        return recipe.value().assemble(recipeInput);
    }

    @Override
    public boolean showNotification() {
        return recipe.value().showNotification();
    }

    @Override
    public String group() {
        return recipe.value().group();
    }

    @Override
    public RecipeSerializer<? extends Recipe<EnchanterRecipe.Input>> getSerializer() {
        return recipe.value().getSerializer();
    }

    @Override
    public RecipeType<? extends Recipe<EnchanterRecipe.Input>> getType() {
        return recipe.value().getType();
    }

    @Override
    public PlacementInfo placementInfo() {
        return recipe.value().placementInfo();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return recipe.value().recipeBookCategory();
    }

    @Override
    public List<RecipeDisplay> display() {
        return recipe.value().display();
    }
}
