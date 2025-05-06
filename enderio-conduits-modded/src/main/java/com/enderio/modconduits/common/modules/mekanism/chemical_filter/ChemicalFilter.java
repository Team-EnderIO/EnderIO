package com.enderio.modconduits.common.modules.mekanism.chemical_filter;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import org.jetbrains.annotations.Nullable;

public interface ChemicalFilter {
    /**
     * Test whether the fluid stack passes this filter.
     * @param target The target handler which can be used for comparisons using the destination before moving.
     * @param stack The stack being tested.
     * @return The stack that is permitted (can differ in amount), use {@link ChemicalStack#EMPTY} to transfer nothing.
     */
    ChemicalStack test(@Nullable IChemicalHandler target, ChemicalStack stack);
}
