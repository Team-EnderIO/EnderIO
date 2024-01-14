package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.utility.ValidatingListCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SlicingRecipe implements MachineRecipe<Container> {
    final Item output;
    final List<Ingredient> inputs;
    final int energy;

    public SlicingRecipe(Item output, List<Ingredient> inputs, int energy) {
        this.output = output;
        this.inputs = inputs;
        this.energy = energy;
    }

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

    public List<Ingredient> getInputs() {
        return List.copyOf(inputs);
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
        private static final Codec<SlicingRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("output").forGetter(slicingRecipe -> slicingRecipe.output),
		        new ValidatingListCodec<>(Ingredient.LIST_CODEC, 6).fieldOf("inputs").forGetter(slicingRecipe -> slicingRecipe.inputs),
		        Codec.INT.fieldOf("energy").forGetter(slicingRecipe -> slicingRecipe.energy)
	    ).apply(instance, SlicingRecipe::new));

        @Override
        public Codec<SlicingRecipe> codec() {
            return CODEC;
        }

        @Override
        @Nullable
        public SlicingRecipe fromNetwork(FriendlyByteBuf buffer) {
            try {
                ResourceLocation outputId = buffer.readResourceLocation();
                Item output = BuiltInRegistries.ITEM.get(outputId);
                List<Ingredient> inputs = buffer.readCollection(ArrayList::new, Ingredient::fromNetwork);

                int energy = buffer.readInt();

                return new SlicingRecipe(output, inputs, energy);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error reading slicing recipe from packet.", ex);
                return null;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SlicingRecipe recipe) {
            try {
                buffer.writeResourceLocation(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(recipe.output)));
                buffer.writeCollection(recipe.inputs, (buf, ing) -> ing.toNetwork(buf));
                buffer.writeInt(recipe.energy);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing slicing recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
