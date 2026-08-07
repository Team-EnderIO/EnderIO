package com.enderio.enderio.content.machines.soul_binder;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.ingredient.SoulIngredient;
import com.enderio.enderio.foundation.MachineRecipe;
import com.enderio.enderio.foundation.recipe.FluidRecipeInput;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import com.enderio.enderio.init.EIORecipeBookCategories;
import com.enderio.enderio.init.EIORecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class SoulBindingRecipe implements MachineRecipe<SoulBindingRecipe.Input> {
    private static final MapCodec<SoulBindingRecipe> MAP_CODEC = RecordCodecBuilder.<SoulBindingRecipe>mapCodec(instance -> instance
        .group(
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(SoulBindingRecipe::output),
            Ingredient.CODEC.fieldOf("input").forGetter(SoulBindingRecipe::input),
            SoulIngredient.CODEC.fieldOf("inputSoul").forGetter(SoulBindingRecipe::inputSoul),
            Codec.INT.fieldOf("operation_time").forGetter(SoulBindingRecipe::operationTime),
            Codec.INT.fieldOf("experience").forGetter(SoulBindingRecipe::experience),
            Codec.BOOL.optionalFieldOf("copyInputComponents", false).forGetter(SoulBindingRecipe::copyInputComponents))
        .apply(instance, SoulBindingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoulBindingRecipe> STREAM_CODEC = StreamCodec.composite(
        ItemStackTemplate.STREAM_CODEC, SoulBindingRecipe::output,
        Ingredient.CONTENTS_STREAM_CODEC, SoulBindingRecipe::input,
        SoulIngredient.STREAM_CODEC, SoulBindingRecipe::inputSoul,
        ByteBufCodecs.INT, SoulBindingRecipe::operationTime,
        ByteBufCodecs.INT, SoulBindingRecipe::experience,
        ByteBufCodecs.BOOL, SoulBindingRecipe::copyInputComponents,
        SoulBindingRecipe::new);

    public static final RecipeSerializer<SoulBindingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final ItemStackTemplate output;
    private final Ingredient input;
    private final SoulIngredient inputSoul;
    private final int operationTime;
    private final int experience;
    private final boolean copyInputComponents;

    @Nullable
    private PlacementInfo placementInfo;

    public SoulBindingRecipe(ItemStackTemplate output, Ingredient input, SoulIngredient inputSoul, int operationTime, int experience, boolean copyInputComponents) {
        this.output = output;
        this.input = input;
        this.inputSoul = inputSoul;
        this.operationTime = operationTime;
        this.experience = experience;
        this.copyInputComponents = copyInputComponents;
    }

    public ItemStackTemplate output() {
        return output;
    }

    public Ingredient input() {
        return input;
    }

    public SoulIngredient inputSoul() {
        return inputSoul;
    }

    public int operationTime() {
        return operationTime;
    }

    public int experience() {
        return experience;
    }

    @Override
    public int getOperationTime(Input input) {
        return operationTime;
    }

    @Override
    public List<OutputStack> craft(Input input, RandomSource randomSource, RegistryAccess registryAccess) {
        ItemStack vial = input.getItem(0);

        List<OutputStack> results = getResultStacks(registryAccess);
        ItemStack result = results.getFirst().getItem();

        if (copyInputComponents) {
            result.applyComponents(input.itemToBind.getComponents());
        }

        var vialSoulBindable = ItemAccess.forStack(vial).getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        var resultSoulBinding = ItemAccess.forStack(result).getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);

        if (vialSoulBindable != null && resultSoulBinding != null) {
            // TODO: Maybe determine the result in the ctor?
            try (Transaction transaction = Transaction.openRoot()) {
                resultSoulBinding.bindSoul(vialSoulBindable.boundSoul(), transaction);
                transaction.commit();
            }
        }

        return results;
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output.create()), OutputStack.of(EIOItems.SOUL_VIAL.get().getDefaultInstance()));
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new SoulBindingDisplay(input.display(), new SlotDisplay.ItemStackSlotDisplay(output),
            new SlotDisplay.ItemSlotDisplay(EIOBlocks.SOUL_BINDER.asItem())));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EIORecipeBookCategories.SOUL_BINDING.get();
    }

    @Override
    public boolean matches(Input recipeInput, Level level) {
        if (!recipeInput.getItem(0).is(EIOItems.SOUL_VIAL.get())) {
            return false;
        }

        if (!input.test(recipeInput.getItem(1))) {
            return false;
        }

        var soulBindable = ItemAccess.forStack(recipeInput.getItem(0)).getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        if (soulBindable == null || !soulBindable.isBound()) {
            return false;
        }

        var soul = soulBindable.boundSoul();
        if (!inputSoul.test(soul)) {
            return false;
        }

        return ExperienceUtil.getLevelFromFluid(recipeInput.getFluid(2).getAmount()) >= experience;
    }

    @Override
    public RecipeSerializer<? extends Recipe<Input>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<Input>> getType() {
        return EIORecipeTypes.SOUL_BINDING.get();
    }

    public boolean copyInputComponents() {
        return copyInputComponents;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.create(input);
        }

        return placementInfo;
    }

    public record Input(ItemStack boundSoulItem, ItemStack itemToBind, FluidStack experience) implements FluidRecipeInput {
        @Override
        public ItemStack getItem(int slotIndex) {
            return switch (slotIndex) {
                case 0 -> boundSoulItem;
                case 1 -> itemToBind;
                case 2 -> ItemStack.EMPTY;
                default -> throw new IllegalArgumentException("No item for index " + slotIndex);
            };
        }

        @Override
        public FluidStack getFluid(int slotIndex) {
            if (slotIndex != 2) {
                throw new IllegalArgumentException("No fluid for index " + slotIndex);
            }

            return experience;
        }

        @Override
        public int size() {
            return 3;
        }
    }

    public record SoulBindingDisplay(SlotDisplay ingredient, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {

        public static final MapCodec<SoulBindingDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(p_379634_ -> p_379634_
            .group(SlotDisplay.CODEC.fieldOf("ingredients").forGetter(SoulBindingDisplay::ingredient),
                SlotDisplay.CODEC.fieldOf("result").forGetter(SoulBindingDisplay::result),
                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(SoulBindingDisplay::craftingStation))
            .apply(p_379634_, SoulBindingDisplay::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SoulBindingDisplay> STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC,
            SoulBindingDisplay::ingredient, SlotDisplay.STREAM_CODEC, SoulBindingDisplay::result, SlotDisplay.STREAM_CODEC, SoulBindingDisplay::craftingStation,
            SoulBindingDisplay::new);
        public static final Type<SoulBindingDisplay> TYPE = new Type<>(MAP_CODEC, STREAM_CODEC);

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
