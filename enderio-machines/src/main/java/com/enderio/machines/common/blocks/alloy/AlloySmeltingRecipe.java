package com.enderio.machines.common.blocks.alloy;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blocks.base.MachineRecipe;
import com.enderio.machines.common.init.MachineRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
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
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public class AlloySmeltingRecipe implements MachineRecipe<AlloySmeltingRecipe.Input> {
    private final List<SizedIngredient> inputs;
    private final ItemStack output;
    private final int energy;
    private final float experience;
    private final boolean isSmelting;

    public AlloySmeltingRecipe(List<SizedIngredient> inputs, ItemStack output, int energy, float experience,
            boolean isSmelting) {
        if (isSmelting && inputs.size() > 1) {
            throw new IllegalArgumentException("More than one smelting ingredient given");
        }

        this.inputs = inputs;
        this.output = output;
        this.energy = energy;
        this.experience = experience;
        this.isSmelting = isSmelting;
    }

    public AlloySmeltingRecipe(List<SizedIngredient> inputs, ItemStack output, int energy, float experience) {
        this(inputs, output, energy, experience, false);
    }

    private AlloySmeltingRecipe(List<SizedIngredient> inputs, ItemStack output, int energy, float experience,
            Optional<Boolean> isSmelting) {
        this(inputs, output, energy, experience, isSmelting.orElse(false));
    }

    public List<SizedIngredient> inputs() {
        return inputs;
    }

    public ItemStack output() {
        return output;
    }

    public int energy() {
        return energy;
    }

    public float experience() {
        return experience;
    }

    public boolean isSmelting() {
        return isSmelting;
    }

    @Override
    public int getBaseEnergyCost() {
        return energy;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY,
                inputs.stream().map(SizedIngredient::ingredient).toArray(Ingredient[]::new));
    }

    @Override
    public boolean matches(Input recipeInput, Level level) {
        if (inputs.isEmpty()) {
            return false;
        }

        // Simpler smelting match logic
        if (isSmelting) {
            int emptyCount = 0;

            for (int i = 0; i < 3; i++) {
                var slotItem = recipeInput.getItem(i);

                if (slotItem.isEmpty()) {
                    emptyCount++;
                    continue;
                }

                if (!inputs.getFirst().test(slotItem)) {
                    return false;
                }
            }

            return emptyCount < 3;
        }

        boolean[] matchedInputs = new boolean[3];

        // Iterate over the slots
        for (int i = 0; i < 3; i++) {
            // Iterate over the inputs
            for (int j = 0; j < 3; j++) {
                // If this ingredient has been matched already, continue
                if (matchedInputs[j]) {
                    continue;
                }

                var slotItem = recipeInput.getItem(i);

                if (j < inputs.size()) {
                    // If we expect an input, test we have a match for it.
                    if (inputs.get(j).test(slotItem)) {
                        matchedInputs[j] = true;
                        break;
                    }
                } else if (slotItem.isEmpty()) {
                    // If we don't expect an input, make sure we have a blank for it.
                    matchedInputs[j] = true;
                    break;
                }
            }
        }

        // If we matched all our ingredients, we win!
        for (int i = 0; i < 3; i++) {
            if (!matchedInputs[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<OutputStack> craft(Input container, RegistryAccess registryAccess) {
        ItemStack outputStack = output.copy();
        if (isSmelting) {
            outputStack.setCount(container.inputsConsumed);
        }
        return List.of(OutputStack.of(outputStack));
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output.copy()));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.ALLOY_SMELTING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.ALLOY_SMELTING.type().get();
    }

    public record Input(List<ItemStack> inputs, int inputsConsumed) implements RecipeInput {

        @Override
        public ItemStack getItem(int slotIndex) {
            if (slotIndex >= inputs.size()) {
                throw new IllegalArgumentException("No item for index " + slotIndex);
            }

            return inputs.get(slotIndex);
        }

        public ItemStack getFirstPopulated() {
            for (ItemStack stack : inputs) {
                if (!stack.isEmpty()) {
                    return stack;
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return inputs.size();
        }

        public Input withInputsConsumed(int inputsConsumed) {
            return new Input(inputs, inputsConsumed);
        }
    }

    public static class Serializer implements RecipeSerializer<AlloySmeltingRecipe> {
        // Uses Optional for isSmelting to avoid polluting recipe generation.
        public static final MapCodec<AlloySmeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
                .group(SizedIngredient.FLAT_CODEC.listOf().fieldOf("inputs").forGetter(AlloySmeltingRecipe::inputs),
                        ItemStack.CODEC.fieldOf("output").forGetter(AlloySmeltingRecipe::output),
                        Codec.INT.fieldOf("energy").forGetter(AlloySmeltingRecipe::energy),
                        Codec.FLOAT.fieldOf("experience").forGetter(AlloySmeltingRecipe::experience),
                        Codec.BOOL.optionalFieldOf("is_smelting")
                                .forGetter(r -> r.isSmelting() ? Optional.of(r.isSmelting()) : Optional.empty()))
                .apply(inst, AlloySmeltingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AlloySmeltingRecipe> STREAM_CODEC = StreamCodec
                .composite(SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), AlloySmeltingRecipe::inputs,
                        ItemStack.STREAM_CODEC, AlloySmeltingRecipe::output, ByteBufCodecs.INT,
                        AlloySmeltingRecipe::energy, ByteBufCodecs.FLOAT, AlloySmeltingRecipe::experience,
                        ByteBufCodecs.BOOL, AlloySmeltingRecipe::isSmelting, AlloySmeltingRecipe::new);

        @Override
        public MapCodec<AlloySmeltingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlloySmeltingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
