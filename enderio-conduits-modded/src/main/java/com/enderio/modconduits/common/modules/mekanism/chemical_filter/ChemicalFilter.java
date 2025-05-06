package com.enderio.modconduits.common.modules.mekanism.chemical_filter;

import com.enderio.base.api.filter.ResourceFilter;
import mekanism.api.chemical.ChemicalStack;

import java.util.function.Predicate;

public interface ChemicalFilter extends ResourceFilter, Predicate<ChemicalStack> {

}
