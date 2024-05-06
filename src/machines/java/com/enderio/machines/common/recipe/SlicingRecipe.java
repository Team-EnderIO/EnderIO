package com.enderio.machines.common.recipe;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.utility.ValidatingListCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public record SlicingRecipe(
    Item output,
    List<Ingredient> inputs,
    int energy
) implements MachineRecipe<Container> {

    @Override
    public int getBaseEnergyCost() {
        return energy;
    }

    @Override
    public List<OutputStack> craft(Container container, RegistryAccess registryAccess) {
        return getResultStacks(registryAccess);
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(new ItemStack(output, 1)));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, inputs.toArray(new Ingredient[0]));
    }

    @Override
    public boolean matches(Container container, Level level) {
        for (int i = 0; i < inputs.size(); i++) {
            if (!inputs.get(i).test(container.getItem(i))) {
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

    public static class Serializer implements RecipeSerializer<SlicingRecipe> {
        public static final MapCodec<SlicingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("output").forGetter(SlicingRecipe::output),
		        new ValidatingListCodec<>(Ingredient.LIST_CODEC, 6).fieldOf("inputs").forGetter(SlicingRecipe::inputs),
		        Codec.INT.fieldOf("energy").forGetter(SlicingRecipe::energy)
	    ).apply(instance, SlicingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SlicingRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ITEM),
            SlicingRecipe::output,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
            SlicingRecipe::inputs,
            ByteBufCodecs.INT,
            SlicingRecipe::energy,
            SlicingRecipe::new
        );

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
