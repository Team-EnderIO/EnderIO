//package com.enderio.api.recipe;
//
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.Container;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//// TODO: 27/05/2022 Revert this abstract pattern, i dont know where I picked it up but I think it just confuses things more.
//public abstract class AlloySmeltingRecipe implements IMachineRecipe<AlloySmeltingRecipe, Container> {
//    private final ResourceLocation id;
//    private final List<CountedIngredient> inputs;
//    private final ItemStack result;
//    private final int energy;
//    private final float experience;
//
//    public AlloySmeltingRecipe(ResourceLocation id, List<CountedIngredient> inputs, ItemStack result, int energy, float experience) {
//        if (inputs.size() > 3) {
//            throw new IllegalArgumentException("Tried to create an invalid alloy smelting recipe!");
//        }
//
//        this.id = id;
//        this.inputs = inputs;
//        this.result = result;
//        this.energy = energy;
//        this.experience = experience;
//    }
//
//    @Override
//    public ResourceLocation getId() {
//        return this.id;
//    }
//
//    public List<CountedIngredient> getInputs() {
//        return inputs;
//    }
//
//    @Override
//    public int getEnergyCost() {
//        return energy;
//    }
//
//    public float getExperience() {
//        return experience;
//    }
//
//    // region Crafting
//
//    @Override
//    public boolean matches(Container pContainer, Level pLevel) {
//        boolean[] matched = new boolean[3];
//
//        // Iterate over the slots
//        for (int i = 0; i < 3; i++) {
//
//            // Iterate over the inputs
//            for (int j = 0; j < 3; j++) {
//
//                // If this ingredient has been matched already, continue
//                if (matched[j])
//                    continue;
//
//                if (j < inputs.size()) {
//                    // If we expect an input, test we have a match for it.
//                    if (inputs.get(j).test(pContainer.getItem(i))) {
//                        matched[j] = true;
//                    }
//                } else if (pContainer.getItem(i) == ItemStack.EMPTY) {
//                    // If we don't expect an input, make sure we have a blank for it.
//                    matched[j] = true;
//                }
//            }
//
//        }
//
//        // If we matched all our ingredients, we win!
//        for (int i = 0; i < 3; i++) {
//            if (!matched[i])
//                return false;
//        }
//
//        return true;
//    }
//
//    @Override
//    public void consumeInputs(Container container) {
//        // Track which ingredients have been consumed
//        boolean[] consumed = new boolean[3];
//
//        // Iterate over the slots
//        for (int i = 0; i < 3; i++) {
//
//            // Iterate over the inputs
//            for (int j = 0; j < 3; j++) {
//
//                // If this ingredient has been matched already, continue
//                if (consumed[j])
//                    continue;
//
//                if (j < inputs.size()) {
//                    // If we expect an input, test we have a match for it.
//                    CountedIngredient input = inputs.get(j);
//
//                    if (input.test(container.getItem(i))) {
//                        consumed[j] = true;
//                        container.removeItem(i, input.count());
//                    }
//                } else if (container.getItem(i) == ItemStack.EMPTY) {
//                    // If we don't expect an input, make sure we have a blank for it.
//                    consumed[j] = true;
//                }
//            }
//        }
//    }
//
//    @Override
//    public List<ItemStack> craft(Container container) {
//        return List.of(result.copy());
//    }
//
//    @Override
//    public int getOutputCount(Container container) {
//        return 1;
//    }
//
//    // endregion
//
//    // region JEI Helpers
//
//    @Override
//    public List<List<ItemStack>> getAllInputs() {
//        List<List<ItemStack>> inputs = new ArrayList<>();
//        for (CountedIngredient ingredient : this.inputs) {
//            inputs.add(Arrays.stream(ingredient.getItems()).toList());
//        }
//        return inputs;
//    }
//
//    @Override
//    public List<ItemStack> getAllOutputs() {
//        return List.of(result);
//    }
//
//    // endregion
//
//}
