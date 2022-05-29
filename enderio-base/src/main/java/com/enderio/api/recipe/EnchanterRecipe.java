package com.enderio.api.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A recipe for the enchanter.
 * Takes in an ingredient, some XP and some lapis lazuli and in turn enchants an item with the given enchantment.
 */
public abstract class EnchanterRecipe implements IEnderRecipe<Container> {
    private final ResourceLocation id;
    private final Enchantment enchantment;
    private final int levelModifier;
    private final Ingredient input;
    private final int inputAmountPerLevel;

    public EnchanterRecipe(ResourceLocation id, Ingredient input, Enchantment enchantment, int amountPerLevel, int levelModifier) {
        this.id = id;
        this.input = input;
        this.enchantment = enchantment;
        this.inputAmountPerLevel = amountPerLevel;
        this.levelModifier = levelModifier;
    }

    /**
     * Get the enchantment provided by the recipe.
     */
    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    /**
     * Get the input ingredient for the recipe.
     */
    public Ingredient getInput() {return this.input;}

    /**
     * Get the XP level modifier
     */
    public int getLevelModifier() {
        return levelModifier;
    }

    /**
     * Get the amount of the ingredient per level
     */
    public int getInputAmountPerLevel() {
        return inputAmountPerLevel;
    }

    public int getLevelCost(Container container) {
        int level = getEnchantmentLevel(container.getItem(1).getCount());
        return getEnchantCost(level);
    }

    public int getEnchantmentLevel(int ingredientCount) {
        return Math.min(ingredientCount / inputAmountPerLevel, enchantment.getMaxLevel());
    }

    public abstract int getLapisForLevel(int level);

    public int getAmount(Container container) {
        if (matches(container, null)) {
            return getEnchantmentLevel(container.getItem(1).getCount()) * this.inputAmountPerLevel;
        }
        return 0;
    }

    public int getEnchantCost(int level) {
        level = Math.min(level, enchantment.getMaxLevel());
        int cost = getRawXPCostForLevel(level);
        if (level < enchantment.getMaxLevel()) {
            // min cost of half the next levels XP cause books combined in anvil
            int nextCost = getEnchantCost(level + 1);
            cost = Math.max(nextCost / 2, cost);
        }
        return Math.max(1, cost);
    }

    private int getRawXPCostForLevel(int level) {
        double min = Math.max(1, enchantment.getMinCost(level));
        min *= levelModifier;
        int cost = (int) Math.round(min * 1); //TODO global scaling
        cost += 1; //TODO base cost
        return cost;
    }

    @Override
    public List<List<ItemStack>> getAllInputs() {
        return Collections.singletonList(Arrays.stream(input.getItems()).toList());
    }

    @Override
    public List<ItemStack> getAllOutputs() {
        return List.of();
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if (!pContainer.getItem(0).is(Items.WRITABLE_BOOK)) {
            return false;
        }
        if (!input.test(pContainer.getItem(1)) || pContainer.getItem(1).getCount() < inputAmountPerLevel) {
            return false;
        }
        if (!pContainer.getItem(2).is(Tags.Items.GEMS_LAPIS) || pContainer.getItem(2).getCount() < getLapisForLevel(
            getEnchantmentLevel(pContainer.getItem(1).getCount()))) {
            return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(Container pContainer) {
        return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, getEnchantmentLevel(pContainer.getItem(1).getCount())));
    }

    @Override
    public void consumeInputs(Container container) {
        // TODO
    }

    @Override
    public List<ItemStack> craft(Container container) {
        return List.of(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, getEnchantmentLevel(container.getItem(1).getCount()))));
    }

    @Override
    public int getOutputCount(Container container) {
        return 1;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(input);
        return ingredients;
    }

    @Override
    public List<ResourceLocation> getMiscModDependencies() {
        return List.of(enchantment.getRegistryName());
    }
}