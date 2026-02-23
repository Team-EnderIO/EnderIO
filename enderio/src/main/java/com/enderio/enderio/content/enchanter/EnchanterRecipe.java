package com.enderio.enderio.content.enchanter;

import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIORecipeBookCategories;
import com.enderio.enderio.init.EIORecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * A recipe for the enchanter.
 */
public record EnchanterRecipe(Holder<Enchantment> enchantment, int costMultiplier, SizedIngredient input)
        implements Recipe<EnchanterRecipe.Input> {

    public static final MapCodec<EnchanterRecipe> MAP_CODEC = RecordCodecBuilder
        .mapCodec(inst -> inst
            .group(Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchanterRecipe::enchantment),
                ExtraCodecs.POSITIVE_INT.fieldOf("cost_multiplier")
                    .forGetter(EnchanterRecipe::costMultiplier),
                SizedIngredient.NESTED_CODEC.fieldOf("input").forGetter(EnchanterRecipe::input))
            .apply(inst, EnchanterRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchanterRecipe> STREAM_CODEC = StreamCodec.composite(
        Enchantment.STREAM_CODEC, EnchanterRecipe::enchantment, ByteBufCodecs.INT,
        EnchanterRecipe::costMultiplier, SizedIngredient.STREAM_CODEC, EnchanterRecipe::input,
        EnchanterRecipe::new);

    public static final RecipeSerializer<EnchanterRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

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
        return Items.BOOK.applyEnchantments(new ItemStack(Items.BOOK), List.of(new EnchantmentInstance(enchantment, level)));
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
    public ItemStack assemble(Input recipeInput) {
        return getBookForLevel(getEnchantmentLevel(recipeInput.getItem(1).getCount()));
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(input.ingredient());
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new EnchantingDisplay(input.ingredient().display(),
            new SlotDisplay.ItemStackSlotDisplay(getBookForLevel(getEnchantmentLevel(input.count()))), //TODO is this the right way?
            new SlotDisplay.ItemSlotDisplay(EIOBlocks.ENCHANTER.asItem())));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EIORecipeBookCategories.ENCHANTING.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends Recipe<Input>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<Input>> getType() {
        return EIORecipes.ENCHANTING.get();
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

    public record EnchantingDisplay(SlotDisplay ingredient, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {

        public static final MapCodec<EnchantingDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_379634_ -> p_379634_.group(
                    SlotDisplay.CODEC.fieldOf("ingredients").forGetter(EnchantingDisplay::ingredient),
                    SlotDisplay.CODEC.fieldOf("result").forGetter(EnchantingDisplay::result),
                    SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(EnchantingDisplay::craftingStation)
                )
                .apply(p_379634_, EnchantingDisplay::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, EnchantingDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC,
            EnchantingDisplay::ingredient,
            SlotDisplay.STREAM_CODEC,
            EnchantingDisplay::result,
            SlotDisplay.STREAM_CODEC,
            EnchantingDisplay::craftingStation,
            EnchantingDisplay::new
        );
        public static final RecipeDisplay.Type<EnchantingDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public Type<? extends RecipeDisplay> type() {
            return TYPE;
        }

        @Override
        public boolean isEnabled(FeatureFlagSet flagSet) {
            return this.ingredient.isEnabled(flagSet) && RecipeDisplay.super.isEnabled(flagSet);
        }
    }
}
