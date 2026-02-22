package com.enderio.enderio.foundation.util;

import com.enderio.enderio.EnderIO;
import net.minecraft.core.Holder;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

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
    public static List<Holder<Fluid>> getFluidStacksInPreferredOrder(SizedFluidIngredient ingredient) {
        List<Holder<Fluid>> fluids = ingredient.ingredient().fluids();
        List<Holder<Fluid>> preferred = new ArrayList<>(fluids.size());
        List<Holder<Fluid>> others = new ArrayList<>(fluids.size());
        
        for (Holder<Fluid> fluid : fluids) {
            if (fluid.getKey().identifier().getNamespace().equals(EnderIO.MOD_ID)) {
                preferred.add(fluid);
            } else {
                others.add(fluid);
            }
        }

        preferred.addAll(others);
        return preferred;
    }
}
