package com.enderio.machines.common.blocks.enchanter;

import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

/**
 * A recipe for the enchanter.
 */
public record EnchanterRecipe(Holder<Enchantment> enchantment, int costMultiplier, SizedIngredient input)
        implements Recipe<EnchanterRecipe.Input> {

    // region Calculations

    /**
     * Get the enchantment level based on the number of the input ingredient.
     */
    public int getEnchantmentLevel(int ingredientCount) {
        return Math.min(ingredientCount / input.count(), enchantment.value().getMaxLevel());
    }

    /**
     * Get the amount of lapis required for the given level.
     */
    public int getLapisForLevel(int level) {
        int res = enchantment.value().getMaxLevel() == 1 ? 5 : level;
        return Math.max(1, Math.round(res * MachinesConfig.COMMON.ENCHANTER_LAPIS_COST_FACTOR.get().floatValue()));
    }

    /**
     * Get the number of ingredients to be consumed when crafting.
     * Basically just determines the exact amount of the ingredient to take, rather than just taking everything provided.
     */
    public int getInputAmountConsumed(Input recipeInput) {
        if (matches(recipeInput, null)) {
            return getEnchantmentLevel(recipeInput.getItem(1).getCount()) * input.count();
        }
        return 0;
    }

    /**
     * Get the XP level cost of the recipe.
     */
    public int getXPCost(Input recipeInput) {
        int level = getEnchantmentLevel(recipeInput.getItem(1).getCount());
        return getXPCostForLevel(level);
    }

    /**
     * Get the XP cost for crafting at the given level.
     */
    public int getXPCostForLevel(int level) {
        level = Math.min(level, enchantment.value().getMaxLevel());
        int cost = getRawXPCostForLevel(level);
        if (level < enchantment.value().getMaxLevel()) {
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
        double min = Math.max(1, enchantment.value().getMinCost(level));
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
    public boolean matches(Input recipeInput, @Nullable Level level) {
        ItemStack book = recipeInput.getItem(0);
        if (!book.is(Items.WRITABLE_BOOK)) {
            return false;
        }

        ItemStack catalyst = recipeInput.getItem(1);
        if (!input.test(catalyst) || catalyst.getCount() < input.count()) {
            return false;
        }

        ItemStack lapis = recipeInput.getItem(2);
        return lapis.is(Tags.Items.GEMS_LAPIS)
                && lapis.getCount() >= getLapisForLevel(getEnchantmentLevel(catalyst.getCount()));
    }

    @Override
    public ItemStack assemble(Input recipeInput, HolderLookup.Provider lookupProvider) {
        return getBookForLevel(getEnchantmentLevel(recipeInput.getItem(1).getCount()));
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookupProvider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.ENCHANTING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.ENCHANTING.type().get();
    }

    public record Input(ItemStack bookItem, ItemStack catalyst, ItemStack lapis) implements RecipeInput {

        @Override
        public ItemStack getItem(int slotIndex) {
            return switch (slotIndex) {
            case 0 -> bookItem;
            case 1 -> catalyst;
            case 2 -> lapis;
            default -> throw new IllegalArgumentException("No item for index " + slotIndex);
            };
        }

        @Override
        public int size() {
            return 3;
        }
    }

    public static class Serializer implements RecipeSerializer<EnchanterRecipe> {
        public static final MapCodec<EnchanterRecipe> CODEC = RecordCodecBuilder
                .mapCodec(inst -> inst
                        .group(Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchanterRecipe::enchantment),
                                ExtraCodecs.POSITIVE_INT.fieldOf("cost_multiplier")
                                        .forGetter(EnchanterRecipe::costMultiplier),
                                SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(EnchanterRecipe::input))
                        .apply(inst, EnchanterRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EnchanterRecipe> STREAM_CODEC = StreamCodec.composite(
                Enchantment.STREAM_CODEC, EnchanterRecipe::enchantment, ByteBufCodecs.INT,
                EnchanterRecipe::costMultiplier, SizedIngredient.STREAM_CODEC, EnchanterRecipe::input,
                EnchanterRecipe::new);

        @Override
        public MapCodec<EnchanterRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EnchanterRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
