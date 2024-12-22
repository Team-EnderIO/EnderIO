package com.enderio.machines.common.blocks.painting;

import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.paint.BlockPaintData;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blocks.base.MachineRecipe;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record PaintingRecipe(Ingredient input, ItemStack output) implements MachineRecipe<PaintingRecipe.Input> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public boolean matches(Input recipeInput, Level pLevel) {
        return input.test(recipeInput.getItem(0)) && !recipeInput.getItem(1).isEmpty();
    }

    @Override
    public int getBaseEnergyCost() {
        return MachinesConfig.COMMON.ENERGY.PAINTING_MACHINE_ENERGY_COST.get();
    }

    @Override
    public List<OutputStack> craft(Input recipeInput, RegistryAccess registryAccess) {
        List<OutputStack> outputs = new ArrayList<>();
        ItemStack outputStack = output.copy();

        var paintItem = recipeInput.getItem(1);
        if (!(paintItem.getItem() instanceof BlockItem blockItem)) {
            throw new IllegalStateException("The item must be a block item.");
        }

        var paintBlock = blockItem.getBlock();
        outputStack.set(EIODataComponents.BLOCK_PAINT, BlockPaintData.of(paintBlock));

        outputs.add(OutputStack.of(outputStack));
        return outputs;
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output.copy()));
    }

    @Override
    public ItemStack assemble(Input recipeInput, HolderLookup.Provider lookupProvider) {
        return null;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookupProvider) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.PAINTING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.PAINTING.type().get();
    }

    public record Input(ItemStack template, ItemStack paint) implements RecipeInput {

        @Override
        public ItemStack getItem(int slotIndex) {
            return switch (slotIndex) {
            case 0 -> template;
            case 1 -> paint;
            default -> throw new IllegalArgumentException("No item for index " + slotIndex);
            };
        }

        @Override
        public int size() {
            return 2;
        }
    }

    public static class Serializer implements RecipeSerializer<PaintingRecipe> {

        public static final MapCodec<PaintingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(Ingredient.CODEC.fieldOf("input").forGetter(PaintingRecipe::input),
                        ItemStack.CODEC.fieldOf("output").forGetter(PaintingRecipe::output))
                .apply(instance, PaintingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, PaintingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, PaintingRecipe::input, ItemStack.STREAM_CODEC, PaintingRecipe::output,
                PaintingRecipe::new);

        @Override
        public MapCodec<PaintingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PaintingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
