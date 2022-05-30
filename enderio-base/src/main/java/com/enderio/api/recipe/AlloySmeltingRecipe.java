package com.enderio.api.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AlloySmeltingRecipe implements IMachineRecipe<AlloySmeltingRecipe, Container> {
    private final ResourceLocation id;
    private final List<CountedIngredient> inputs;
    private final ItemStack result;
    private final int energy;
    private final float experience;

    public AlloySmeltingRecipe(ResourceLocation id, List<CountedIngredient> inputs, ItemStack result, int energy, float experience) {
        if (inputs.size() > 3) {
            throw new IllegalArgumentException("Tried to create an invalid alloy smelting recipe!");
        }

        this.id = id;
        this.inputs = inputs;
        this.result = result;
        this.energy = energy;
        this.experience = experience;
    }

    // TODO: Need a better solution to this.
    public List<CountedIngredient> getInputs() {
        return inputs;
    }

    public ItemStack consumeInput(ItemStack input) {
        // We allow empty slots
        if (input.isEmpty())
            return input;

        // Try to work out which ingredient this is
        for (CountedIngredient ingredient : inputs) {
            if (ingredient.test(input)) {
                input.shrink(ingredient.count());
                return input;
            }
        }

        throw new RuntimeException("Tried to consume an invalid input. A recipe match check must not have been performed!");
    }

    @Override
    public List<List<ItemStack>> getAllInputs() {
        List<List<ItemStack>> inputs = new ArrayList<>();
        for (CountedIngredient ingredient : this.inputs) {
            inputs.add(Arrays.stream(ingredient.getItems()).toList());
        }
        return inputs;
    }

    @Override
    public List<ItemStack> getAllOutputs() {
        return List.of(result);
    }

    @Override
    public int getEnergyCost() {
        return energy;
    }

    public float getExperience() {
        return experience;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        boolean[] matched = new boolean[3];

        // Iterate over the slots
        for (int i = 0; i < 3; i++) {

            // Iterate over the inputs
            for (int j = 0; j < 3; j++) {

                // If this ingredient has been matched already, continue
                if (matched[j])
                    continue;

                if (j < inputs.size()) {
                    // If we expect an input, test we have a match for it.
                    if (inputs.get(j).test(pContainer.getItem(i))) {
                        matched[j] = true;
                    }
                } else if (pContainer.getItem(i) == ItemStack.EMPTY) {
                    // If we don't expect an input, make sure we have a blank for it.
                    matched[j] = true;
                }
            }

        }

        // If we matched all our ingredients, we win!
        for (int i = 0; i < 3; i++) {
            if (!matched[i])
                return false;
        }

        return true;
    }

    @Override
    public ItemStack assemble(Container pContainer) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return result.copy();
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }
}
