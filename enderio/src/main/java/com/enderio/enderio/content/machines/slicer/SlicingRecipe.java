package com.enderio.enderio.content.machines.slicer;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.enderio.foundation.MachineRecipe;
import com.enderio.enderio.foundation.util.ValidatingListCodec;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIORecipeBookCategories;
import com.enderio.enderio.init.EIORecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class SlicingRecipe implements MachineRecipe<SlicingRecipe.Input> {
    private final ItemStack output;
    private final List<Ingredient> inputs;
    private final int energy;

    @Nullable
    private PlacementInfo placementInfo;

    public SlicingRecipe(ItemStack output, List<Ingredient> inputs, int energy) {
        this.output = output;
        this.inputs = inputs;
        this.energy = energy;
    }

    public ItemStack output() {
        return output;
    }

    public List<Ingredient> inputs() {
        return inputs;
    }

    public int energy() {
        return energy;
    }

    @Override
    public int getBaseEnergyCost() {
        return energy;
    }

    @Override
    public List<OutputStack> craft(Input recipeInput, RegistryAccess registryAccess) {
        return getResultStacks(registryAccess);
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output.copy()));
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new SlicingDisplay(inputs.stream().map(Ingredient::display).toList(), new SlotDisplay.ItemStackSlotDisplay(output.copy()),
            new SlotDisplay.ItemSlotDisplay(EIOBlocks.SLICE_AND_SPLICE.asItem())));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EIORecipeBookCategories.SLICING.get();
    }

    @Override
    public boolean matches(Input recipeInput, Level level) {
        for (int i = 0; i < inputs.size(); i++) {
            if (!inputs.get(i).test(recipeInput.getItem(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<Input>> getSerializer() {
        return EIORecipes.SLICING.serializer().get();
    }

    @Override
    public RecipeType<? extends Recipe<Input>> getType() {
        return EIORecipes.SLICING.type().get();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.create(inputs);
        }

        return placementInfo;
    }

    public record Input(List<ItemStack> inputs) implements RecipeInput {

        @Override
        public ItemStack getItem(int slotIndex) {
            if (slotIndex >= inputs.size()) {
                throw new IllegalArgumentException("No item for index " + slotIndex);
            }

            return inputs.get(slotIndex);
        }

        @Override
        public int size() {
            return inputs.size();
        }
    }

    public record SlicingDisplay(List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {

        public static final MapCodec<SlicingDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(p_379634_ -> p_379634_
            .group(SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(SlicingDisplay::ingredients),
                SlotDisplay.CODEC.fieldOf("result").forGetter(SlicingDisplay::result), SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(SlicingDisplay::craftingStation))
            .apply(p_379634_, SlicingDisplay::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SlicingDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), SlicingDisplay::ingredients, SlotDisplay.STREAM_CODEC, SlicingDisplay::result,
            SlotDisplay.STREAM_CODEC, SlicingDisplay::craftingStation, SlicingDisplay::new);
        public static final Type<SlicingDisplay> TYPE = new Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public Type<? extends RecipeDisplay> type() {
            return TYPE;
        }

        @Override
        public boolean isEnabled(FeatureFlagSet flagSet) {
            return this.ingredients.stream().allMatch(i -> i.isEnabled(flagSet)) && RecipeDisplay.super.isEnabled(flagSet);
        }
    }

    public static class Serializer implements RecipeSerializer<SlicingRecipe> {
        public static final MapCodec<SlicingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(ItemStack.CODEC.fieldOf("output").forGetter(SlicingRecipe::output),
                new ValidatingListCodec<>(Ingredient.CODEC.listOf(), 6).fieldOf("inputs").forGetter(SlicingRecipe::inputs),
                Codec.INT.fieldOf("energy").forGetter(SlicingRecipe::energy))
            .apply(instance, SlicingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SlicingRecipe> STREAM_CODEC = StreamCodec.composite(ItemStack.STREAM_CODEC, SlicingRecipe::output,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), SlicingRecipe::inputs, ByteBufCodecs.INT, SlicingRecipe::energy, SlicingRecipe::new);

        @Override
        public MapCodec<SlicingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SlicingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
