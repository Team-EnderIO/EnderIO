package com.enderio.machines.common.integration.jei.helpers;

import com.enderio.api.recipe.EnchanterRecipe;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnchanterRecipeWrapper {
    private final List<List<ItemStack>> inputs;
    private final ItemStack output;
    private final EnchanterRecipe recipe;

    public EnchanterRecipeWrapper(EnchanterRecipe recipe, int level) {
        this.recipe = recipe;

        List<ItemStack> leftInputs = new ArrayList<>();
        List<ItemStack> rightInputs = new ArrayList<>();
        for (ItemStack input : recipe.getInput().getItems()) {
            leftInputs.add(new ItemStack(input.getItem(), recipe.getInputAmountPerLevel() * level));
            rightInputs.add(new ItemStack(Items.LAPIS_LAZULI, recipe.getLapisForLevel(level)));
        }

        this.inputs = ImmutableList.of(
            Collections.singletonList(new ItemStack(Items.WRITABLE_BOOK)),
            leftInputs,
            rightInputs
        );
        this.output = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(recipe.getEnchantment(), level));
    }

    public List<List<ItemStack>> getInputs() {
        return inputs;
    }

    public ItemStack getOutput() {
        return output;
    }

    public EnchanterRecipe getRecipe() {
        return recipe;
    }

    public int getLevelCost(ItemStack left, ItemStack right) {
        int level = recipe.getEnchantmentLevel(left.getCount());
        return recipe.getEnchantCost(level);
    }
}