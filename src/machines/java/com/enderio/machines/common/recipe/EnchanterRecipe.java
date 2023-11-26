package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.CountedIngredient;
import com.enderio.core.common.recipes.EnderRecipe;
import com.enderio.machines.common.blockentity.EnchanterBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonObject;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A recipe for the enchanter.
 */
public class EnchanterRecipe implements EnderRecipe<Container> {

    private final ResourceLocation id;
    private final Enchantment enchantment;
    private final int costMultiplier;
    private final CountedIngredient input;

    public EnchanterRecipe(ResourceLocation id, CountedIngredient input, Enchantment enchantment, int costMultiplier) {
        this.id = id;
        this.input = input;
        this.enchantment = enchantment;
        this.costMultiplier = costMultiplier;
    }

    // region Get recipe parameters

    /**
     * Get the input ingredient.
     */
    public CountedIngredient getInput() {
        return input;
    }

    /**
     * Get the XP multiplier.
     */
    public int getCostMultiplier() {
        return costMultiplier;
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
        return Math.min(ingredientCount / input.count(), enchantment.getMaxLevel());
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
            return getEnchantmentLevel(EnchanterBlockEntity.CATALYST.getItemStack(container).getCount()) * input.count();
        }
        return 0;
    }

    /**
     * Get the XP level cost of the recipe.
     */
    public int getXPCost(Container container) {
        int level = getEnchantmentLevel(EnchanterBlockEntity.CATALYST.getItemStack(container).getCount());
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
        if (!EnchanterBlockEntity.BOOK.getItemStack(container).is(Items.WRITABLE_BOOK)) {
            return false;
        }
        if (!input.test(EnchanterBlockEntity.CATALYST.getItemStack(container)) || EnchanterBlockEntity.CATALYST.getItemStack(container).getCount() < input.count()) {
            return false;
        }
        return EnchanterBlockEntity.LAPIS.getItemStack(container).is(Tags.Items.GEMS_LAPIS) && EnchanterBlockEntity.LAPIS.getItemStack(container).getCount() >= getLapisForLevel(
            getEnchantmentLevel(EnchanterBlockEntity.CATALYST.getItemStack(container).getCount()));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return getBookForLevel(getEnchantmentLevel(EnchanterBlockEntity.CATALYST.getItemStack(container).getCount()));
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.ENCHANTING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.ENCHANTING.type().get();
    }

    public static class Serializer implements RecipeSerializer<EnchanterRecipe> {

        @Override
        public EnchanterRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            CountedIngredient input = CountedIngredient.fromJson(serializedRecipe.get("input").getAsJsonObject());
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(serializedRecipe.get("enchantment").getAsString()));
            if (enchantment == null) {
                throw new ResourceLocationException("The enchantment in " + recipeId + " does not exist");
            }
            int costMultiplier = serializedRecipe.get("cost_multiplier").getAsInt();
            return new EnchanterRecipe(recipeId, input, enchantment, costMultiplier);
        }

        @Nullable
        @Override
        public EnchanterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            try {
                CountedIngredient input = CountedIngredient.fromNetwork(buffer);
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(buffer.readResourceLocation());
                if (enchantment == null) {
                    throw new ResourceLocationException("The enchantment in " + recipeId + " does not exist");
                }
                int level = buffer.readInt();
                return new EnchanterRecipe(recipeId, input, enchantment, level);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error reading enchanter recipe from packet.", ex);
                throw ex;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, EnchanterRecipe recipe) {
            try {
                recipe.getInput().toNetwork(buffer);
                buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getKey(recipe.getEnchantment())));
                buffer.writeInt(recipe.getCostMultiplier());
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing enchanter recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
