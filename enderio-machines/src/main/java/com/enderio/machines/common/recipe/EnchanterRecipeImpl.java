package com.enderio.machines.common.recipe;

import com.enderio.api.recipe.DataGenSerializer;
import com.enderio.api.recipe.EnchanterRecipe;
import com.enderio.base.config.machines.MachinesConfig;
import com.enderio.machines.common.init.MachineRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchanterRecipeImpl extends EnchanterRecipe {
    public EnchanterRecipeImpl(ResourceLocation id, Ingredient input, Enchantment enchantment, int amountPerLevel, int levelModifier) {
        super(id, input, enchantment, amountPerLevel, levelModifier);
    }

    @Override
    public int getLapisForLevel(int level) {
        int res = getEnchantment().getMaxLevel() == 1 ? 5 : level;
        return Math.max(1, Math.round(res * MachinesConfig.COMMON.ENCHANTER_LAPIS_COST_FACTOR.get()));
    }

    @Override
    public DataGenSerializer<EnchanterRecipe, Container> getSerializer() {
        return MachineRecipes.ENCHANTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.ENCHANTING.get();
    }
}
