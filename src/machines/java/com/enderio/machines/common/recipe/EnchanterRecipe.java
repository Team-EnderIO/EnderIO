package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.CountedIngredient;
import com.enderio.core.common.recipes.EnderRecipe;
import com.enderio.machines.common.blockentity.EnchanterBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
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
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A recipe for the enchanter.
 */
public record EnchanterRecipe(
    Enchantment enchantment,
    int costMultiplier,
    CountedIngredient input
) implements EnderRecipe<Container> {

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
        int res = enchantment.getMaxLevel() == 1 ? 5 : level;
        return Math.max(1, Math.round(res * MachinesConfig.COMMON.ENCHANTER_LAPIS_COST_FACTOR.get().floatValue()));
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
    public ItemStack assemble(Container container, HolderLookup.Provider lookupProvider) {
        return getBookForLevel(getEnchantmentLevel(EnchanterBlockEntity.CATALYST.getItemStack(container).getCount()));
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookupProvider) {
        return ItemStack.EMPTY;
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
        public static final MapCodec<EnchanterRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
            .group(
                BuiltInRegistries.ENCHANTMENT.byNameCodec().fieldOf("enchantment").forGetter(EnchanterRecipe::enchantment),
                ExtraCodecs.POSITIVE_INT.fieldOf("cost_multiplier").forGetter(EnchanterRecipe::costMultiplier),
                CountedIngredient.CODEC.fieldOf("input").forGetter(EnchanterRecipe::input))
            .apply(inst, EnchanterRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EnchanterRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ENCHANTMENT),
            EnchanterRecipe::enchantment,
            ByteBufCodecs.INT,
            EnchanterRecipe::costMultiplier,
            CountedIngredient.STREAM_CODEC,
            EnchanterRecipe::input,
            EnchanterRecipe::new
        );

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
