package com.enderio.machines.common.blocks.slicer;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blocks.base.MachineRecipe;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.utility.ValidatingListCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record SlicingRecipe(ItemStack output, List<Ingredient> inputs, int energy)
        implements MachineRecipe<SlicingRecipe.Input> {

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
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, inputs.toArray(new Ingredient[0]));
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
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.SLICING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.SLICING.type().get();
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

    public static class Serializer implements RecipeSerializer<SlicingRecipe> {
        public static final MapCodec<SlicingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(ItemStack.CODEC.fieldOf("output").forGetter(SlicingRecipe::output),
                        new ValidatingListCodec<>(Ingredient.LIST_CODEC, 6).fieldOf("inputs")
                                .forGetter(SlicingRecipe::inputs),
                        Codec.INT.fieldOf("energy").forGetter(SlicingRecipe::energy))
                .apply(instance, SlicingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SlicingRecipe> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, SlicingRecipe::output,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), SlicingRecipe::inputs, ByteBufCodecs.INT,
                SlicingRecipe::energy, SlicingRecipe::new);

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
