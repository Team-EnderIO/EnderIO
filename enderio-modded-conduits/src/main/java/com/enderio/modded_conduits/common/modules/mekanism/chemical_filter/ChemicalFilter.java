package com.enderio.modded_conduits.common.modules.mekanism.chemical_filter;

import mekanism.api.chemical.ChemicalResource;
import mekanism.api.chemical.ChemicalStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import org.jspecify.annotations.Nullable;

public interface ChemicalFilter {
    /**
     * Test whether the chemical stack passes this filter.
     * @param target The target handler which can be used for comparisons using the destination before moving.
     * @param stack The stack being tested.
     * @return The stack that is permitted (can differ in amount), use {@link ChemicalStack#EMPTY} to transfer nothing.
     */
    ChemicalStack test(@Nullable ResourceHandler<ChemicalResource> target, ChemicalStack stack);
}
