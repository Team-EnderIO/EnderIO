package com.enderio.machines.common.recipe;

import com.enderio.base.config.machines.MachinesConfig;
import com.enderio.core.recipes.EnderRecipe;
import com.enderio.machines.EIOMachines;
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

/**
 * A recipe for the enchanter.
 */
public class EnchanterRecipe implements EnderRecipe<Container> {

    private final ResourceLocation id;
    private final Enchantment enchantment;
    private final int costMultiplier;
    private final Ingredient input;
    private final int amountPerLevel;

    public EnchanterRecipe(ResourceLocation id, Ingredient input, Enchantment enchantment, int amountPerLevel, int costMultiplier) {
        this.id = id;
        this.input = input;
        this.enchantment = enchantment;
        this.amountPerLevel = amountPerLevel;
        this.costMultiplier = costMultiplier;
    }

    // region Get recipe parameters

    /**
     * Get the input ingredient.
     */
    public Ingredient getInput() {
        return input;
    }

    /**
     * Get the XP multiplier.
     */
    public int getCostMultiplier() {
        return costMultiplier;
    }

    /**
     * Get the input amount per level.
     */
    public int getAmountPerLevel() {
        return amountPerLevel;
    }

    /**
     * Get the resuting enchantment.
     */
    public Enchantment getEnchantment() {
        return enchantment;
    }

    // endregion

    // region Calculations

    /**
     * Get the enchantment level based on the number of the input ingredient.
     */
    public int getEnchantmentLevel(int ingredientCount) {
        return Math.min(ingredientCount / amountPerLevel, enchantment.getMaxLevel());
    }

    /**
     * Get the amount of lapis required for the given level.
     */
    public int getLapisForLevel(int level) {
        int res = getEnchantment().getMaxLevel() == 1 ? 5 : level;
        return Math.max(1, Math.round(res * MachinesConfig.COMMON.ENCHANTER_LAPIS_COST_FACTOR.get()));
    }

    /**
     * Get the number of ingredients to be consumed when crafting.
     * Basically just determines the exact amount of the ingredient to take, rather than just taking everything provided.
     */
    public int getInputAmountConsumed(Container container) {
        if (matches(container, null)) {
            return getEnchantmentLevel(container.getItem(1).getCount()) * this.amountPerLevel;
        }
        return 0;
    }

    /**
     * Get the XP level cost of the recipe.
     */
    public int getXPCost(Container container) {
        int level = getEnchantmentLevel(container.getItem(1).getCount());
        return getXPCostForLevel(level);
    }

    /**
     * Get the XP cost for crafting at the given level.
     */
    public int getXPCostForLevel(int level) {
        level = Math.min(level, enchantment.getMaxLevel());
        int cost = getRawXPCostForLevel(level);
        if (level < enchantment.getMaxLevel()) {
            // min cost of half the next levels XP cause books combined in anvil
            int nextCost = getXPCostForLevel(level + 1);
            cost = Math.max(nextCost / 2, cost);
        }
        return Math.max(1, cost);
    }

    /**
     * Get the raw xp cost for the given level.
     */
    private int getRawXPCostForLevel(int level) {
        double min = Math.max(1, enchantment.getMinCost(level));
        min *= costMultiplier;
        int cost = (int) Math.round(min * MachinesConfig.COMMON.ENCHANTER_LEVEL_COST_FACTOR.get());
        cost += MachinesConfig.COMMON.ENCHANTER_BASE_LEVEL_COST.get();
        return cost;
    }

    /**
     * Get the enchanted book with the correct enchantment of level.
     */
    public ItemStack getBookForLevel(int level) {
        return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, level));
    }

    // endregion

    @Override
    public boolean matches(Container container, @Nullable Level level) {
        if (!container.getItem(0).is(Items.WRITABLE_BOOK)) {
            return false;
        }
        if (!input.test(container.getItem(1)) || container.getItem(1).getCount() < amountPerLevel) {
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
            int amountPerLevel = serializedRecipe.get("amount").getAsInt();
            int costMultiplier = serializedRecipe.get("cost_multiplier").getAsInt();
            return new EnchanterRecipe(recipeId, ingredient, enchantment, amountPerLevel, costMultiplier);
        }

        @Nullable
        @Override
        public EnchanterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            try {
                Ingredient ingredient = Ingredient.fromNetwork(buffer);
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(buffer.readResourceLocation());
                if (enchantment == null) {
                    throw new ResourceLocationException("The enchantment in " + recipeId + " does not exist");
                }
                int amount = buffer.readInt();
                int level = buffer.readInt();
                return new EnchanterRecipe(recipeId, ingredient, enchantment, amount, level);
            } catch (Exception ex) {
                EIOMachines.LOGGER.error("Error reading enchanter recipe from packet.", ex);
                throw ex;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, EnchanterRecipe recipe) {
            try {
                recipe.getInput().toNetwork(buffer);
                buffer.writeResourceLocation(Objects.requireNonNull(recipe.getEnchantment().getRegistryName()));
                buffer.writeInt(recipe.getAmountPerLevel());
                buffer.writeInt(recipe.getCostMultiplier());
            } catch (Exception ex) {
                EIOMachines.LOGGER.error("Error writing enchanter recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
