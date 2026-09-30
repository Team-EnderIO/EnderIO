package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.filter.ResourceFilter;
import mekanism.api.chemical.merged.BoxedChemicalStack;

import java.util.function.Predicate;

public interface ChemicalStackFilter extends ResourceFilter, Predicate<BoxedChemicalStack> {
}