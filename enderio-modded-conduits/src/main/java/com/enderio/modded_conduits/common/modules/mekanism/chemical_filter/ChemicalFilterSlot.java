package com.enderio.modded_conduits.common.modules.mekanism.chemical_filter;

import com.enderio.enderio.content.filters.FilterSlot;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ChemicalFilterSlot extends FilterSlot<ChemicalStack> {

    public ChemicalFilterSlot(Supplier<ChemicalStack> getter, Consumer<ChemicalStack> setter, int pSlot, int pX, int pY) {
        super(getter, setter, pSlot, pX, pY);
    }

    @Override
    protected ChemicalStack emptyResource() {
        return ChemicalStack.EMPTY;
    }

    @Override
    public Optional<ChemicalStack> getResourceFrom(ItemStack itemStack) {
        IChemicalHandler capability = itemStack.getCapability(MekanismModule.Capabilities.Item.CHEMICAL);
        if (capability != null) {
            var ghost = capability.getChemicalInTank(0).copy();
            if (!ghost.isEmpty()) {
                return Optional.of(ghost);
            }
        }

        return Optional.empty();
    }
}
