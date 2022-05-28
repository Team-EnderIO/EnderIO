package com.enderio.machines.common.compat.jei.helpers;

import mezz.jei.api.gui.ingredient.IGuiIngredient;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;

public class EnchanterRecipeDisplayData {
    @Nullable
    private Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients = null;
    @Nullable
    private ItemStack lastLeftStack;
    @Nullable
    private ItemStack lastRightStack;
    private int lastCost;

    @Nullable
    public Map<Integer, ? extends IGuiIngredient<ItemStack>> getCurrentIngredients() {
        return currentIngredients;
    }

    public void setCurrentIngredients(Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients) {
        this.currentIngredients = currentIngredients;
    }
    @Nullable
    public ItemStack getLastLeftStack() {
        return lastLeftStack;
    }

    @Nullable
    public ItemStack getLastRightStack() {
        return lastRightStack;
    }

    public int getLastCost() {
        return lastCost;
    }

    public void setLast(ItemStack leftStack, ItemStack rightStack, int lastCost) {
        this.lastLeftStack = leftStack;
        this.lastRightStack = rightStack;
        this.lastCost = lastCost;
    }
}