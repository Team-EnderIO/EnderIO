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
        // TODO: Test
        boolean[] matchArray = new boolean[3]; // Used to ensure there are blank slots left. // TODO: I want to get rid of it
        int matches = 0;

        for (int i = 0; i < 3; i++) {
            if (matchArray[i])
                continue;

            for (int j = 0; j < 3; j++) {
                if (j < inputs.size()) {
                    if (inputs.get(j).test(pContainer.getItem(i))) {
                        matchArray[i] = true;
                        matches++;
                    }
                } else if (pContainer.getItem(i).isEmpty())
                    matchArray[i] = true;
            }

            for (CountedIngredient ingredient : inputs) {
                if (ingredient.test(pContainer.getItem(i)))
                    matchArray[i] = true;
                else if (ingredient == CountedIngredient.EMPTY && pContainer.getItem(i).isEmpty())
                    matchArray[i] = true;
            }
        }

        return matches == inputs.size() && matchArray[0] && matchArray[1] && matchArray[2];
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