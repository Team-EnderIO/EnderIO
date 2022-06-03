package com.enderio.machines.common.recipe;

import com.enderio.base.config.machines.MachinesConfig;
import com.enderio.core.recipes.EnderRecipe;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonObject;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class EnchanterRecipe implements EnderRecipe<Container> {

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

    // region Get recipe parameters

    public Ingredient getInput() {
        return input;
    }

    public int getLevelModifier() {
        return levelModifier;
    }

    public int getInputAmountPerLevel() {
        return inputAmountPerLevel;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    // endregion

    // region Calculations

    public int getLevelCost(Container container) {
        int level = getEnchantmentLevel(container.getItem(1).getCount());
        return getEnchantCost(level);
    }

    public int getEnchantmentLevel(int ingredientCount) {
        return Math.min(ingredientCount / inputAmountPerLevel, enchantment.getMaxLevel());
    }

    public int getLapisForLevel(int level) {
        int res = getEnchantment().getMaxLevel() == 1 ? 5 : level;
        return Math.max(1, Math.round(res * MachinesConfig.COMMON.ENCHANTER_LAPIS_COST_FACTOR.get()));
    }

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

    public ItemStack getBookForLevel(int level) {
        return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, level));
    }


    // endregion

    @Override
    public boolean matches(Container container, @Nullable Level level) {
        if (!container.getItem(0).is(Items.WRITABLE_BOOK)) {
            return false;
        }
        if (!input.test(container.getItem(1)) || container.getItem(1).getCount() < inputAmountPerLevel) {
            return false;
        }
        if (!container.getItem(2).is(Tags.Items.GEMS_LAPIS) || container.getItem(2).getCount() < getLapisForLevel(
            getEnchantmentLevel(container.getItem(1).getCount()))) {
            return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(Container container) {
        return getBookForLevel(getEnchantmentLevel(container.getItem(1).getCount()));
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.Serializer.ENCHANTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.Types.ENCHANTING;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<EnchanterRecipe> {

        @Override
        public EnchanterRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            Ingredient ingredient = Ingredient.fromJson(serializedRecipe.get("input").getAsJsonObject());
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(serializedRecipe.get("enchantment").getAsString()));
            if (enchantment == null) {
                throw new ResourceLocationException("The enchantment in " + recipeId + " does not exist");
            }
            int amount = serializedRecipe.get("amount_per_level").getAsInt();
            int level = serializedRecipe.get("level").getAsInt();
            return new EnchanterRecipe(recipeId, ingredient, enchantment, amount, level);
        }

        @Nullable
        @Override
        public EnchanterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(buffer.readResourceLocation());
            if (enchantment == null) {
                throw new ResourceLocationException("The enchantment in " + recipeId + " does not exist");
            }
            int amount = buffer.readInt();
            int level = buffer.readInt();
            return new EnchanterRecipe(recipeId, ingredient, enchantment, amount, level);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, EnchanterRecipe recipe) {
            recipe.getInput().toNetwork(buffer);
            buffer.writeResourceLocation(Objects.requireNonNull(recipe.getEnchantment().getRegistryName()));
            buffer.writeInt(recipe.getInputAmountPerLevel());
            buffer.writeInt(recipe.getLevelModifier());
        }
    }
}
