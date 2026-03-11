package com.enderio.enderio.foundation.util;

import com.enderio.enderio.EnderIO;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper utilities for working with SizedFluidIngredient.
 * Provides ordering and conversion methods that prefer EnderIO fluids over others.
 */
public class SizedFluidIngredientHelper {
    
    /**
     * Gets all possible fluid stacks from the ingredient in preferred order.
     * EnderIO fluids are prioritized first, then other mod fluids.
     * The returned stacks maintain their original amounts.
     * 
     * @param ingredient The sized fluid ingredient
     * @return List of fluid stacks in preferred order
     */
    public static List<FluidStack> getFluidStacksInPreferredOrder(SizedFluidIngredient ingredient) {
        FluidStack[] stacks = ingredient.getFluids();
        List<FluidStack> preferred = new ArrayList<>(stacks.length);
        List<FluidStack> others = new ArrayList<>(stacks.length);
        
        for (FluidStack stack : stacks) {
            if (stack.getFluid().builtInRegistryHolder().getKey().location().getNamespace().equals(EnderIO.MOD_ID)) {
                preferred.add(stack);
            } else {
                others.add(stack);
            }
        }

        preferred.addAll(others);
        return preferred;
    }
}
