package com.enderio.modconduits.mods.mekanism;

import com.enderio.base.api.filter.ResourceFilter;
import mekanism.api.chemical.merged.BoxedChemicalStack;

import java.util.function.Predicate;

public interface ChemicalFilter extends ResourceFilter, Predicate<BoxedChemicalStack> {

}
