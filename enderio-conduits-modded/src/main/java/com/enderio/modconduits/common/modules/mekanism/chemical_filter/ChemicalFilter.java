package com.enderio.modconduits.common.modules.mekanism.chemical_filter;

import com.enderio.base.api.filter.ResourceFilter;
import java.util.function.Predicate;
import mekanism.api.chemical.ChemicalStack;

public interface ChemicalFilter extends ResourceFilter, Predicate<ChemicalStack> {

}
