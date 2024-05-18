package com.enderio.machines.common.recipe;

import com.enderio.base.common.paint.BlockPaintData;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blockentity.PaintingMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;

public record PaintingRecipe(
    Ingredient input,
    Item output
) implements MachineRecipe<RecipeWrapper> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public boolean matches(RecipeWrapper container, Level pLevel) {
        return input.test(PaintingMachineBlockEntity.INPUT.getItemStack(container)) && !PaintingMachineBlockEntity.PAINT.getItemStack(container).isEmpty();
    }

    @Override
    public int getBaseEnergyCost() {
        return MachinesConfig.COMMON.ENERGY.PAINTING_MACHINE_ENERGY_COST.get();
    }

    @Override
    public List<OutputStack> craft(RecipeWrapper container, RegistryAccess registryAccess) {
        List<OutputStack> outputs = new ArrayList<>();
        ItemStack outputStack = new ItemStack(output);

        var paintItem = PaintingMachineBlockEntity.PAINT.getItemStack(container);
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
        return List.of(OutputStack.of(output.getDefaultInstance()));
    }

    @Override
    public ItemStack assemble(RecipeWrapper container, HolderLookup.Provider lookupProvider) {
        return null;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookupProvider) {
        return new ItemStack(output);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.PAINTING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.PAINTING.type().get();
    }


    public static class Serializer implements RecipeSerializer<PaintingRecipe> {

        public static final MapCodec<PaintingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("input").forGetter(PaintingRecipe::input),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("output").forGetter(PaintingRecipe::output)
        ).apply(instance, PaintingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, PaintingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            PaintingRecipe::input,
            ByteBufCodecs.registry(Registries.ITEM),
            PaintingRecipe::output,
            PaintingRecipe::new
        );

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
